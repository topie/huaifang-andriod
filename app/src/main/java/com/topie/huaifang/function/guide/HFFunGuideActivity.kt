package com.topie.huaifang.function.guide

import android.os.Bundle
import com.topie.huaifang.HFBaseTitleActivity
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.composeApi

/**
 * Created by arman on 2017/9/21.
 * 办事指南
 */
class HFFunGuideActivity : HFBaseTitleActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HFRetrofit.hfService.getFunGuideMenu().composeApi().subscribe({
            it.data?.firstOrNull()?.let {

            }
        }, {

        })
    }
}