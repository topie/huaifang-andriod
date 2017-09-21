package com.topie.huaifang.util

import android.util.Log
import com.topie.huaifang.BuildConfig
import java.util.logging.Level

/**
 * Created by arman on 2017/9/21.
 */
interface HFLogger {
    fun log(msg: String)
    fun log(msg: String, error: Throwable?)

    companion object {

        private val logger: HFLogger = HFLogger.HFDefaultLogger("HFLogger")

        fun log(msg: String) {
            logger.log(msg)
        }

        fun log(msg: String, error: Throwable?) {
            logger.log(msg, error)
        }
    }

    class HFDefaultLogger(private val tag: String) : HFLogger {

        private val MAX_LOG_LENGTH = 1000
        private var mLevel = if (BuildConfig.DEBUG) Level.INFO else Level.OFF

        override fun log(msg: String) {
            log(msg, null)
        }

        override fun log(msg: String, error: Throwable?) {
            if (mLevel === Level.OFF) {
                return
            }
            val message = msg + '\n' + Log.getStackTraceString(error)
            // Split by line, then ensure each line can fit into Log's maximum length.
            var i = 0
            val length = message.length
            while (i < length) {
                var newline = message.indexOf('\n', i)
                newline = if (newline != -1) newline else length
                do {
                    val end = Math.min(newline, i + MAX_LOG_LENGTH)
                    Log.println(Log.INFO, tag, message.substring(i, end))
                    i = end
                } while (i < newline)
                i++
            }
        }

    }
}