package com.topie.huaifang.extensions

import android.text.TextUtils

/**
 * Created by arman on 2017/9/21.
 */
fun String?.kIsEmpty(): Boolean {
    return TextUtils.isEmpty(this)
}

fun String?.kIsNotEmpty(): Boolean {
    return !TextUtils.isEmpty(this)
}

fun String?.kToastLong() {
    HFContext.appContext?.kToastLong(this@kToastLong ?: "error")
}

fun String?.kToastShort() {
    HFContext.appContext?.kToastShort(this@kToastShort ?: "error")
}