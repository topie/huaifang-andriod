package com.topie.huaifang.extensions

import android.content.Context
import android.os.Environment
import java.io.File

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