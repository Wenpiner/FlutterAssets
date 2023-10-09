package com.github.wenpiner.flutterassets.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import com.jetbrains.lang.dart.psi.DartStringLiteralExpression

class FlutterReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, p1: ProcessingContext): Array<PsiReference> {
        if (element is DartStringLiteralExpression) {
            return arrayOf(PsiReference(element, false))
        }
        return emptyArray()
    }
}