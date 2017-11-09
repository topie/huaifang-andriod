package com.topie.huaifang.function.party

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Html
import android.view.View
import com.davdain.tools.acp.AcpListener
import com.davdain.tools.acp.AcpOptions
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.kInto
import com.topie.huaifang.extensions.kIsNotEmpty
import com.topie.huaifang.extensions.kToastShort
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunPartyPublicResponseBody
import com.topie.huaifang.http.subscribeResultOkApi
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
        initView(mData)
    }

    private fun initView(aData: HFFunPartyPublicResponseBody.ListData?) {
        if (isFinishing) {
            return
        }
        setBaseTitle(aData?.title)
        tv_fun_party_public_detail_title.text = aData?.title
        tv_fun_party_public_detail_time.text = aData?.publishTime
        tv_fun_party_public_detail_publisher.text = aData?.publishUser
        tv_fun_party_public_detail_content.text = aData?.content?.let { Html.fromHtml(it) }
        tv_fun_party_public_detail_read.text = aData?.readCount?.toString() ?: "0"
        tv_fun_party_public_detail_download.visibility = aData?.file
                ?.takeIf { it.kIsNotEmpty() }
                ?.let { View.VISIBLE }
                ?: View.GONE
        tv_fun_party_public_detail_download.setOnClickListener {
            aData?.file?.takeIf { it.kIsNotEmpty() } ?: return@setOnClickListener
            if (!Environment.isExternalStorageEmulated()) {
                kToastShort("手机没有存储卡")
                return@setOnClickListener
            }
            AcpOptions()
                    .apply {
                        permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        } else {
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        }
                    }
                    .request(object : AcpListener {
                        override fun onGranted() {
                            val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                            HFRetrofit.hfService.downloadFile(aData.file!!, directory.absolutePath, {}, {
                                kToastShort("文件下载成功")
                            }, {}).kInto(pauseDisableList)
                        }

                        override fun onDenied(permissions: List<String>) {
                            //do nothing
                        }

                    })
        }
    }

    override fun onResume() {
        super.onResume()
        HFRetrofit.hfService.getFunPartyPublicDetail(mData?.id ?: -1).subscribeResultOkApi {
            initView(it.data)
        }.kInto(pauseDisableList)
    }
}