package com.topie.huaifang

import android.app.Application
import com.topie.huaifang.extensions.log
import me.yokeyword.fragmentation.Fragmentation

/**
 * Created by arman on 2017/7/26.
 */
class HFApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Fragmentation.builder()
                .stackViewMode(if (BuildConfig.DEBUG) {
                    Fragmentation.BUBBLE
                } else {
                    Fragmentation.NONE
                })
                .debug(BuildConfig.DEBUG)
                .handleException({
                    log("handleException", it)
                })
                .install()
    }
}