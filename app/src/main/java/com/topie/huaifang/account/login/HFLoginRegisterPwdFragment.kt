package com.topie.huaifang.account.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseFragment
import com.topie.huaifang.extensions.kFindViewById
import com.topie.huaifang.extensions.kToastShort
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.subscribeResultOkApi

/**
 * Created by arman on 2017/10/15.
 * 登录
 */
class HFLoginRegisterPwdFragment : HFBaseFragment() {

    private var mEtPwd: EditText? = null
    private var mTvRegister: TextView? = null

    companion object {
        const val ARGS_PHONE = "args_phone"
    }

    override fun onCreateViewSupport(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val inflate = inflater.inflate(R.layout.login_register_pwd_fragment, container, false)
        mEtPwd = inflate.kFindViewById(R.id.et_login_pwd_input)
        mTvRegister = inflate.kFindViewById(R.id.tv_login_pwd_next)

        mTvRegister!!.setOnClickListener {
            val phone = arguments?.getString(ARGS_PHONE)
            val pwd = mEtPwd?.text?.toString()
            when {
                (phone?.length ?: 0) < 11 -> kToastShort("请输入有效的手机号码")
                (pwd?.length ?: 0) < 6 -> kToastShort("请输入至少6位数密码")
                else -> {
                    HFRetrofit.hfService.register(phone!!, pwd!!).subscribeResultOkApi {
                        kToastShort("注册成功，请重新登录")
                        activity?.let { it as HFLoginActivity }?.pushLoginFragment(phone)
                    }
                }
            }
        }
        return inflate
    }
}