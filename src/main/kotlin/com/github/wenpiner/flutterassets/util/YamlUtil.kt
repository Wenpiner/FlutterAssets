package com.github.wenpiner.flutterassets.util

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.lang.dart.util.PubspecYamlUtil
import org.jetbrains.annotations.NotNull
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader


fun findPubspecYamlFile(@NotNull project: Project, @NotNull currentFile: VirtualFile): VirtualFile? {
    return PubspecYamlUtil.findPubspecYamlFile(project, currentFile)
}

fun readInputStream(inputStream: InputStream): List<String> {
    val lines: MutableList<String> = ArrayList()
    try {
        BufferedReader(InputStreamReader(inputStream)).use { reader ->
            var line: String?
            while (!reader.readLine().also { line = it }.isNullOrBlank()) {
                lines.add(line ?: "")
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return lines
}

fun getFlutterAssets(oldTexts: List<String>): List<String> {
    var flutterBool = false;
    var assetsBool = false;
    val assets = mutableListOf<String>()
    for (oldText in oldTexts) {
        // 判断当前是否为Flutter节点
        if (!flutterBool && oldText.startsWith("flutter:")) {
            flutterBool = true;
            continue
        }
        // 判断当前是否为assets节点
        if (!assetsBool && oldText.trim().contains("assets:") && flutterBool) {
            assetsBool = true;
            continue
        }
        // 如果找到assets节点
        if (flutterBool && assetsBool) {
            // 写入新的数据
            if (oldText.trim().startsWith("-")) {
                assets.add(oldText.trim().substring(1).trim())
            }
        }
    }
    return assets
}

fun writeStrings(oldTexts: List<String>, newItems: List<String>): List<String> {
    // 新的数据
    val newTexts = mutableListOf<String>()

    // 是否找到flutter节点
    var flutterBool = false;
    // 是否找到assets节点
    var assetsBool = false;
    var write = false;
    for (oldText in oldTexts) {
        // 判断当前是否为Flutter节点
        if (!flutterBool && oldText.startsWith("flutter:")) {
            flutterBool = true;
            newTexts.add("$oldText${System.lineSeparator()}")
            continue
        }
        // 判断当前是否为assets节点
        if (!assetsBool && oldText.trim().contains("assets:") && flutterBool) {
            assetsBool = true;
            newTexts.add("$oldText${System.lineSeparator()}")
            continue
        }

        // 未找到Assets节点则直接写入注释
        if (!assetsBool && !flutterBool && oldText.trim().startsWith("#")) {
            newTexts.add("$oldText${System.lineSeparator()}")
            continue
        }

        // 如果找到assets节点
        if (flutterBool && assetsBool && !write) {
            // 写入新的数据
            for (newItem in newItems) {
                newTexts.add("    - $newItem${System.lineSeparator()}")
            }
            write = true
        } else {
            newTexts.add("$oldText${System.lineSeparator()}")
        }
    }
    // 如果未找到flutter节点，则直接写入
    if (!flutterBool) {
        newTexts.add("flutter: ${System.lineSeparator()}")
    }
    if (!assetsBool) {
        newTexts.add("  assets: ${System.lineSeparator()}")
        // 写入新的数据
        for (newItem in newItems) {
            newTexts.add("    - $newItem${System.lineSeparator()}")
        }
    }
    return newTexts
}