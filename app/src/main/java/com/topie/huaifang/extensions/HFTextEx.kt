package com.topie.huaifang.extensions

import android.net.Uri
import android.text.TextUtils
import android.webkit.URLUtil
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

fun String?.kParseUrlOrFileUri(): Uri? {
    if (this == null || this.isEmpty()) {
        return null
    }
    if (URLUtil.isHttpUrl(this) || URLUtil.isHttpsUrl(this)) {
        return Uri.parse(this)
    }
    return Uri.fromFile(File(this))
}


private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
private val simpleDateFormat2 = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

const val DAY = 1000 * 60 * 60 * 24
const val HOUR = 1000 * 60 * 60
const val MINUTE = 1000 * 60
const val SECOND = 1000

/**
 * 转化为常规时间，后台约定
 */
fun Date.kToSimpleFormat(): String {
    return simpleDateFormat.format(this)
}

/**
 * 按时间段显示，今天、昨天、明天
 */
fun Date.kSplit(): String {
    val today = Date()
    if (today.time / DAY - time / DAY == 1.toLong()) {
        return "昨天"
    }
    val defSecond = today.time - time
    return when {
        defSecond < MINUTE -> "刚刚"
        defSecond < HOUR -> "${defSecond / MINUTE}分钟前"
        defSecond < DAY -> "${defSecond / HOUR}小时前"
        else -> simpleDateFormat2.format(this)
    }
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