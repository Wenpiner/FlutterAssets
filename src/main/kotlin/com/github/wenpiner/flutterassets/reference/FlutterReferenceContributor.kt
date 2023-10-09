package com.github.wenpiner.flutterassets.reference

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import com.jetbrains.lang.dart.DartTokenTypes

class FlutterReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(p0: PsiReferenceRegistrar) {
        p0.registerReferenceProvider(
            PlatformPatterns.psiElement(DartTokenTypes.STRING_LITERAL_EXPRESSION), FlutterReferenceProvider()
        )
    }
}