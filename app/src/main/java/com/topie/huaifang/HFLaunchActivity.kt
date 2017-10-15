package com.topie.huaifang

import android.os.Bundle
import com.topie.huaifang.account.HFAccountManager
import com.topie.huaifang.account.login.HFLoginActivity
import com.topie.huaifang.base.HFBaseActivity
import com.topie.huaifang.extensions.kStartActivity
import com.topie.huaifang.facing.HFMainActivity

/**
 * Created by arman on 2017/10/15.
 * 启动页
 */
class HFLaunchActivity : HFBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (HFAccountManager.isLogin) {
            kStartActivity(HFMainActivity::class.java)
        } else {
            kStartActivity(HFLoginActivity::class.java)
        }
    }
}