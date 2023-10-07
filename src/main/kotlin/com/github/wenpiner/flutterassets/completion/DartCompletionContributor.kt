package com.github.wenpiner.flutterassets.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.openapi.project.DumbAware
import com.intellij.patterns.PlatformPatterns
import com.jetbrains.lang.dart.DartTokenTypes

class DartCompletionContributor : CompletionContributor(), DumbAware {
    init {
        // 在这里添加自定义的CompletionProvider
        extend(CompletionType.BASIC, PlatformPatterns.
            and(PlatformPatterns.psiElement(DartTokenTypes.REGULAR_STRING_PART),PlatformPatterns.not(PlatformPatterns.psiElement(DartTokenTypes.REGULAR_STRING_PART).inside(PlatformPatterns.psiElement(DartTokenTypes.IMPORT_STATEMENT))))
            , DartCompletionProvider())
    }
}