package com.davdain.tools.acp

import android.content.Context
import android.content.Intent
import android.os.Build
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * 参考 http://blog.csdn.net/tgbus18990140382/article/details/47058043
 * Created by hupei on 2016/7/22.
 */
internal object MiuiOs {

    val UNKNOWN = "UNKNOWN"

    /**
     * 获取 V5/V6 后面的数字
     */
    val systemVersionCode: Int
        get() {
            val systemProperty = systemProperty
                    .takeIf { it.length >= 2 && 'V' == it[0].toUpperCase() }
                    ?.substring(1)
                    ?.toIntOrNull()
            return systemProperty ?: 0
        }

    /**
     * 判断V5/V6
     *
     * @return V5 、V6 、V7 字符
     */
    val systemProperty: String
        get() {
            return try {
                val pro = Runtime.getRuntime().exec("getprop ro.miui.ui.version.name")
                BufferedReader(InputStreamReader(pro.inputStream), 1024)
            } catch (e: IOException) {
                null
            }?.let {
                val line = try {
                    it.readLine()
                } catch (e: Exception) {
                    UNKNOWN
                }
                try {
                    it.close()
                } catch (e: Exception) {
                }
                return line
            } ?: UNKNOWN
        }

    /**
     * 检查手机是否是miui
     *
     * @return
     * @ref http://dev.xiaomi.com/doc/p=254/index.html
     */
    val isMIUI: Boolean
        get() {
            return "Xiaomi" == Build.MANUFACTURER
        }

    /**
     * 获取应用权限设置 Intent <br></br>
     * http://blog.csdn.net/dawanganban/article/details/41749911
     *
     * @param context
     */
    fun getSettingIntent(context: Context): Intent? {
        // 之兼容miui v5/v6/v7  的应用权限设置页面
        if (systemVersionCode >= 6) {
            val intent = Intent()
            intent.action = "miui.intent.action.APP_PERM_EDITOR"
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.putExtra("extra_pkgname", context.packageName)
            return intent
        }
        return null
    }

    /**
     * 判断 activity 是否存在
     *
     * @param context
     * @param intent
     * @return
     */
    fun isIntentAvailable(context: Context, intent: Intent?): Boolean {
        return when (intent) {
            null -> false
            else -> context.packageManager.queryIntentActivities(intent, 0).size > 0
        }
    }
}
