package com.topie.huaifang.login

import android.os.Bundle
import android.view.View
import com.topie.huaifang.HFBaseTitleActivity
import com.topie.huaifang.R
import com.topie.huaifang.account.HFAccountManager
import com.topie.huaifang.extensions.kToastLong
import com.topie.huaifang.extensions.log
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.composeApi
import kotlinx.android.synthetic.main.login_activity.*

/**
 * Created by arman on 2017/9/20.
 * 登录／注册
 */
class HFLoginActivity : HFBaseTitleActivity() {
    companion object {
        const val EXTRA_IS_REGISTER = "extra_is_register"
    }

    private var isRegister = false
    private var inputPwd = false

    private var phone: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        isRegister = intent.getBooleanExtra(EXTRA_IS_REGISTER, false)
        when (isRegister) {
            true -> {
                fl_login_phone_area.visibility = View.VISIBLE
                fl_login_pwd_area.visibility = View.GONE
                tv_login_register_button.visibility = View.GONE
                setBaseTitle(R.string.login_reg_phone_title)
            }
            else -> {
                fl_login_phone_area.visibility = View.VISIBLE
                fl_login_pwd_area.visibility = View.VISIBLE
                tv_login_register_button.visibility = View.VISIBLE
                setBaseTitle(R.string.default_login)
            }
        }
        tv_login_pwd_next.setOnClickListener {
            when {
                isRegister -> if (inputPwd) {
                    if (!checkPwd()) return@setOnClickListener
                    val pwd = et_login_pwd_input.text.toString()
                    HFRetrofit.hfService.register(phone ?: "", pwd).composeApi().subscribe({
                        if (!it.resultOk) {
                            log(it.convertMessage())
                        } else {
                            kToastLong("注册成功")
                            finish()
                        }
                    }, {
                        kToastLong(it.message ?: "error")
                    })
                } else {
                    if (!checkPhone()) return@setOnClickListener
                    val phone = et_login_phone_input.text.toString()
                    HFRetrofit.hfService.checkPhone(phone).composeApi().subscribe({
                        if (it.resultOk && it.data) {
                            this@HFLoginActivity.phone = phone
                            this@HFLoginActivity.inputPwd = true
                            fl_login_phone_area.visibility = View.GONE
                            fl_login_pwd_area.visibility = View.VISIBLE
                        } else if (!it.data) {
                            kToastLong("手机号码已注册")
                        } else {
                            kToastLong(it.convertMessage())
                        }
                    }, {
                        kToastLong(it.message ?: "error")
                    })

                }
                else -> {
                    if (!(checkPhone() && checkPwd())) return@setOnClickListener
                    val phone = et_login_phone_input.text.toString()
                    val pwd = et_login_pwd_input.text.toString()
                    HFRetrofit.hfService.login(phone, pwd).composeApi().subscribe({
                        if (it.resultOk) {
                            HFAccountManager.setToken(it.token)
                            kToastLong("登录成功")
                            finish()
                        } else {
                            kToastLong(it.convertMessage())
                        }
                    }, {
                        kToastLong(it.message ?: "error")
                    })
                }
            }
        }
    }

    private fun checkPhone(): Boolean {
        return et_login_phone_input?.text?.length?.equals(11) ?: false
    }

    private fun checkPwd(): Boolean {
        return et_login_pwd_input?.text?.length?.let {
            return it in 6..16
        } ?: false
    }
}