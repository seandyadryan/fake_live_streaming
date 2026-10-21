package com.deploydulupulangnanti.fakelivestream

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import java.io.File
import java.util.UUID

/** Keep a bounded, normalized private copy; the picker grant can expire safely. */
object PhotoStore {
    fun import(context: Context, uri: Uri): String {
        val source = File.createTempFile("picked-", ".image", context.cacheDir)
        val destination = File(context.filesDir, "stage-${UUID.randomUUID()}.jpg")
        var bitmap: Bitmap? = null
        try {
            requireNotNull(context.contentResolver.openInputStream(uri)).use { input ->
                source.outputStream().use { output ->
                    val buffer = ByteArray(8192)
                    var total = 0
                    while (true) {
                        val count = input.read(buffer)
                        if (count < 0) break
                        total += count
                        require(total <= 20 * 1024 * 1024) { "Choose an image smaller than 20 MB." }
                        output.write(buffer, 0, count)
                    }
                }
            }
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(source.path, bounds)
            require(bounds.outWidth > 0 && bounds.outHeight > 0) { "This image could not be opened." }
            var sample = 1
            while (maxOf(bounds.outWidth, bounds.outHeight) / sample > 2048) sample *= 2
            bitmap = requireNotNull(BitmapFactory.decodeFile(source.path, BitmapFactory.Options().apply { inSampleSize = sample }))
            val matrix = Matrix()
            when (runCatching { ExifInterface(source.path).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL) }.getOrDefault(ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.setScale(1f, -1f)
                ExifInterface.ORIENTATION_TRANSPOSE -> { matrix.setRotate(90f); matrix.postScale(-1f, 1f) }
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
                ExifInterface.ORIENTATION_TRANSVERSE -> { matrix.setRotate(270f); matrix.postScale(-1f, 1f) }
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(270f)
            }
            if (!matrix.isIdentity) {
                val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                if (rotated !== bitmap) bitmap.recycle()
                bitmap = rotated
            }
            val normalized = requireNotNull(bitmap)
            destination.outputStream().use { require(normalized.compress(Bitmap.CompressFormat.JPEG, 90, it)) }
            return destination.name
        } catch (e: Exception) {
            destination.delete()
            throw e
        } finally { bitmap?.recycle(); source.delete() }
    }

    fun file(context: Context, name: String?): File? = name?.takeIf {
        it.startsWith("stage-") && it.endsWith(".jpg") && '/' !in it && '\\' !in it
    }?.let { File(context.filesDir, it) }?.takeIf { it.isFile }
}
