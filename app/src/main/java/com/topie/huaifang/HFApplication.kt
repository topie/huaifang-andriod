package com.topie.huaifang

import android.app.Application
import com.davdain.tools.acp.Acp
import com.topie.huaifang.account.HFAccountManager
import com.topie.huaifang.extensions.kInitApplication

/**
 * Created by arman on 2017/7/26.
 */
class HFApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        kInitApplication()
        HFAccountManager.getToken()
        registerActivityLifecycleCallbacks(HFActivityManager.HFActivityLifecycleCallbacks)
        Acp.init(applicationContext)
    }
}