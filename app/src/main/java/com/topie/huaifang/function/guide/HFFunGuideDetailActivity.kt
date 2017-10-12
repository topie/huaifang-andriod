package com.topie.huaifang.function.guide

import android.os.Bundle
import android.text.Html
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.kInto
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunGuideListResponseBody
import com.topie.huaifang.http.subscribeResultOkApi
import kotlinx.android.synthetic.main.function_guide_detail_activity.*

/**
 * Created by arman on 2017/10/12.
 * 办事指南
 */
class HFFunGuideDetailActivity : HFBaseTitleActivity() {

    private var mId: Int = -1

    companion object {
        const val EXTRA_ID = "extra_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mId = intent.getIntExtra(EXTRA_ID, -1)
        setContentView(R.layout.function_guide_detail_activity)
    }

    private fun initData(data: HFFunGuideListResponseBody.ListData?) {
        if (isFinishing) {
            return
        }
        setBaseTitle(data?.title)
        tv_fun_guide_title.text = data?.title
        tv_fun_guide_time.text = data?.publishTime
        tv_fun_guide_publisher.text = data?.publishUser
        tv_fun_guide_content.text = data?.allContent?.let { Html.fromHtml(it) }
    }

    override fun onResume() {
        super.onResume()
        HFRetrofit.hfService.getFunGuideDetail(mId).subscribeResultOkApi {
            initData(it.data)
        }.kInto(pauseDisableList)
    }
}