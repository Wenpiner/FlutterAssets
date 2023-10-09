package com.github.wenpiner.flutterassets.util

import com.intellij.icons.AllIcons
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.IconUtil
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.Icon

private val ICON_CACHE: MutableMap<String, Icon> = HashMap()
fun VirtualFile.icon(cacheKey: String): Icon {
    val icon = ICON_CACHE[cacheKey]
    if (icon != null) {
        return icon
    }
    try {
        val iconImage = ImageIO.read(File(this.path))
        return iconImage.toIcon(cacheKey)
    } catch (e: Exception) {
        return AllIcons.General.Warning
    }
}

fun VirtualFile?.getCacheKey(): String {
    if (this == null) {
        return ""
    }
    return "${this.path}${this.timeStamp}"
}

fun BufferedImage.toIcon(cacheKey: String): Icon {
    val image = TransformImage.resizeAspectFitCenter(this, 32, 32)
    val createImageIcon = IconUtil.createImageIcon(image)
    if (cacheKey.isNotEmpty()) {
        ICON_CACHE[cacheKey] = createImageIcon
    }
    return createImageIcon
}