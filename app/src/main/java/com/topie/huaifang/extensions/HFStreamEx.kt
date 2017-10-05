package com.topie.huaifang.extensions

import java.io.Closeable

fun Closeable?.kSafeClose() {
    try {
        this?.close()
    } catch (e: Exception) {
        log("close", e)
    }
}



