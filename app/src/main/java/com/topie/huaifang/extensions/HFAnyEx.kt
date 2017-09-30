package com.topie.huaifang.extensions

import com.topie.huaifang.util.HFLogger

/**
 * Created by arman on 2017/7/11.
 * Any类的扩展
 */

fun Any?.kIsNull(): Boolean {
    return this == null
}

fun Any?.kIsNotNull(): Boolean {
    return this != null
}

fun log(msg: String) {
    HFLogger.log(msg)
}

fun log(msg: String, throwable: Throwable) {
    HFLogger.log(msg, throwable)
}
