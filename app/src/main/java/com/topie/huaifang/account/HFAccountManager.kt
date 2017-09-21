package com.topie.huaifang.account

import android.content.Context
import com.google.gson.Gson
import com.topie.huaifang.extensions.HFContext
import com.topie.huaifang.extensions.kIsEmpty
import com.topie.huaifang.extensions.kIsNotEmpty
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.util.HFLogger
import okhttp3.logging.HttpLoggingInterceptor

/**
 * Created by arman on 2017/9/21.
 */
object HFAccountManager {
    private const val SP_NAME = "com.topie.huaifang.account.HFAccountManager"
    private const val KEY_MODEL = "model"
    private val logger: HFLogger = HFLogger.HFDefaultLogger("Account")
    private var accountModel: HFAccountModel = getData()

    init {
        val logInterceptor = HttpLoggingInterceptor({
            logger.log(it)
        }).setLevel(HttpLoggingInterceptor.Level.BODY)
        HFRetrofit.addIntercept(HFAccountHttpInterceptor())
        HFRetrofit.addIntercept(logInterceptor)
    }

    val isLogin: Boolean
        get() {
            return accountModel.token.kIsNotEmpty()
        }

    fun setToken(token: String?) {
        accountModel.token = token
        saveData()
    }

    fun getToken(): String? {
        return accountModel.token
    }

    private fun saveData() {
        HFContext.appContext?.let {
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
        return HFContext.appContext?.let {
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
}