package com.topie.huaifang.account

import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import org.json.JSONObject
import java.io.EOFException
import java.io.IOException
import java.nio.charset.Charset


/**
 * Created by arman on 2017/8/10.
 * 网络请求插值器
 *
 */
internal class HFAccountHttpInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = HFAccountManager.getToken()
        val response = if (token != null) {
            val build = request.newBuilder().addHeader("X-Auth-Token", token).build()
            chain.proceed(build)
        } else {
            chain.proceed(request)
        }
        if (response.code() != 200) {
            return response
        }
        try {
            val responseBody = response.body()
            val source = responseBody!!.source()
            source.request(java.lang.Long.MAX_VALUE) // Buffer the entire body.
            val charset = responseBody.contentType()?.charset(UTF8) ?: UTF8
            source.buffer().takeIf {
                isPlaintext(it) && 0.toLong() != responseBody.contentLength()
            }?.clone()?.let {
                var code = -1
                try {
                    val json = it.readString(charset)
                    val jsonObject = JSONObject(json)
                    code = jsonObject.optInt("code")
                } finally {
                    if (code == 401) {
                        HFAccountManager.setToken(null)
                    }
                }
            }
        } catch (aE: Exception) {
            throw IOException(aE)
        }
        return response
    }

    companion object {
        private val UTF8 = Charset.forName("UTF-8")

        /**
         * Returns true if the body in question probably contains human readable text. Uses a small sample
         * of code points to detect unicode control characters commonly used in binary file signatures.
         */
        private fun isPlaintext(buffer: Buffer): Boolean {
            try {
                val prefix = Buffer()
                val byteCount = (if (buffer.size() < 64) buffer.size() else 64).toLong()
                buffer.copyTo(prefix, 0, byteCount)
                for (i in 0..15) {
                    if (prefix.exhausted()) {
                        break
                    }
                    val codePoint = prefix.readUtf8CodePoint()
                    if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                        return false
                    }
                }
                return true
            } catch (e: EOFException) {
                return false // Truncated UTF-8 sequence.
            }

        }
    }
}

//internal class AccountHttpInterceptor @JvmOverloads constructor(aManager: AccountManager, aLogger: AccountLogger = AccountLogger("AccountHttpInterceptor")) : Interceptor {
//
//    @Throws(IOException::class)
//    override fun intercept(chain: Interceptor.Chain): Response {
//        var request = chain.request()
//        val body = request.body()
//        if (POST == request.method()) {
//            when (body) {
//                is FormBody -> {
//                    val size = body.size()
//                    val map = HashMap<String, String>()
//                    for (i in 0 until size) {
//                        val name = body.name(i)
//                        val value = body.value(i)
//                        map.put(name, value)
//                    }
//                    val builder = FormBody.Builder()
//                    for ((key, value) in map) {
//                        builder.add(key, value)
//                    }
//                    request = request.newBuilder().post(builder.build()).build()
//                }
//                is MultipartBody -> {
//                    //目前不做任何事情，等待需求后补充
//                }
//                else -> {
//                    //do nothing
//                }
//            }
//        }
//        val response = chain.proceed(request)
//        if (response.code() != 200) {
//            return response
//        }
//        try {
//            val responseBody = response.body()
//            val source = responseBody!!.source()
//            source.request(java.lang.Long.MAX_VALUE) // Buffer the entire body.
//            val buffer = source.buffer()
//            var charset = UTF8
//            val contentType = responseBody.contentType()
//            if (contentType != null) {
//                charset = contentType.charset(UTF8)
//            }
//            val contentLength = responseBody.contentLength()
//            if (isPlaintext(buffer) && contentLength != 0) {
//                var code = -1
//                var visitorStatus: Int? = -1
//                var sessionKey: String? = null
//                var shopUrl: String? = null
//                try {
//                    val json = buffer.clone().readString(charset)
//                    val jsonObject = JSONObject(json)
//                    code = jsonObject.optInt("code")
//                    sessionKey = jsonObject.optString(SESSION_KEY)
//                    visitorStatus = jsonObject.optInt(VISITOR_STATUS)
//                    shopUrl = jsonObject.optString(SHOP_URL)
//                    if (DVDTextUtils.isEmptyOrNullString(sessionKey) || DVDTextUtils.isEmptyOrNullString(shopUrl)) {
//                        throw IllegalArgumentException("sessionKey = [$sessionKey],shopUrl=[$shopUrl]")
//                    }
//                } finally {
//                    if (code == 10010 || code == 10009 || code == 30000) {
//                        val requestUrl = request.url().toString()
//                        if (mManager != null) {
//                            mLogger.log("intercept: user status changed by [$requestUrl]")
//                            mManager.resetToGuest(requestUrl)
//                        }
//                    }
//                    if (mManager != null && !DVDTextUtils.isEmptyOrNullString(sessionKey) && !DVDTextUtils.isEmptyOrNullString(shopUrl) && visitorStatus !== -1) {
//                        val requestUrl = request.url().toString()
//                        mManager.getUserModel().setSessionKeyShopUrlAndVisitorStatus(sessionKey, shopUrl, visitorStatus.toString(), requestUrl)
//                    }
//                }
//            }
//        } catch (aE: Exception) {
//            mLogger.log("intercept: ", aE)
//        }
//
//        return response
//    }
//
//    companion object {
//
//        private val UTF8 = Charset.forName("UTF-8")
//        private val SESSION_KEY = "sess_key"
//        private val VISITOR_STATUS = "visitor_status"
//        private val SHOP_URL = "shop_url"
//        private val SIGN = "sign"
//        private val POST = "POST"
//
//        /**
//         * Returns true if the body in question probably contains human readable text. Uses a small sample
//         * of code points to detect unicode control characters commonly used in binary file signatures.
//         */
//        private fun isPlaintext(buffer: Buffer): Boolean {
//            try {
//                val prefix = Buffer()
//                val byteCount = (if (buffer.size() < 64) buffer.size() else 64).toLong()
//                buffer.copyTo(prefix, 0, byteCount)
//                for (i in 0..15) {
//                    if (prefix.exhausted()) {
//                        break
//                    }
//                    val codePoint = prefix.readUtf8CodePoint()
//                    if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
//                        return false
//                    }
//                }
//                return true
//            } catch (e: EOFException) {
//                return false // Truncated UTF-8 sequence.
//            }
//
//        }
//    }
//}
