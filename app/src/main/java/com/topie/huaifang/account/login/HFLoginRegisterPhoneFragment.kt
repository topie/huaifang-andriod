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
class HFLoginRegisterPhoneFragment : HFBaseFragment() {

    private var mEtPhone: EditText? = null
    private var mTvNext: TextView? = null

    override fun onCreateViewSupport(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val inflate = inflater.inflate(R.layout.login_register_phone_fragment, container, false)
        mEtPhone = inflate.kFindViewById(R.id.et_login_phone_input)
        mTvNext = inflate.kFindViewById(R.id.tv_login_phone_next)
        mTvNext!!.setOnClickListener {
            val phone = mEtPhone?.text?.toString()
            when {
                (phone?.length ?: 0) < 11 -> kToastShort("请输入有效的手机号码")
                else -> {
                    HFRetrofit.hfService.checkPhone(phone!!).subscribeResultOkApi {
                        when (it.data) {
                            false -> kToastShort("该手机已注册")
                            else -> activity?.let { it as HFLoginActivity }?.pushRegisterPwdFragment(phone)
                        }
                    }
                }
            }
        }
        return inflate
    }
}