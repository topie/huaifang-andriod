package com.topie.huaifang.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import android.support.annotation.AnyRes
import android.widget.Toast
import java.io.File
import java.lang.ref.WeakReference

/**
 * Created by arman on 2017/7/11.
 */


//--------------------------------------------//
//file
//--------------------------------------------//

fun Context.kIsExternalAvailable(): Boolean {
    return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
}


/**
 *
 * @param type The type of files directory to return. May be {@code null}
 *            for the root of the files directory or one of the following
 *            constants for a subdirectory:
 *            {@link android.os.Environment#DIRECTORY_MUSIC},
 *            {@link android.os.Environment#DIRECTORY_PODCASTS},
 *            {@link android.os.Environment#DIRECTORY_RINGTONES},
 *            {@link android.os.Environment#DIRECTORY_ALARMS},
 *            {@link android.os.Environment#DIRECTORY_NOTIFICATIONS},
 *            {@link android.os.Environment#DIRECTORY_PICTURES}, or
 *            {@link android.os.Environment#DIRECTORY_MOVIES}.
 */
fun Context.kGetExternalFilesDir(type: String): File? {
    if (!kIsExternalAvailable()) {
        return null
    }
    return getExternalFilesDir(type)?.takeIf {
        if (it.exists() && !it.isDirectory) {
            it.delete() && it.mkdirs()
        } else if (!it.exists()) {
            it.mkdirs()
        } else {
            true
        }
    }
}

fun Context.kGetExternalPictureDir(): File? {
    return kGetExternalFilesDir(Environment.DIRECTORY_PICTURES)
}

fun Context.kGetExtrnalPictureFile(name: String): File? {
    return kGetExternalFilesDir(Environment.DIRECTORY_PICTURES)?.let { return@let File(it, name) }
}

/**
 * 获取资源id
 */
fun Context.kGetIdentifier(name: String, defType: String): Int {
    return resources.getIdentifier(name, defType, packageName)
}

/**
 * 通过资源id获取对应的名称
 */
fun Context.kGetResourceEntryName(@AnyRes resId: Int): String? {
    try {
        return resources.getResourceEntryName(resId)
    } catch (e: Exception) {
        log("", e)
        return null
    }
}

fun Context.kToastLong(msg: String) {
    HFToast.showToast(this, msg, Toast.LENGTH_LONG)
}

fun Context.kToastShort(msg: String) {
    HFToast.showToast(this, msg, Toast.LENGTH_SHORT)
}

fun Context.kInitApplication() {
    HFContext.init(applicationContext)
}

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

object HFContext {

    private var context: WeakReference<Context>? = null

    val appContext: Context?
        get() {
            return context?.get()
        }

    fun init(context: Context) {
        this.context = WeakReference(context)
    }
}