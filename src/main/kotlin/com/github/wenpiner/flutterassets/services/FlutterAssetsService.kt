package com.github.wenpiner.flutterassets.services

import com.github.wenpiner.flutterassets.util.readInputStream
import com.github.wenpiner.flutterassets.util.writeStrings
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import org.apache.tools.ant.filters.StringInputStream
import java.io.*
import java.nio.file.Paths


@Service(Service.Level.PROJECT)
class FlutterAssetsService(private var project: Project) {

    fun performLongRunningOperation(): Boolean {

        val projectPath = project.basePath ?: return false
        val pubspecPath = Paths.get(projectPath, "assets")
        val task = MyTask(project = project, relativePath = pubspecPath.toString())
        ProgressManager.getInstance().runProcessWithProgressSynchronously(task, "Flutter Sync", false, project)
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
        val findFileByIoFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(pubspec)
        findFileByIoFile ?: return
        findFileByIoFile.refresh(false, true)
        WriteCommandAction.runWriteCommandAction(project, object : Runnable {
            override fun run() {
                val outputStream = findFileByIoFile.getOutputStream("FlutterAssetsService")
                // 根据inputStream获取所有行
                val inputStream = findFileByIoFile.inputStream
                val allLines = inputStream.use {
                    readInputStream(inputStream)
                }
                val writeStrings = writeStrings(allLines, flutterAssets)

                outputStream.use { it ->
                    val str = writeStrings.joinToString("")
                    it.write(str.toByteArray())
                    it.flush()
                }
                findFileByIoFile.refresh(true, true)
            }
        });

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