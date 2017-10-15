package com.topie.huaifang.account.login

import android.os.Bundle
import com.topie.huaifang.HFActivityManager
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.kStartActivity
import com.topie.huaifang.facing.HFMainActivity

/**
 * Created by arman on 2017/9/20.
 * 登录／注册
 */
class HFLoginActivity : HFBaseTitleActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        mContentId = R.id.fl_login_container
        pushLoginFragment()
    }

    fun pushLoginFragment(phone: String? = null) {
        val args = createArgsWithTitle("登录")
        args.putString(HFLoginFragment.ARGS_PHONE, phone)
        pushFragment(HFLoginFragment::class.java, args)
    }

    fun pushRegisterPhoneFragment() {
        pushFragment(HFLoginRegisterPhoneFragment::class.java, createArgsWithTitle("新用户注册"))
    }

    fun pushRegisterPwdFragment(phone: String) {
        val args = createArgsWithTitle("设置登录密码")
        args.putString(HFLoginRegisterPwdFragment.ARGS_PHONE, phone)
        pushFragment(HFLoginRegisterPwdFragment::class.java, args)
    }

    fun toApp() {
        if (HFActivityManager.isActivityOnAct(HFMainActivity::class.java)) {
            finish()
        } else {
            kStartActivity(HFMainActivity::class.java)
            finish()
        }
    }

    override fun onBackPressed() {
        when (curFragment?.javaClass) {
            HFLoginFragment::class.java -> HFActivityManager.closeAllActivities()
            HFLoginRegisterPhoneFragment::class.java -> pushLoginFragment()
            HFLoginRegisterPwdFragment::class.java -> pushRegisterPhoneFragment()
            else -> HFActivityManager.closeAllActivities()
        }
    }
}