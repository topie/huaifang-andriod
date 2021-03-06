package com.topie.huaifang.account

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.google.gson.Gson
import com.topie.huaifang.HFActivityManager
import com.topie.huaifang.account.login.HFLoginActivity
import com.topie.huaifang.extensions.appContext
import com.topie.huaifang.extensions.kIsNotEmpty
import com.topie.huaifang.extensions.kStartActivity
import com.topie.huaifang.extensions.log
import com.topie.huaifang.function.identity.HFFunIdentityEditActivity
import com.topie.huaifang.http.HFHttpLoggingInterceptor
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.account.HFRoomInfo
import com.topie.huaifang.http.bean.account.HFUserInfo
import com.topie.huaifang.http.composeApi
import com.topie.huaifang.util.HFLogger
import com.topie.huaifang.view.HFTipDialog
import java.lang.ref.WeakReference

/**
 * Created by arman on 2017/9/21.
 * 账户管理
 */
object HFAccountManager {
    private const val SP_NAME = "com.topie.huaifang.account.HFAccountManager"
    private const val KEY_MODEL = "model"
    private val logger: HFLogger = HFLogger.HFDefaultLogger("Account")
    private var identifyDialog: Pair<HFTipDialog, WeakReference<Activity>>? = null
    private val mainHandler: Handler = Handler(Looper.getMainLooper())

    var accountModel: HFAccountModel = getData()
        private set

    init {
        HFRetrofit.addIntercept(HFAccountHttpInterceptor())
        HFRetrofit.addIntercept(HFHttpLoggingInterceptor(logger).setLevel(HFHttpLoggingInterceptor.Level.BODY))
    }

    val isLogin: Boolean
        get() {
            return accountModel.token.kIsNotEmpty()
        }

    fun setToken(token: String?) {
        accountModel.token = token
        saveData()
    }

    fun resetToGuest() {
        setUserInfo(null, null, null)
    }

    fun resetToGuestRelogin() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            resetToGuest()
            HFActivityManager.resumedActivity?.get().kStartActivity(HFLoginActivity::class.java)
        } else {
            mainHandler.post({ resetToGuestRelogin() })
        }
    }

    fun setUserInfo(token: String?, userInfo: HFUserInfo?, roomInfo: HFRoomInfo?) {
        accountModel.token = token
        accountModel.userInfo = userInfo
        accountModel.roomInfo = roomInfo
        saveData()
    }

    fun getToken(): String? {
        return accountModel.token
    }

    fun refreshAccountData(onFinished: ((manager: HFAccountManager) -> Unit)? = null) {
        HFRetrofit.hfService.getAccountInfo().composeApi().subscribe({
            if (it.resultOk) {
                accountModel.userInfo = it?.data?.base
                accountModel.roomInfo = it?.data?.shenfen
            }
            onFinished?.invoke(this@HFAccountManager)
        }, {
            log("refreshAccountData", it)
            onFinished?.invoke(this@HFAccountManager)
        })
    }

    private fun saveData() {
        appContext?.let {
            try {
                val toJson = Gson().toJson(accountModel)
                val sp = it.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
                sp.edit().putString(KEY_MODEL, toJson).apply()
            } catch (error: Exception) {
                HFLogger.log("saveData", error)
            }

        }
    }

    private fun getData(): HFAccountModel {
        return appContext?.let {
            val sp = it.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
            val json = sp.getString(KEY_MODEL, null)
            return try {
                Gson().fromJson(json, HFAccountModel::class.java)
            } catch (error: Exception) {
                HFLogger.log("from json[$json]", error)
                HFAccountModel()
            }
        } ?: HFAccountModel()
    }

    fun showIdentifyDialog() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            showIdentifyDialogPrivate(HFActivityManager.resumedActivity?.get())
        } else {
            mainHandler.post({ showIdentifyDialogPrivate(HFActivityManager.resumedActivity?.get()) })
        }
    }

    private fun showIdentifyDialogPrivate(activity: Activity?) {
        if (activity?.isFinishing != false) {
            return
        }
        if (identifyDialog?.second?.get() == activity) {
            return
        }
        identifyDialog?.first?.dismiss()
        HFTipDialog.Builder()
                .apply {
                    content = "需要添加认证信息后才能继续访问"
                    onOkClicked = { activity.kStartActivity(HFFunIdentityEditActivity::class.java) }
                }
                .show(activity)
                .also { dialog ->
                    dialog.setOnDismissListener {
                        if (identifyDialog?.first == dialog) {
                            identifyDialog = null
                        }
                    }
                }
                .let { identifyDialog = it to WeakReference(activity) }
    }
}