/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.topie.huaifang.http

import android.util.Log
import com.topie.huaifang.BuildConfig
import com.topie.huaifang.util.HFLogger
import okhttp3.*
import okhttp3.internal.http.HttpHeaders
import okio.Buffer
import java.io.EOFException
import java.io.IOException
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

/**
 * An OkHttp interceptor which logs request and response information. Can be applied as an
 * [application interceptor][OkHttpClient.interceptors] or as a [ ][OkHttpClient.networkInterceptors].
 *
 * The format of the logs created by
 * this class should not be considered stable and may change slightly between releases. If you need
 * a stable logging format, use your own interceptor.
 */
class HFHttpLoggingInterceptor(private val logger: HFLogger) : Interceptor {

    enum class Level {
        /**
         * No logs.
         */
        NONE,
        /**
         * Logs request and response lines.
         *
         * Example:
         * <pre>`--> POST /greeting http/1.1 (3-byte body)
         *
         * <-- 200 OK (22ms, 6-byte body)
        `</pre> *
         */
        BASIC,
        /**
         * Logs request and response lines and their respective headers.
         *
         *
         *
         * Example:
         * <pre>`--> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         * --> END POST
         *
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         * <-- END HTTP
        `</pre> *
         */
        HEADERS,
        /**
         * Logs request and response lines and their respective headers and bodies (if present).
         *
         *
         *
         * Example:
         * <pre>`--> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         *
         * Hi?
         * --> END POST
         *
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         *
         * Hello!
         * <-- END HTTP
        `</pre> *
         */
        BODY
    }

    @Volatile private var level = Level.NONE

    /**
     * Change the level at which this interceptor logs.
     */
    fun setLevel(level: Level?): HFHttpLoggingInterceptor {
        if (level == null) throw NullPointerException("level == null. Use Level.NONE instead.")
        this.level = level
        return this
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!DEBUG) {
            return chain.proceed(chain.request())
        }
        val sb = StringBuilder()
        val response: Response
        try {
            sb.append(STR_LINE).append("STA:").append(System.currentTimeMillis()).append(STR_LINE).append(STR_ENTER)
            val level = this.level

            val request = chain.request()
            if (level == Level.NONE) {
                return chain.proceed(request)
            }

            val logBody = level == Level.BODY
            val logHeaders = logBody || level == Level.HEADERS

            val requestBody = request.body()
            val hasRequestBody = requestBody != null

            val connection = chain.connection()
            val protocol = if (connection != null) connection.protocol() else Protocol.HTTP_1_1
            sb.append(STR_START).append(request.method()).append(STR_SPACE).append(request.url()).append(STR_SPACE).append(protocol).append(STR_ENTER)
            if (!logHeaders && hasRequestBody) {
                sb.append(" (").append(requestBody!!.contentLength()).append("-byte body)")
                sb.append(STR_ENTER)
            }
            if (logHeaders) {
                if (hasRequestBody) {
                    // Request body headers are only present when installed as a network interceptor. Force
                    // them to be included (when available) so there values are known.
                    if (requestBody!!.contentType() != null) {
                        sb.append("Content-Type: ").append(requestBody.contentType()).append(STR_ENTER)
                    }
                    if (requestBody.contentLength() != (-1).toLong()) {
                        sb.append("Content-Length: ").append(requestBody.contentLength()).append(STR_ENTER)
                    }
                }

                val headers = request.headers()
                var i = 0
                val count = headers.size()
                while (i < count) {
                    val name = headers.name(i)
                    // Skip headers from the request body as they are explicitly logged above.
                    if (!"Content-Type".equals(name, ignoreCase = true) && !"Content-Length".equals(name, ignoreCase = true)) {
                        sb.append(name).append(": ").append(headers.value(i)).append(STR_ENTER)
                    }
                    i++
                }
                val isMultiParty = requestBody?.contentType()?.toString()?.contains("multipart/form-data") ?: false
                if (!logBody || !hasRequestBody || isMultiParty) {
                    sb.append(STR_END).append(request.method()).append(STR_ENTER)
                } else if (bodyEncoded(request.headers())) {
                    sb.append(STR_END).append(request.method()).append(" (encoded body omitted)").append(STR_ENTER)
                } else {
                    val buffer = Buffer()
                    requestBody!!.writeTo(buffer)

                    var charset: Charset? = UTF8
                    val contentType = requestBody.contentType()
                    if (contentType != null) {
                        charset = contentType.charset(UTF8)
                    }
                    sb.append(STR_ENTER)
                    if (isPlaintext(buffer)) {
                        sb.append(buffer.readString(charset!!)).append(STR_ENTER)
                        sb.append(STR_END).append(request.method()).append(" (").append(requestBody.contentLength()).append("-byte body)").append(STR_ENTER)
                    } else {
                        sb.append(STR_END).append(request.method()).append(" (binary ").append(requestBody.contentLength()).append("-byte body omitted)").append(STR_ENTER)
                    }
                }
            }

            val startNs = System.nanoTime()
            try {
                response = chain.proceed(request)
                sb.append("-------------------").append(STR_ENTER)
                sb.append("RE TIME:").append(System.currentTimeMillis()).append("|").append(STR_ENTER)
                sb.append("-------------------").append(STR_ENTER)
            } catch (e: IOException) {
                sb.append("-------------------").append(STR_ENTER)
                sb.append("RE TIME:").append(System.currentTimeMillis()).append("|").append(STR_ENTER)
                sb.append("-------------------").append(STR_ENTER)
                sb.append("<-- HTTP FAILED: ").append(Log.getStackTraceString(e)).append(STR_ENTER)
                throw e
            }

            val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)

