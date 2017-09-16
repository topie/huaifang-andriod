package com.topie.huaifang.http.bean

/**
 * Created by arman on 2017/7/25.
 *
 */
open class BaseRequestBody(val code: Int, var message: String?, var json: String?, var token: String?) {
    constructor() : this(0, null, null, null)

    override fun toString(): String {
        return "BaseRequestBody(code=$code, message=$message, json=$json, token=$token)"
    }

}

/**
 * 登录解析数据
 */
class LoginResponseBody : BaseRequestBody()