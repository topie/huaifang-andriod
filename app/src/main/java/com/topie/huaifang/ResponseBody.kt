package com.topie.huaifang

/**
 * Created by arman on 2017/7/25.
 *
 */
open class BaseRequestBody(val code: Int, var message: String?, var json: String?, var token: String?) {
    constructor() : this(0, null, null, null)
}

/**
 * 登录解析数据
 */
class LoginResponseBody : BaseRequestBody()