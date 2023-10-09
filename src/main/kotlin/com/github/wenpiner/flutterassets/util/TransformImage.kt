package com.github.wenpiner.flutterassets.util

import com.intellij.util.ui.ImageUtil
import java.awt.*
import java.awt.image.BufferedImage


object TransformImage {
    private fun getAspectRatioRect(imgSize: Dimension, boundary: Dimension): Rectangle {
        val original_width = imgSize.width
        val original_height = imgSize.height
        val bound_width = boundary.width
        val bound_height = boundary.height
        var new_width = original_width
        var new_height = original_height

        // first check if we need to scale width
        if (original_width > bound_width) {
            //scale width to fit
            new_width = bound_width
            //scale height to maintain aspect ratio
            new_height = new_width * original_height / original_width
        }

        // then check if we need to scale even with the new height
        if (new_height > bound_height) {
            //scale height to fit instead
            new_height = bound_height
            //scale width to maintain aspect ratio
            new_width = new_height * original_width / original_height
        }
        return Rectangle((bound_width - new_width) / 2, (bound_height - new_height) / 2, new_width, new_height)
    }

    fun resizeAspectFitCenter(originalImage: BufferedImage, boundWidth: Int, boundHeight: Int): Image {
        val type = BufferedImage.TYPE_INT_ARGB
        val resizedImage = ImageUtil.createImage(boundWidth, boundHeight, type)
        val g = resizedImage.createGraphics()
        g.composite = AlphaComposite.Src
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        val rectangle =
            getAspectRatioRect(Dimension(originalImage.width, originalImage.height), Dimension(boundWidth, boundHeight))
        g.drawImage(originalImage, rectangle.x, rectangle.y, rectangle.width, rectangle.height, null)
        g.dispose()
        return resizedImage
    }
}