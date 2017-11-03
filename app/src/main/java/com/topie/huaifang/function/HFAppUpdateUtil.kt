package com.topie.huaifang.function

import android.content.Context
import com.topie.huaifang.extensions.kInto
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.composeApi
import io.reactivex.disposables.Disposable

/**
 * Created by arman on 2017/10/31.
 */
class HFAppUpdateUtil(var context: Context?) : Disposable {

    private val list: MutableList<Disposable> = mutableListOf()

    override fun isDisposed(): Boolean {
        return context == null
    }

    override fun dispose() {
        list.forEach {
            if (!it.isDisposed) {
                it.dispose()
            }
        }
        list.clear()
        context = null
    }

    fun checkUpdate() {
        context ?: return
        HFRetrofit.hfService.getAppUpdate().composeApi().subscribe({
            if (!it.resultOk) {
                return@subscribe
            }
            context ?: return@subscribe
            it.data?.version?.takeIf { it.isNotEmpty() } ?: return@subscribe
            compareToCurVersionName(context!!, it.data!!.version!!).takeIf { it } ?: return@subscribe
            val downloadUrl = it.data!!.downloadUrl?.takeIf { it.isNotEmpty() } ?: return@subscribe
            val force = it.data!!.forceUpdate == 1
            onUpdate(downloadUrl, force)
        }, {}).kInto(list)
    }

    private fun onUpdate(downloadUrl: String, force: Boolean) {
        
    }

    /**
     * if true,application's version < oldest version
     * otherwise, otherwise
     */
    private fun compareToCurVersionName(context: Context, version: String): Boolean {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val appVer = packageInfo.versionName.split('.').asReversed()
        val newVer = version.split('.').asReversed()
        for (i in 0 until appVer.size.coerceAtMost(newVer.size)) {
            val newCode = newVer[i].toIntOrNull() ?: return false
            val appCode = appVer[i].toIntOrNull() ?: return false
            if (newCode > appCode) {
                return true
            }
        }
        return newVer.size > appVer.size
    }
}