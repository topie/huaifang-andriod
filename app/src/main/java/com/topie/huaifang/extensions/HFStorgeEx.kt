package com.topie.huaifang.extensions

import java.io.File
import java.io.InputStream

/*
 * Created by arman on 2017/10/1.
 * 本地存储的相关扩展
 */

/**
 * 缓存目录下picture文件夹
 */
fun kGetCachePictureDir(): File? {
    return try {
        appContext?.externalCacheDir?.let { File(it, "picture").apply { mkdirs() } }
    } catch (e: Exception) {
        null
    }
}

fun File.kMkdirs() {
    parentFile.takeIf { !it.exists() }?.mkdirs()
}

fun kGetExtraFilesDir(): File? {
    return appContext?.getExternalFilesDir(null)
}

fun kGetExtraCacheDir(): File? {
    return appContext?.externalCacheDir
}

fun kWrite2File(inputStream: InputStream, file: File) {

}