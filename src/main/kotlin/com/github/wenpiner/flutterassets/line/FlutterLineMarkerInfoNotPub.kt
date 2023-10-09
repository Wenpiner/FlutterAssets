package com.github.wenpiner.flutterassets.line

import com.github.wenpiner.flutterassets.services.FlutterAssetsService
import com.github.wenpiner.flutterassets.util.getPath
import com.github.wenpiner.flutterassets.util.toIcon
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.navigation.actions.ClickLinkAction
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.util.Function
import icons.DartIcons
import java.awt.event.MouseEvent
import java.nio.file.Paths
import javax.swing.Icon

class FlutterLineMarkerInfoNotPub(element: PsiElement, range: TextRange) :
    LineMarkerInfo<PsiElement>(
        element, range, com.intellij.icons.AllIcons.General.Warning,
        Function { "Flutter Assets Sync" }, object :
            GutterIconNavigationHandler<PsiElement> {
            override fun navigate(p0: MouseEvent?, p1: PsiElement?) {

            }
        }, GutterIconRenderer.Alignment.CENTER,
        { "Flutter Assets Sync" }
    ) {
    override fun createGutterRenderer(): GutterIconRenderer {
        return object : LineMarkerGutterIconRenderer<PsiElement>(this) {
            override fun getClickAction(): AnAction {
                return object : AnAction() {
                    override fun actionPerformed(p0: AnActionEvent) {
                        if (p0.project == null) {
                            return
                        }
                        val service = p0.project!!.service<FlutterAssetsService>()
                        ProgressManager.getInstance().runProcessWithProgressSynchronously(
                            {
                                service.performLongRunningOperation()
                            },
                            "Start Flutter Sync",
                            false,
                            p0.project!!
                        )
                    }
                }
            }
        }
    }
}