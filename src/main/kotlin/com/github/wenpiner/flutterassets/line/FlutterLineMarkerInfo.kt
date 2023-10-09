package com.github.wenpiner.flutterassets.line

import com.github.wenpiner.flutterassets.util.getPath
import com.github.wenpiner.flutterassets.util.getPsiFile
import com.github.wenpiner.flutterassets.util.toIcon
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.navigation.actions.ClickLinkAction
import com.intellij.codeInsight.navigation.openFileWithPsiElement
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.util.Function
import icons.DartIcons
import java.nio.file.Paths
import javax.swing.Icon

class FlutterLineMarkerInfo(element: PsiElement, range: TextRange, icon: Icon) :
    LineMarkerInfo<PsiElement>(
        element, range, icon,
        Function { "Preview file" }, null, GutterIconRenderer.Alignment.CENTER,
        { "Preview file" }
    ) {
    override fun createGutterRenderer(): GutterIconRenderer {
        return object : LineMarkerGutterIconRenderer<PsiElement>(this) {
            override fun getClickAction(): AnAction? {
                return object : AnAction() {
                    override fun actionPerformed(p0: AnActionEvent) {
                        if (p0.project == null) {
                            return
                        }
                        val text = element?.text?.getPath(p0.project!!) ?: return
                        val psiFile = text.getPsiFile(p0.project!!) ?: return
                        openFileWithPsiElement(psiFile, true, requestFocus = true)
                    }
                }
            }
        }
    }
}