            val responseBody = response.body()
            val contentLength = responseBody!!.contentLength()
            val bodySize = if (contentLength != (-1).toLong()) contentLength.toString() + "-byte" else "unknown-length"
            sb.append("<-- ").append(response.code()).append(STR_SPACE).append(response.message()).append(STR_SPACE).append(response.request().url()).append(" (").append(tookMs).append("ms").append(if (!logHeaders)
                ", "
                        + bodySize + " body"
            else
                "").append(')').append(STR_ENTER)
            if (logHeaders) {
                val headers = response.headers()
                var i = 0
                val count = headers.size()
                while (i < count) {
                    sb.append(headers.name(i)).append(": ").append(headers.value(i)).append(STR_ENTER)
                    i++
                }

                if (!logBody || !HttpHeaders.hasBody(response)) {
                    sb.append("<-- END HTTP").append(STR_ENTER)
                } else if (bodyEncoded(response.headers())) {
                    sb.append("<-- END HTTP (encoded body omitted)").append(STR_ENTER)
                } else {
                    val source = responseBody.source()
                    source.request(java.lang.Long.MAX_VALUE) // Buffer the entire body.
                    val buffer = source.buffer()

                    var charset: Charset? = UTF8
                    val contentType = responseBody.contentType()
                    if (contentType != null) {
                        charset = contentType.charset(UTF8)
                    }

                    if (!isPlaintext(buffer)) {
                        sb.append(STR_ENTER)
                        sb.append("<-- END HTTP (binary ").append(buffer.size()).append("-byte body omitted)").append(STR_ENTER)
                        return response
                    }

                    if (contentLength != 0.toLong()) {
                        sb.append(STR_ENTER)
                        sb.append(buffer.clone().readString(charset!!)).append(STR_ENTER)
                    }
                    sb.append("<-- END HTTP (").append(buffer.size()).append("-byte body)").append(STR_ENTER)
                }
            }

        } finally {
            sb.append(STR_ENTER).append(STR_LINE).append("END:").append(System.currentTimeMillis()).append(STR_LINE).append(STR_ENTER)
            synchronized(logger) {
                logger.log(sb.toString())
            }
        }
        return response
    }

    private fun bodyEncoded(headers: Headers): Boolean {
        val contentEncoding = headers.get("Content-Encoding")
        return contentEncoding != null && !contentEncoding.equals("identity", ignoreCase = true)
    }

    companion object {
        private val UTF8 = Charset.forName("UTF-8")
        private val STR_START = "--> "
        private val STR_END = "--> END "
        private val STR_SPACE = ' '
        private val STR_ENTER = "\n"
        private val STR_LINE = "----------------------------"
        private val DEBUG = BuildConfig.DEBUG

        /**
         * Returns true if the body in question probably contains human readable text. Uses a small sample
         * of code points to detect unicode control characters commonly used in binary file signatures.
         */
        private fun isPlaintext(buffer: Buffer): Boolean {
            try {
                val prefix = Buffer()
                val byteCount = if (buffer.size() < 64) buffer.size() else 64
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
