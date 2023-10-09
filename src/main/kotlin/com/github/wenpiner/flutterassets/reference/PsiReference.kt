package com.github.wenpiner.flutterassets.reference

import com.intellij.codeInsight.navigation.openFileWithPsiElement
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.*
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.PsiIconUtil
import java.nio.file.Paths
import kotlin.reflect.jvm.internal.impl.descriptors.CallableDescriptor.UserDataKey

class PsiReference(element: PsiElement, soft: Boolean) : PsiReferenceBase<PsiElement>(element, soft) {
    override fun resolve(): PsiElement? {
        val text = element.text.subSequence(1, element.text.length - 1).toString()
        val project = element.project
        val basePath = project.basePath
        if (basePath != null) {
            val fullPath = Paths.get(basePath, text)
            LocalFileSystem.getInstance().findFileByPath(fullPath.toString())?.let {
                return PsiManager.getInstance(project).findFile(it)
            }
        }
        return null
    }

    override fun getVariants(): Array<Any> {
        return super.getVariants()
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        return super.handleElementRename(newElementName)
    }
}