package com.topie.huaifang.http.bean

/**
 * Created by arman on 2017/7/25.
 *
 */
open class BaseRequestBody(val code: Int, val message: String?, var json: String?) {
    constructor() : this(0, null, null)

    fun isResultOk(): Boolean {
        return code == 200
    }

    fun convertMessage(): String {
        return message ?: "错误码：$code"
    }

    override fun toString(): String {
        return "BaseRequestBody(code=$code, message=$message, json=$json)"
    }
}

/**
 * 登录解析数据
 */
class LoginResponseBody(val token: String?) : BaseRequestBody() {
    constructor() : this(null)

    override fun toString(): String {
        return "LoginResponseBody(token=$token)"
    }
}

/**
 * 检查手机号是否唯一
 */
class CheckPhoneResponseBody(val data: Boolean) : BaseRequestBody() {
    constructor() : this(false)
}