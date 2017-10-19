package com.topie.huaifang.function.guide

import android.os.Bundle
import android.text.Html
import android.widget.LinearLayout
import android.widget.TextView
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.kFindViewById
import com.topie.huaifang.extensions.kInflate
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
        //内容
        ll_fun_guide_content.removeAllViews()
        val obj = { parent: LinearLayout, subject: String, content: String ->
            val kInflate = parent.kInflate(R.layout.function_guide_detail_item)
            kInflate.kFindViewById<TextView>(R.id.tv_fun_guide_detail_subject).text = subject
            kInflate.kFindViewById<TextView>(R.id.tv_fun_guide_detail_content).text = Html.fromHtml(content)
            parent.addView(kInflate)
        }
        obj(ll_fun_guide_content, "办事条件：", data?.actionCondition ?: "")
        obj(ll_fun_guide_content, "办事材料：", data?.actionMaterial ?: "")
        obj(ll_fun_guide_content, "办事地址：", data?.actionAddress ?: "")
        obj(ll_fun_guide_content, "流程：", data?.actionFlow ?: "")
        obj(ll_fun_guide_content, "时间范围：", (data?.actionBegin ?: "") + " - " + (data?.actionEnd ?: ""))
        obj(ll_fun_guide_content, "办事费用：", data?.actionFee ?: "")
        obj(ll_fun_guide_content, "办事依据：", data?.actionYiju ?: "")

    }

    override fun onResume() {
        super.onResume()
        HFRetrofit.hfService.getFunGuideDetail(mId).subscribeResultOkApi {
            initData(it.data)
        }.kInto(pauseDisableList)
    }
}