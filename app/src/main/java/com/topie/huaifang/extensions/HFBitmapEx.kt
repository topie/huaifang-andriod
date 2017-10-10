package com.topie.huaifang.extensions

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.*

fun kCompress(file: File, targetWidth: Int, targetHeight: Int): Bitmap? {
    return BitmapFactory.Options().also {
        it.inPreferredConfig = Bitmap.Config.ARGB_8888
        it.inJustDecodeBounds = true
    }.also {
        var inputStream: InputStream? = null
        try {
            inputStream = FileInputStream(file)
            BitmapFactory.decodeStream(inputStream, null, it)
            it.inSampleSize = getInSampleSize(it.outWidth, it.outHeight, targetWidth, targetHeight)
            it.inJustDecodeBounds = false
        } catch (e: Exception) {
            log("kCompress", e)
            it.inSampleSize = 0
        } finally {
            inputStream.kSafeClose()
        }
    }.takeIf {
        it.inSampleSize >= 1
    }?.let {
        var inputStream: InputStream? = null
        try {
            inputStream = FileInputStream(file)
            BitmapFactory.decodeStream(inputStream, null, it)
        } catch (e: Exception) {
            log("kCompress", e)
            null
        } finally {
            inputStream.kSafeClose()
        }
    }
}


fun kCompress(file: File, outputFile: File, targetWidth: Int, targetHeight: Int): Boolean? {
    return BitmapFactory.Options().also {
        it.inPreferredConfig = Bitmap.Config.ARGB_8888
        it.inJustDecodeBounds = true
    }.also {
        var inputStream: InputStream? = null
        try {
            inputStream = FileInputStream(file)
            BitmapFactory.decodeStream(inputStream, null, it)
            it.inSampleSize = getInSampleSize(it.outWidth, it.outHeight, targetWidth, targetHeight)
            it.inJustDecodeBounds = false
        } catch (e: Exception) {
            log("kCompress", e)
            it.inSampleSize = 0
        } finally {
            inputStream.kSafeClose()
        }
    }.takeIf {
        it.inSampleSize >= 1
    }?.let {
        var inputStream: InputStream? = null
        try {
            inputStream = FileInputStream(file)
            BitmapFactory.decodeStream(inputStream, null, it)
        } catch (e: Exception) {
            log("kCompress", e)
            null
        } finally {
            inputStream.kSafeClose()
        }
    }?.let {
        var outputStream: OutputStream? = null
        try {
            outputStream = FileOutputStream(outputFile)
            it.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        } catch (e: Exception) {
            log("kCompress", e)
            false
        } finally {
            outputStream.kSafeClose()
        }
    } ?: false
}


fun getInSampleSize(outWidth: Int, outHeight: Int, targetWidth: Int, targetHeight: Int): Int {
    val sampleSize = when {
        targetHeight <= 0 || targetWidth <= 0 || outHeight <= 0 || outWidth <= 0 -> 1
        outWidth <= targetWidth && outHeight <= targetHeight -> 1
        outWidth > targetWidth && outHeight < targetHeight -> {
            (outWidth * 1.0f / targetWidth + 0.99f).toInt().coerceAtLeast(1)
        }
        outHeight > targetHeight && outWidth < targetWidth -> {
            (outHeight * 1.0f / targetHeight + 0.99f).toInt().coerceAtLeast(1)
        }
        else -> {
            //目标宽高小于实际宽高
            val size4Height = (outHeight * 1.0f / targetHeight + 0.99f).toInt()
            val size4Width = (outWidth * 1.0f / targetWidth + 0.99f).toInt()
            Math.max(size4Height, size4Width).coerceAtLeast(1)
        }
    }
    log("sampleSize = {$sampleSize}, width = {$outWidth}, height = {$outHeight}, targetWidth = {$targetWidth} , targetHeight = {$targetHeight}")
    return sampleSize
}