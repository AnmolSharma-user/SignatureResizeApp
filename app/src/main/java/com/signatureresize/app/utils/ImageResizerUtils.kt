package com.signatureresize.app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream

object ImageResizerUtils {

    fun resizeImage(
        context: Context,
        uri: Uri,
        maxWidth: Int?,
        maxHeight: Int?,
        maxSizeKB: Int?
    ): ByteArray? {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream) ?: return null
            inputStream?.close()

            // 1. Resize Dimensions
            var processedBitmap = originalBitmap
            if (maxWidth != null && maxHeight != null) {
                 processedBitmap = Bitmap.createScaledBitmap(originalBitmap, maxWidth, maxHeight, true)
            }

            // 2. Compress to Target Size (KB)
            if (maxSizeKB != null) {
                return compressToTargetSize(processedBitmap, maxSizeKB)
            }

            // Default return if no KB limit (just dimensions)
            val stream = ByteArrayOutputStream()
            processedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
            return stream.toByteArray()

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun compressToTargetSize(bitmap: Bitmap, targetKB: Int): ByteArray {
        var quality = 100
        var stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)

        while (stream.toByteArray().size / 1024 > targetKB && quality > 5) {
            stream = ByteArrayOutputStream()
            quality -= 5
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        }
        return stream.toByteArray()
    }
}
