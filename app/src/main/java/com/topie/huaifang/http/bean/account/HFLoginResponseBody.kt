package com.topie.huaifang.http.bean.account

import com.topie.huaifang.http.bean.HFBaseResponseBody

/**
 * 登录解析数据
 */
class HFLoginResponseBody : HFBaseResponseBody() {
    val token: String? = null
    override fun toString(): String {
        return "HFLoginResponseBody(token=$token)"
    }
}