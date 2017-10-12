package com.topie.huaifang.function.notice

import android.os.Bundle
import android.text.Html
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.kInto
import com.topie.huaifang.extensions.kIsEmpty
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunPublicResponseBody
import com.topie.huaifang.http.subscribeResultOkApi
import kotlinx.android.synthetic.main.function_live_note_detail_activity.*

/**
 * Created by arman on 2017/10/12.
 * 通知公告详细内容
 */
class HFFunNoteDetailActivity : HFBaseTitleActivity() {

    private var mData: HFFunPublicResponseBody.ListData? = null

    companion object {
        const val EXTRA_DATA = "extra_data"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mData = intent.getSerializableExtra(EXTRA_DATA) as HFFunPublicResponseBody.ListData?
        setContentView(R.layout.function_live_note_detail_activity)
        setBaseTitle(mData?.title)
        initData(mData)
    }

    private fun initData(aData: HFFunPublicResponseBody.ListData?) {
        tv_fun_live_public_title.text = aData?.title
        tv_fun_live_public_time.text = aData?.cTime
        tv_fun_live_public_publisher.text = aData?.cUser
        tv_fun_live_public_content.text = aData?.content?.let { Html.fromHtml(it) }
    }

    override fun onResume() {
        super.onResume()
        if (mData?.content.kIsEmpty()) {
            HFRetrofit.hfService.getFunPublicDetail(mData?.id ?: -1).subscribeResultOkApi {
                mData = it.data
                initData(mData)
            }.kInto(pauseDisableList)
        }
    }
}