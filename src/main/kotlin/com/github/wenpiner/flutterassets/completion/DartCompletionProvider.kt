package com.github.wenpiner.flutterassets.completion

import com.github.wenpiner.flutterassets.util.findPubspecYamlFile
import com.github.wenpiner.flutterassets.util.getFlutterAssets
import com.github.wenpiner.flutterassets.util.readInputStream
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionUtilCore.DUMMY_IDENTIFIER
import com.intellij.codeInsight.completion.PlainPrefixMatcher
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.progress.ProgressManager
import com.intellij.refactoring.suggested.startOffset
import com.intellij.util.ProcessingContext
import io.ktor.utils.io.core.*
import kotlin.io.use

class DartCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        ProgressManager.checkCanceled()
        val caretPosition = parameters.position.text.indexOf(DUMMY_IDENTIFIER)
        val prefix = parameters.position.text.substring(0, caretPosition)
        findPubspecYamlFile(parameters.position.project, parameters.originalFile.virtualFile)?.let {
            val assets = it.inputStream.use { inputStream ->
                readInputStream(inputStream)
            }
            val flutterAssets = getFlutterAssets(assets)
            var r = result.withPrefixMatcher(PlainPrefixMatcher(prefix, false)).caseInsensitive()
            for (flutterAsset in flutterAssets) {
                ProgressManager.checkCanceled();
                if (flutterAsset.startsWith(prefix)) {
                    result.addElement(LookupElementBuilder.create(flutterAsset))
                }
            }
        }
    }
}