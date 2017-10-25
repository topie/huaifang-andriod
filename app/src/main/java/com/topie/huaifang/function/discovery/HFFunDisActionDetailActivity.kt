package com.topie.huaifang.function.discovery

import android.os.Bundle
import android.text.Html
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.kInto
import com.topie.huaifang.extensions.kIsNotNull
import com.topie.huaifang.extensions.kParseUrl
import com.topie.huaifang.extensions.kToastShort
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunDisActionListResponseBody
import com.topie.huaifang.http.bean.function.HFFunPartyActResponseBody
import com.topie.huaifang.http.subscribeResultOkApi
import kotlinx.android.synthetic.main.function_party_act_detail_activity.*

/**
 * Created by arman on 2017/10/3.
 * 社区党建-党支部活动
 */
class HFFunDisActionDetailActivity : HFBaseTitleActivity() {

    private var mDetail: HFFunDisActionListResponseBody.ListData? = null

    companion object {
        const val EXTRA_DETAIL = "extra_detail"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.function_party_act_detail_activity)
        mDetail = intent.getSerializableExtra(EXTRA_DETAIL) as HFFunDisActionListResponseBody.ListData?
        initData(mDetail)
    }

    override fun onResume() {
        super.onResume()
        HFRetrofit.hfService.getFunDisActionDetail(mDetail?.id ?: -1).subscribeResultOkApi {
            initData(it.data)
        }.kInto(pauseDisableList)
    }

    private fun initData(detail: HFFunDisActionListResponseBody.ListData?) {
        setBaseTitle(detail?.title)
        iv_fun_party_detail_img.loadImageUri(detail?.image?.kParseUrl())
        tv_fun_party_act_title.text = detail?.title
        tv_fun_party_act_organ.text = ""
        tv_fun_party_act_time.text = detail?.beginTime
        tv_fun_party_act_address.text = detail?.address
        tv_fun_party_act_publisher.text = detail?.publishUser
        tv_fun_party_act_read.text = detail?.total?.toString()?.let { it + "人已参加" }
        tv_fun_party_act_content.text = detail?.content?.let { Html.fromHtml(it) }
        fl_fun_party_act_join.setOnClickListener {
            HFRetrofit.hfService.postFunPartyAct(detail?.id ?: -1).subscribeResultOkApi {
                kToastShort("报名成功")
                finish()
            }.kInto(pauseDisableList)
        }
        fl_fun_party_act_join.isEnabled = detail
                ?.takeIf { HFFunPartyActResponseBody.ListData.STATUS_GOING == it.status }//活动进行中
                ?.takeIf { !it.hasJoin }//还没有报名参加
                .kIsNotNull()
    }
}