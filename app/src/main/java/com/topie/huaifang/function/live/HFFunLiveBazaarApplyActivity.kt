package com.topie.huaifang.function.live

import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.view.ViewGroup
import com.topie.huaifang.HFGetFileActivity
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.*
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunLiveBazaarApplyRequestBody
import com.topie.huaifang.http.subscribeApi
import com.topie.huaifang.http.subscribeResultOkApi
import com.topie.huaifang.imageloader.HFImageView
import com.topie.huaifang.util.HFDimensUtils
import kotlinx.android.synthetic.main.function_live_bazaar_apply_activity.*
import kotlinx.android.synthetic.main.function_live_repairs_apply_activity.*
import java.io.File

/**
 * Created by arman on 2017/10/7.
 * 集市发布
 */
class HFFunLiveBazaarApplyActivity : HFBaseTitleActivity() {

    private val repairsImages: MutableMap<String, String> = hashMapOf()
    private val backgroundThread = HandlerThread("updateImage").also { it.start() }
    private var mTopic: Pair<String, String?>? = null

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
        setContentView(R.layout.function_live_bazaar_apply_activity)
        setBaseTitle("发话题")
        ll_fun_live_bazaar_apply_select_img.setOnClickListener {
            HFGetFileActivity.takePicture {
                it.firstOrNull()?.let {
                    val value = repairsImages[it] ?: ""
                    repairsImages.put(it, value)
                    handler.sendEmptyMessage(100)
                    refreshView()
                }
            }
        }

        iv_fun_live_bazaar_topic.setOnClickListener {
            HFGetFileActivity.takePicture {
                it.firstOrNull()?.also { path ->
                    HFRetrofit.hfService.uploadImage(File(path)).subscribeResultOkApi({
                        if (it.resultOk) {
                            it.data?.attachmentUrl?.also {
                                log("put [$path] to [$it]")
                                mTopic = path.to(it)
                                refreshView()
                            } ?: log("unknown reason, json = ${it.json}")
                        } else {
                            it.convertMessage().kToastShort()
                        }
                    })
                } ?: let {
                    mTopic = null
                    refreshView()
                }
            }
        }

        tv_fun_live_bazaar_apply_submit.setOnClickListener {
            try {
                tv_fun_live_bazaar_apply_submit?.isEnabled = false
                HFRetrofit.hfService.postFunLiveBazaar(getBazaar()).subscribeResultOkApi({
                    kToastShort("提交成功")
                    finish()
                }, {
                    tv_fun_live_bazaar_apply_submit?.isEnabled = true
                })
            } catch (e: Exception) {
                tv_fun_live_bazaar_apply_submit?.isEnabled = true
                e.message?.kToastShort()
            }
        }
    }

    private fun refreshView() {
        val llImages = ll_fun_live_bazaar_apply_images ?: return
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
        val ivTopic = iv_fun_live_bazaar_topic ?: return
        ivTopic.loadImageUri(mTopic?.second?.kParseUrl())
    }

    @Throws(IllegalArgumentException::class)
    private fun getBazaar(): HFFunLiveBazaarApplyRequestBody {
        return HFFunLiveBazaarApplyRequestBody().apply {
            //头图
            headImage = mTopic?.second ?: throw IllegalArgumentException("请添加头图")
            //type
            type = when {
                rb_fun_live_bazaar_apply_fleas.isChecked -> 0
                else -> 1
            }
            //联系电话
            contactPhone = et_fun_live_bazaar_apply_tel?.text?.toString()?.trim()?.takeIf {
                it.isNotEmpty()
            } ?: throw IllegalArgumentException("请填写手机号")
            //用户名称
            contactPhone = et_fun_live_bazaar_apply_name?.text?.toString()?.trim()?.takeIf {
                it.isNotEmpty()
            } ?: throw IllegalArgumentException("请填写用户名称")
            //内容
            content = et_fun_live_bazaar_apply_content?.text?.toString()?.trim()?.takeIf {
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