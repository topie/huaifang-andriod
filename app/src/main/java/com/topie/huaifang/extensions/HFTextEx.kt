package com.topie.huaifang.extensions

import android.net.Uri
import android.text.TextUtils
import java.text.SimpleDateFormat
import java.util.*

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
    kToastLong(this@kToastLong ?: "error")
}

fun String?.kToastShort() {
    kToastShort(this@kToastShort ?: "error")
}

fun String?.kParseUrl(): Uri? {
    if (kIsEmpty()) {
        return null
    }
    return Uri.parse(this)
}

/**
 * yyyy-MM-dd HH:mm:ss
 */
fun String?.kFormatTime(format: String): String? {
    if (kIsEmpty()) {
        return null
    }
    val formatter = SimpleDateFormat(format, Locale.getDefault())
    return formatter.format(this)
}