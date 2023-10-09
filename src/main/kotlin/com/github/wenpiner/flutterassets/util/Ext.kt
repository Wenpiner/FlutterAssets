package com.github.wenpiner.flutterassets.util

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.ui.IconManager
import java.nio.file.Paths
import javax.swing.Icon

fun String.getPath(project: Project): VirtualFile? {
    val text = this.subSequence(1, this.length - 1).toString()
    val basePath = project.basePath
    if (basePath != null) {
        val fullPath = Paths.get(basePath, text)
        return LocalFileSystem.getInstance().findFileByPath(fullPath.toString())
    }
    return null
}

fun VirtualFile.getPsiFile(project: Project): PsiFile? {
    return PsiManager.getInstance(project).findFile(this)
}
