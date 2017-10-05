package com.topie.huaifang.function.party

import android.os.Bundle
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.kGetExtraFilesDir
import com.topie.huaifang.extensions.kIsEmpty
import com.topie.huaifang.extensions.kToastShort
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunPartyPublicResponseBody
import kotlinx.android.synthetic.main.function_party_public_detail_activity.*

/**
 * Created by arman on 2017/10/5.
 * 党务公开详情
 */
class HFFunPartyPublicDetailActivity : HFBaseTitleActivity() {

    var mData: HFFunPartyPublicResponseBody.ListData? = null

    companion object {
        const val EXTRA_DATA: String = "extra_data"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mData = intent.getSerializableExtra(EXTRA_DATA) as HFFunPartyPublicResponseBody.ListData?
        setContentView(R.layout.function_party_public_detail_activity)
        setBaseTitle("党务公开")
        tv_fun_party_public_detail_title.text = mData?.title
        tv_fun_party_public_detail_time.text = mData?.publishTime
        tv_fun_party_public_detail_publisher.text = mData?.publishUser
        tv_fun_party_public_detail_content.text = mData?.content
        tv_fun_party_public_detail_enclosure.setOnClickListener {
            val dir = kGetExtraFilesDir()
            val fileUrl = mData?.file
            if (dir == null || fileUrl.kIsEmpty()) {
                kToastShort("下载失败")
                return@setOnClickListener
            }
            HFRetrofit.hfService.downloadFile(fileUrl!!, dir.absolutePath, {

            }, {
                kToastShort("下载成功")
            }, {
                kToastShort("下载失败")
            })
        }
    }
}