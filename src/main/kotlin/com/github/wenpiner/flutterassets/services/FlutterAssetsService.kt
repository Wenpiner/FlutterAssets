package com.github.wenpiner.flutterassets.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.io.File


@Service(Service.Level.PROJECT)
class FlutterAssetsService(private var project: Project) {

    fun performLongRunningOperation(relativePath: String): Boolean {
        val task = MyTask(project = project, relativePath = relativePath)
        ProgressManager.getInstance().runProcessWithProgressSynchronously(task, "Flutter sync", false, project)
        return task.getResult()
    }


}

class MyTask(private var project: Project, private var relativePath: String) : Runnable {
    private var result = false
    override fun run() {
        val flutterAssets = getFlutterAssets(project.basePath!!, relativePath)
        val pubspec = File(project.basePath, "pubspec.yaml")
        if (!pubspec.exists() || !pubspec.isFile) {
            result = false
            return
        }
        val options = DumperOptions()
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
        options.indent = 2
        options.indicatorIndent = 2
        options.indentWithIndicator = true

        val yaml = Yaml(options)
        val yamlMap: MutableMap<String, Any> = yaml.load(pubspec.readText())

        val flutterKey = yamlMap.containsKey("flutter")
        val flutter = yamlMap["flutter"];
        if (!flutterKey || flutter == null) {
            yamlMap["flutter"] = mapOf("assets" to flutterAssets)
        } else {
            val tempMap = flutter as MutableMap<Any, Any>
            tempMap["assets"] = flutterAssets
        }
        yaml.dump(yamlMap, pubspec.writer())
        result = true
    }

    fun getResult(): Boolean {
        return result;
    }

    private fun getFlutterAssets(projectPath: String, relativePath: String): List<String> {
        // 遍历获取目录下的所有文件和文件夹，并拼接成string
        val flutterAssets = mutableListOf<String>()
        val file = File(relativePath)
        val files = file.listFiles()
        files?.forEach {
            if (it.isDirectory) {
                flutterAssets.addAll(getFlutterAssets(projectPath, it.path))
            } else {
                flutterAssets.add(it.path.replace("$projectPath/", ""))
            }
        }
        return flutterAssets
    }
}