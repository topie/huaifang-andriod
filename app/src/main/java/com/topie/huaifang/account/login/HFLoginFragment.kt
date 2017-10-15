package com.topie.huaifang.account.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.topie.huaifang.R
import com.topie.huaifang.account.HFAccountManager
import com.topie.huaifang.base.HFBaseFragment
import com.topie.huaifang.extensions.kFindViewById
import com.topie.huaifang.extensions.kToastShort
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.subscribeResultOkApi

/**
 * Created by arman on 2017/10/15.
 * 登录
 */
class HFLoginFragment : HFBaseFragment() {

    private var mEtPhone: EditText? = null
    private var mEtPwd: EditText? = null
    private var mTvLogin: TextView? = null
    private var mTvRegister: TextView? = null

    companion object {
        const val ARGS_PHONE = "args_phone"
    }

    override fun onCreateViewSupport(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val inflate = inflater.inflate(R.layout.login_fragment, container, false)
        mEtPhone = inflate.kFindViewById(R.id.et_login_phone_input)
        mEtPwd = inflate.kFindViewById(R.id.et_login_pwd_input)
        mTvLogin = inflate.kFindViewById(R.id.tv_login_next)
        mTvRegister = inflate.kFindViewById(R.id.tv_login_register_button)

        mTvLogin!!.setOnClickListener {
            val phone = mEtPhone?.text?.toString()
            val pwd = mEtPwd?.text?.toString()
            when {
                (phone?.length ?: 0) < 11 -> kToastShort("请输入有效的手机号码")
                (pwd?.length ?: 0) < 6 -> kToastShort("请输入至少6位数密码")
                else -> {
                    HFRetrofit.hfService.login(phone!!, pwd!!).subscribeResultOkApi {
                        HFAccountManager.setToken(it.token)
                        activity?.let { it as HFLoginActivity }?.toApp()
                    }
                }
            }
        }
        mTvRegister!!.setOnClickListener {
            activity?.let { it as HFLoginActivity }?.pushRegisterPhoneFragment()
        }
        return inflate
    }

    override fun setArguments(args: Bundle?) {
        super.setArguments(args)
        args?.getString(ARGS_PHONE)?.let {
            mEtPhone?.setText(it)
        }
    }
}