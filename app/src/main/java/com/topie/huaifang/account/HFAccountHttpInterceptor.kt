package com.topie.huaifang.account

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Created by arman on 2017/8/10.
 * 网络请求插值器
 * request：插入sess_key,shop_url,并重新计算sign值
 * response：获取sess_key,shop_url,并
 */
internal class HFAccountHttpInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = HFAccountManager.getToken()
        if (token != null) {
            val build = request.newBuilder().addHeader("X-Auth-Token", token).build()
            return chain.proceed(build)
        }
        return chain.proceed(request)
    }
}
