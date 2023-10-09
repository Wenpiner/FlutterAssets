package com.github.wenpiner.flutterassets.line

import com.github.wenpiner.flutterassets.util.*
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.FunctionUtil
import com.jetbrains.lang.dart.psi.DartLiteralExpression


class FlutterAssetsLineMarkerProvider : LineMarkerProvider {
    override fun getLineMarkerInfo(p0: PsiElement): LineMarkerInfo<*>? {
        return null
    }


    override fun collectSlowLineMarkers(
        elements: MutableList<out PsiElement>,
        result: MutableCollection<in LineMarkerInfo<*>>
    ) {

        for (element in elements) {

            findPubspecYamlFile(element.project, element.containingFile.virtualFile)?.let {
                val assets = it.inputStream.use { inputStream ->
                    readInputStream(inputStream)
                }
                val flutterAssets = getFlutterAssets(assets)
                val text = element.text
                if (((text.startsWith("\"") && text.endsWith("\"")) || (text.startsWith("'") && text.endsWith("'"))) && text.length > 2) {
                    val asset = text.substring(1, text.length - 1)
                    if (flutterAssets.contains(asset)) {
                        println("collectSlowLineMarkers:${element.text}")
                        val filePath = element.text.getPath(element.project)
                        val icon = filePath?.icon(filePath.getCacheKey()) ?: AllIcons.General.Warning
                        result.add(FlutterLineMarkerInfo(element, element.textRange, icon))
                    } else {
                        text.getPath(element.project)?.exists().takeIf { it == true }?.let {
                            result.add(
                                FlutterLineMarkerInfoNotPub(
                                    element,
                                    element.textRange
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}