package com.topie.huaifang.function.advice

import android.os.Bundle
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.kToSimpleFormat
import com.topie.huaifang.extensions.kToastShort
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunAdviceRequestBody
import com.topie.huaifang.http.subscribeResultOkApi
import kotlinx.android.synthetic.main.base_title_layout.*
import kotlinx.android.synthetic.main.function_advice_publish_activity.*
import java.util.*

/**
 * Created by arman on 2017/10/26.
 * 发表意见
 */
class HFFunAdvicePublishActivity : HFBaseTitleActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.function_advice_publish_activity)
        setBaseTitle("我要提意见")
        setBaseTitleRight("发布")
        tv_advice_contact_time.text = Date().kToSimpleFormat()
        tv_base_title_right.setOnClickListener {
            val trim = et_advice_contact_content.text.toString().trim()
            if (trim.isEmpty()) {
                kToastShort("请填写您的意见或建议")
                return@setOnClickListener
            }
            HFRetrofit.hfService.postFunAdvice(HFFunAdviceRequestBody(trim)).subscribeResultOkApi {
                kToastShort("发表成功")
                finish()
            }
        }
    }
}