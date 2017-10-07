package com.topie.huaifang.extensions

import android.net.Uri
import android.text.TextUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


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

fun String?.kParseFileUri(): Uri? {
    if (kIsEmpty()) {
        return null
    }
    return Uri.fromFile(File(this))
}

private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

/**
 * 转化为常规时间，后台约定
 */
fun Date.kToSimpleFormat(): String {
    return simpleDateFormat.format(this)
}


/**
 * 常规时间转化为date实例
 */
fun String.kSimpleFormatToDate(): Date? {
    return try {
        simpleDateFormat.parse(this)
    } catch (ex: Exception) {
        log("kSimpleFormatToDate", ex)
        null
    }
}