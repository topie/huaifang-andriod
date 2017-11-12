package com.arman.tools.acp

import android.content.Context

/**
 * Created by hupei on 2016/4/26.
 */
class Acp private constructor(context: Context) {

    internal val acpManager: AcpManager = AcpManager(context.applicationContext)

    companion object {

        var mInstance: Acp? = null
            get() {
                if (field == null) {
                    throw IllegalStateException("acp has not init")
                }
                return field
            }
            private set

        fun init(context: Context) {
            try {
                mInstance
            } catch (e: Exception) {
                mInstance = Acp(context)
            }
        }
    }
}
