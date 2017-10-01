package com.topie.huaifang.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.annotation.AnyRes
import android.support.annotation.ColorRes
import android.support.annotation.StringRes
import android.widget.Toast
import java.lang.ref.WeakReference


/**
 * Created by arman on 2017/7/11.
 * Context的扩展
 */

/**
 * 单例applicationContext
 */
private var context: WeakReference<Context>? = null

/**
 * Context的委托属性
 */
var appContext: Context?
    get() {
        return context?.get()
    }
    set(value) {
        if (value != null) {
            context = WeakReference(value)
        }
    }

/**
 * 初始化applicationContext单例类
 */
fun Context.kInitApplication() {
    appContext = applicationContext
}

/**
 * 获取资源id
 */
fun kGetIdentifier(name: String, defType: String): Int {
    return appContext?.let { it.resources?.getIdentifier(name, defType, it.packageName) } ?: 0
}

/**
 * 通过资源id获取对应的名称
 */
@Suppress("unused")
fun kGetResourceEntryName(@AnyRes resId: Int): String? {
    return try {
        appContext?.resources?.getResourceEntryName(resId)
    } catch (e: Exception) {
        log("", e)
        null
    }
}

fun kToastLong(msg: String) {
    appContext?.let {
        HFToast.showToast(it, msg, Toast.LENGTH_LONG)
    }
}

fun kToastShort(msg: String) {
    appContext?.let {
        HFToast.showToast(it, msg, Toast.LENGTH_SHORT)
    }
}

/**
 * 如果Context为null，返回单例applicationContext
 */
fun Context?.kMakeNull2Application(): Context? {
    return this ?: appContext
}

/**
 * 打开一个activity，自动判断Context是否是activity
 */
fun Context?.kStartActivity(clazz: Class<out Activity>, bundle: Bundle? = null): Boolean {
    return kMakeNull2Application()?.let {
        val intent = Intent(it, clazz)
        bundle?.let { intent.putExtras(bundle) }
        it.kStartActivity(intent)
    } ?: false
}

/**
 * 打开一个activity，自动判断Context是否是activity
 */
fun Context?.kStartActivity(intent: Intent): Boolean {
    val context = kMakeNull2Application() ?: return false
    val kFindActivity = kFindActivity()
    kFindActivity?.startActivity(intent) ?: context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    return true
}

/**
 * 在Context中寻找activity
 */
fun Context?.kFindActivity(): Activity? {
    if (this == null) {
        return null
    }
    if (this is Activity) {
        return this
    }
    if (this is ContextWrapper) {
        return baseContext.kFindActivity()
    }
    return null
}

/**
 * 打电话界面
 */
fun Context.kTel(phoneNumber: String): Boolean {
    val intent = Intent(Intent.ACTION_DIAL)
    intent.data = Uri.parse("tel:" + phoneNumber)
    return intent.resolveActivity(packageManager)?.let { kStartActivity(intent) } ?: false
}

/**
 * 获取资源文件定义的颜色
 */
fun kGetColor(@ColorRes id: Int): Int {
    @Suppress("DEPRECATION")
    return appContext?.resources?.getColor(id) ?: 0xFFFFFFFF.toInt()
}

/**
 * 获取资源文件定义的字符串
 */
@Suppress("unused")
fun kGetString(@StringRes id: Int): String {
    return appContext?.resources?.getString(id) ?: ""
}

/**
 * 单例toast
 */
private object HFToast {

    private var toast: Toast? = null

    @SuppressLint("ShowToast")
    fun showToast(context: Context, msg: String, duration: Int): Toast {
        toast = toast ?: Toast.makeText(context, msg, duration)
        toast!!.setText(msg)
        toast!!.duration = duration
        toast!!.show()
        return toast!!
    }
}