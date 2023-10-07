package com.github.wenpiner.flutterassets.actionSystem

import com.github.wenpiner.flutterassets.services.FlutterAssetsService
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.service
import com.intellij.openapi.progress.ProgressManager
import java.io.File


class FlutterAssetsAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        if (e.project == null) {
            return
        }
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        val path = virtualFile.path

        val service = e.project!!.service<FlutterAssetsService>()
        ProgressManager.getInstance().runProcessWithProgressSynchronously(
            { service.performLongRunningOperation(path) },
            "Flutter sync",
            false,
            e.project
        )
    }

    override fun update(e: AnActionEvent) {
        if (e.project == null) {
            e.presentation.isEnabledAndVisible = false
            return
        }

        // 获取项目目录
        val projectPath = e.project!!.basePath ?: return
        // 判断是否存在pubspec.yaml
        val pubspec = File("$projectPath/pubspec.yaml")
        if (!pubspec.exists() || !pubspec.isFile) {
            e.presentation.isEnabledAndVisible = false
            return
        }
        e.presentation.isEnabledAndVisible = true
    }


    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }
}