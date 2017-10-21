package com.topie.huaifang.function.party

import android.os.Bundle
import android.text.Html
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.kInto
import com.topie.huaifang.extensions.kParseUrl
import com.topie.huaifang.extensions.kToastShort
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunPartyResponseBody
import com.topie.huaifang.http.subscribeResultOkApi
import kotlinx.android.synthetic.main.function_party_act_detail_activity.*

/**
 * Created by arman on 2017/10/3.
 * 社区党建-党支部活动
 */
class HFFunPartyActDetailActivity : HFBaseTitleActivity() {

    private var mDetail: HFFunPartyResponseBody.ListData? = null

    companion object {
        const val EXTRA_DETAIL = "extra_detail"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.function_party_act_detail_activity)
        mDetail = intent.getSerializableExtra(EXTRA_DETAIL) as HFFunPartyResponseBody.ListData?
        setBaseTitle(mDetail?.topic)
        iv_fun_party_detail_img.loadImageUri(mDetail?.image?.kParseUrl())
        tv_fun_party_act_title.text = mDetail?.topic
        tv_fun_party_act_organ.text = ""
        tv_fun_party_act_time.text = mDetail?.beginTime
        tv_fun_party_act_address.text = mDetail?.address
        tv_fun_party_act_publisher.text = mDetail?.publishUser
        tv_fun_party_act_read.text = mDetail?.total?.toString()?.let { it + "人已参加" }
        tv_fun_party_act_content.text = mDetail?.content?.let { Html.fromHtml(it) }
        fl_fun_party_act_join.setOnClickListener {
            HFRetrofit.hfService.postFunPartyAct(mDetail?.id ?: -1).subscribeResultOkApi {
                kToastShort("报名成功")
                finish()
            }.kInto(pauseDisableList)
        }
        fl_fun_party_act_join.isEnabled = (mDetail?.status?.equals(HFFunPartyResponseBody.ListData.STATUS_GOING) ?: false)
    }
}