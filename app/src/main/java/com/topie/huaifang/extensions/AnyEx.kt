package com.topie.huaifang.extensions

import android.util.Log

/**
 * Created by arman on 2017/7/11.
 */

fun Any?.kIsNull(): Boolean {
    return this == null
}

fun Any?.log(msg: String) {
    val tag = this?.javaClass?.simpleName ?: "NULL"
    Log.i(tag, msg)
}

fun Any?.log(msg: String, throwable: Throwable) {
    val tag = this?.javaClass?.simpleName ?: "NULL"
    Log.e(tag, msg, throwable)
}
