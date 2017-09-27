package com.topie.huaifang.extensions

import android.util.Log
import com.topie.huaifang.BuildConfig

/**
 * Created by arman on 2017/7/11.
 */

fun Any?.kIsNull(): Boolean {
    return this == null
}

fun Any?.kIsNotNull(): Boolean {
    return this != null
}

fun Any?.log(msg: String) {
    if (BuildConfig.DEBUG.not()) {
        return
    }
    val tag = this?.javaClass?.simpleName ?: "NULL"
    Log.i(tag, msg)
}

fun Any?.log(msg: String, throwable: Throwable) {
    if (BuildConfig.DEBUG.not()) {
        return
    }
    val tag = this?.javaClass?.simpleName ?: "NULL"
    Log.e(tag, msg, throwable)
}
