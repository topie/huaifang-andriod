package com.topie.huaifang.http.bean

/**
 * Created by arman on 2017/7/25.
 *
 */
open class HFBaseResponseBody {
    @Suppress("MemberVisibilityCanPrivate")
    val code: Int = 0
    @Suppress("MemberVisibilityCanPrivate")
    val message: String? = null
    var json: String? = null

    val resultOk: Boolean
        get() {
            return code == 200
        }

    fun convertMessage(): String {
        return message ?: "错误码：$code"
    }

    override fun toString(): String {
        return "HFBaseResponseBody(code=$code, message=$message, json=$json)"
    }
}

