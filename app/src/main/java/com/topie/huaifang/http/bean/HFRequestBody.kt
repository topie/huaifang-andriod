package com.topie.huaifang.http.bean

/**
 * Created by arman on 2017/7/25.
 * 登录请求
 */
data class LoginRequestBody(var username: String, var password: String)

data class RegisterRequestBody(var mobilePhone: String, var password: String)

data class CheckPhoneRequestBody(val mobile: String)