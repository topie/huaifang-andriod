package com.topie.huaifang.function

import android.os.Bundle
import com.topie.huaifang.R
import com.topie.huaifang.account.HFAccountManager
import com.topie.huaifang.account.login.HFLoginActivity
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.kStartActivity
import com.topie.huaifang.extensions.kToastShort
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.subscribeApi
import kotlinx.android.synthetic.main.function_setting_activity.*

/**
 * Created by arman on 2017/10/30.
 * 设置页面
 */
class HFSettingActivity : HFBaseTitleActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.function_setting_activity)
        setBaseTitle("设置")
        tv_fun_setting_logout.setOnClickListener {
            HFRetrofit.hfService.logout().subscribeApi {
                if (!it.resultOk) {
                    it.convertMessage().kToastShort()
                } else {
                    HFAccountManager.resetToGuest()
                    this@HFSettingActivity.kStartActivity(HFLoginActivity::class.java)
                }
            }
        }
    }
}