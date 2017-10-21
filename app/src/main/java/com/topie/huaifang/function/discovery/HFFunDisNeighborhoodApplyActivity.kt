package com.topie.huaifang.function.discovery

import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.view.ViewGroup
import com.topie.huaifang.HFGetFileActivity
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.kIsEmpty
import com.topie.huaifang.extensions.kParseFileUri
import com.topie.huaifang.extensions.kToastShort
import com.topie.huaifang.extensions.log
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunLiveBazaarApplyRequestBody
import com.topie.huaifang.http.subscribeResultOkApi
import com.topie.huaifang.imageloader.HFImageView
import com.topie.huaifang.util.HFDimensUtils
import kotlinx.android.synthetic.main.function_dis_neighborhood_apply_activity.*
import java.io.File

/**
 * Created by arman on 2017/10/7.
 * 发现，邻里圈
 */
class HFFunDisNeighborhoodApplyActivity : HFBaseTitleActivity() {

    private val repairsImages: MutableMap<String, String> = hashMapOf()
    private val backgroundThread = HandlerThread("updateImage").also { it.start() }

    private val handler = object : Handler(backgroundThread.looper) {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            for (key in repairsImages.keys) {
                repairsImages[key]?.takeIf { it.isNotEmpty() } ?: let {
                    HFRetrofit.hfService.uploadImage(File(key)).subscribe({
                        if (it.resultOk) {
                            it.data?.attachmentUrl?.also {
                                log("put [$key] to [$it]")
                                repairsImages.put(key, it)
                            } ?: log("unknown reason, json = ${it.json}")
                        } else {
                            it.convertMessage().kToastShort()
                        }
                    }, {
                        "图片上传失败请重试".kToastShort()
                        log("uploadImage", it)
                    })
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.function_dis_neighborhood_apply_activity)
        setBaseTitle("发话题")
        ll_fun_dis_neighborhood_apply_select_img.setOnClickListener {
            HFGetFileActivity.getImage {
                it.firstOrNull()?.let {
                    val value = repairsImages[it] ?: ""
                    repairsImages.put(it, value)
                    handler.sendEmptyMessage(100)
                    refreshView()
                }
            }
        }

        tv_fun_dis_neighborhood_apply_submit.setOnClickListener {
            try {
                tv_fun_dis_neighborhood_apply_submit?.isEnabled = false
                HFRetrofit.hfService.postFunDisNeighborhood(getBazaar()).subscribeResultOkApi({
                    kToastShort("提交成功")
                    finish()
                }, {
                    tv_fun_dis_neighborhood_apply_submit?.isEnabled = true
                })
            } catch (e: Exception) {
                tv_fun_dis_neighborhood_apply_submit?.isEnabled = true
                e.message?.kToastShort()
            }
        }
    }

    private fun refreshView() {
        val llImages = ll_fun_dis_neighborhood_apply_images ?: return
        llImages.removeAllViews()
        repairsImages.forEach {
            val hfImageView = HFImageView(this)
            val lp = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
            lp.topMargin = HFDimensUtils.dp2px(5.toFloat())
            hfImageView.layoutParams = lp
            hfImageView.setAspectRatio(1.toFloat())
            hfImageView.loadImageUri(it.key.kParseFileUri())
            llImages.addView(hfImageView)
        }
    }

    @Throws(IllegalArgumentException::class)
    private fun getBazaar(): HFFunLiveBazaarApplyRequestBody {
        return HFFunLiveBazaarApplyRequestBody().apply {
            //内容
            content = et_fun_dis_neighborhood_apply_content?.text?.toString()?.trim()?.takeIf {
                it.isNotEmpty()
            } ?: throw IllegalArgumentException("请填写内容")
        }.also {
            //检查图片
            repairsImages.forEach {
                if (it.value.kIsEmpty()) {
                    throw IllegalArgumentException("图片正在上传中，请稍后提交")
                }
            }
        }.apply {
            images = repairsImages.values.joinToString()
        }
    }
}