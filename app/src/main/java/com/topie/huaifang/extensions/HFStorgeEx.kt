package com.topie.huaifang.extensions

import java.io.File

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