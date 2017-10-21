package com.topie.huaifang.function.live

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.support.v4.util.ArrayMap
import com.topie.huaifang.HFGetFileActivity
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.*
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunLiveRepairsApplyRequestBody
import com.topie.huaifang.http.subscribeResultOkApi
import com.topie.huaifang.view.HFDateDialog
import com.topie.huaifang.view.HFImagesUploadLayout
import kotlinx.android.synthetic.main.function_live_repairs_apply_activity.*
import java.io.File
import java.util.*

/**
 * Created by arman on 2017/10/7.
 * 物业报修 申请
 *
 */
class HFFunLiveRepairsApplyActivity : HFBaseTitleActivity() {

    private var repairsTime: Date? = null
    private val repairsImages: MutableMap<String, String> = ArrayMap()

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
        setContentView(R.layout.function_live_repairs_apply_activity)
        setBaseTitle("物业报修")
        ll_fun_live_repairs_apply_time.setOnClickListener {
            HFDateDialog(this@HFFunLiveRepairsApplyActivity).apply {
                mDate = repairsTime ?: Date()
                onDatePick = {
                    repairsTime = it
                    refreshView()
                }
                show()
            }
        }
        ul_fun_live_repairs_apply_select_img.setOnItemClickListener(object : HFImagesUploadLayout.OnItemClickListener {
            override fun onAdd() {
                HFGetFileActivity.getImage {
                    it.firstOrNull()?.let {
                        val value = repairsImages[it] ?: ""
                        repairsImages.put(it, value)
                        handler.sendEmptyMessage(100)
                        refreshView()
                    }
                }
            }

            override fun onImageClicked(uri: Uri?, position: Int) {

            }

        })
        tv_fun_live_repairs_apply_submit.setOnClickListener {
            try {
                tv_fun_live_repairs_apply_submit?.isEnabled = false
                HFRetrofit.hfService.postFunLiveRepairs(getRepairs()).subscribeResultOkApi({
                    kToastShort("提交成功")
                    finish()
                }, {
                    tv_fun_live_repairs_apply_submit?.isEnabled = true
                })
            } catch (e: Exception) {
                tv_fun_live_repairs_apply_submit?.isEnabled = true
                e.message?.kToastShort()
            }
        }
    }

    private fun refreshView() {
        tv_fun_live_repairs_apply_time.text = repairsTime?.kToSimpleFormat() ?: ""
        val uriList = repairsImages.keys.map { it.kParseFileUri() }
        ul_fun_live_repairs_apply_select_img?.setUriList(uriList)
    }

    @Throws(IllegalArgumentException::class)
    private fun getRepairs(): HFFunLiveRepairsApplyRequestBody {
        val requestBody = HFFunLiveRepairsApplyRequestBody()
        requestBody.roomNumber = et_fun_live_repairs_apply_room?.text?.toString()?.trim()?.takeIf {
            it.isNotEmpty()
        } ?: throw IllegalArgumentException("请填写写房间号")
        requestBody.contactPhone = et_fun_live_repairs_apply_tel?.text?.toString()?.trim()?.takeIf {
            it.isNotEmpty()
        } ?: throw IllegalArgumentException("请填写手机号")
        requestBody.contactPerson = et_fun_live_repairs_apply_name?.text?.toString()?.trim()?.takeIf {
            it.isNotEmpty()
        } ?: throw IllegalArgumentException("请填写联系人")
        requestBody.reportTime = repairsTime?.kToSimpleFormat()?.takeIf {
            it.isNotEmpty()
        } ?: throw IllegalArgumentException("请选择报修时间")
        requestBody.reportTitle = et_fun_live_repairs_apply_title?.text?.toString()?.trim()?.takeIf {
            it.isNotEmpty()
        } ?: throw IllegalArgumentException("请填写报修事项")
        requestBody.reportContent = et_fun_live_repairs_apply_content?.text?.toString()?.trim()?.takeIf {
            it.isNotEmpty()
        } ?: throw IllegalArgumentException("请填写详细报修内容")

        repairsImages.forEach {
            if (it.value.kIsEmpty()) {
                throw IllegalArgumentException("图片正在上传中，请稍后提交")
            }
        }
        requestBody.images = repairsImages.values.joinToString()
        return requestBody
    }
}