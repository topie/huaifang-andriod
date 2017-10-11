package com.topie.huaifang.function.live

import android.os.Bundle
import android.view.ViewGroup
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.kParseUrl
import com.topie.huaifang.function.kShowTelDialog
import com.topie.huaifang.http.bean.function.HFFunLiveBazaarResponseBody
import com.topie.huaifang.imageloader.HFImageView
import com.topie.huaifang.util.HFDimensUtils
import kotlinx.android.synthetic.main.function_live_bazaar_list_item.*

/**
 * Created by arman on 2017/10/10.
 * 社区集市详情
 */
class HFFunLiveBazaarDetailActivity : HFBaseTitleActivity() {

    private var mData: HFFunLiveBazaarResponseBody.ListData? = null

    companion object {
        const val EXTRA_DATA = "extra_data"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.function_live_bazaar_detail_activity)
        setBaseTitle("话题")
        mData = intent.getSerializableExtra(EXTRA_DATA) as HFFunLiveBazaarResponseBody.ListData?
        tv_fun_live_bazaar_name.text = mData?.addUserName
        tv_fun_live_bazaar_time.text = mData?.publishTime
        mData?.images?.split(",")?.forEach {
            val hfImageView = HFImageView(this)
            val lp = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
            lp.topMargin = HFDimensUtils.dp2px(5.toFloat())
            hfImageView.layoutParams = lp
            hfImageView.setAspectRatio(1.toFloat())
            hfImageView.loadImageUri(it.trim().kParseUrl())
            ll_fun_live_bazaar_content.addView(hfImageView)
        }
        ll_fun_live_bazaar_tel.setOnClickListener {
            mData?.contactPhone?.let {
                kShowTelDialog(it)
            }
        }
    }
}