package com.davdain.tools.acp

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.PermissionChecker
import android.util.Log

/**
 * Created by hupei on 2016/4/26.
 */
internal class AcpService {

    /**
     * 检查权限授权状态
     *
     * @param context
     * @param permission
     * @return
     */
    fun checkSelfPermission(context: Context, permission: String): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val info = context.packageManager.getPackageInfo(
                        context.packageName, 0)
                val targetSdkVersion = info.applicationInfo.targetSdkVersion
                return if (targetSdkVersion >= Build.VERSION_CODES.M) {
                    Log.i(TAG, "targetSdkVersion >= Build.VERSION_CODES.M")
                    ContextCompat.checkSelfPermission(context, permission)
                } else {
                    PermissionChecker.checkSelfPermission(context, permission)
                }

            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }
        return ContextCompat.checkSelfPermission(context, permission)
    }

    /**
     * 向系统请求权限
     *
     * @param activity
     * @param permissions
     * @param requestCode
     */
    fun requestPermissions(activity: Activity, permissions: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode)
    }

    /**
     * 检查权限是否存在拒绝不再提示
     *
     * @param activity
     * @param permission
     * @return
     */
    fun shouldShowRequestPermissionRationale(activity: Activity, permission: String): Boolean {
        val rationale = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
        Log.i(TAG, "rationale = " + rationale)
        return rationale
    }

    companion object {
        private val TAG = "AcpService"
    }
}
