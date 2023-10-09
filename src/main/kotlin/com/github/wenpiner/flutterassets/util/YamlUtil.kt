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
        val reader = InputStreamReader(inputStream)
        val buffer = CharArray(1024)
        var read: Int
        var line = StringBuilder()
        while (reader.read(buffer).also { read = it } != -1) {
            for (i in 0 until read) {
                val c = buffer[i]
                if (c == '\n') {
                    line = if (line.isEmpty()) {
                        lines.add(System.lineSeparator())
                        StringBuilder()
                    } else {
                        lines.add(line.toString())
                        StringBuilder()
                    }
                } else {
                    line.append(c)
                }
            }
        }
        if (line.isNotEmpty()) {
            lines.add(line.toString())
        }
    } catch (e: IOException) {
        return emptyList()
    }
    return lines
}

fun getFlutterAssets(oldTexts: List<String>): List<String> {
    var flutterBool = false;
    var assetsBool = false;
    val assets = mutableListOf<String>()
    for (oldText in oldTexts) {
        if (!flutterBool && oldText.startsWith("flutter:")) {
            flutterBool = true;
            continue
        }
        if (!assetsBool && oldText.trim().contains("assets:") && flutterBool) {
            assetsBool = true;
            continue
        }
        if (flutterBool && assetsBool) {
            if (oldText.trim().startsWith("-")) {
                assets.add(oldText.trim().substring(1).trim())
            } else {
                break
            }
        }
    }
    return assets
}

fun String.clearText(): String {
    if (this == System.lineSeparator()) {
        return System.lineSeparator()
    }
    return "$this${System.lineSeparator()}"
}

fun writeStrings(oldTexts: List<String>, newItems: List<String>): List<String> {
    val newTexts = mutableListOf<String>()
    var flutterBool = false;
    var assetsBool = false;
    var write = false;
    var old = false;
    for (oldText in oldTexts) {
        if (old) {
            newTexts.add(oldText.clearText())
            continue
        }
        if (!flutterBool && oldText.startsWith("flutter:")) {
            flutterBool = true;
            newTexts.add("$oldText${System.lineSeparator()}")
            continue
        }
        if (!assetsBool && oldText.trim().contains("assets:") && flutterBool) {
            assetsBool = true;
            newTexts.add("$oldText${System.lineSeparator()}")
            continue
        }
        if (!assetsBool && !flutterBool) {
            newTexts.add(oldText.clearText())
            continue
        } else {
            if (!write) {
                for (newItem in newItems) {
                    newTexts.add("    - $newItem${System.lineSeparator()}")
                }
                write = true
            } else {
                if (!oldText.trim().startsWith("-")) {
                    newTexts.add(oldText.clearText())
                    old = true
                    continue
                }
            }
        }
    }
    if (!flutterBool) {
        newTexts.add("flutter: ${System.lineSeparator()}")
    }
    if (!assetsBool) {
        newTexts.add("  assets: ${System.lineSeparator()}")
        for (newItem in newItems) {
            newTexts.add("    - $newItem${System.lineSeparator()}")
        }
    }
    return newTexts
}