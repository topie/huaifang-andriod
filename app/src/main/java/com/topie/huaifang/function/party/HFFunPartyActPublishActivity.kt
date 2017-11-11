package com.topie.huaifang.function.party

import android.os.Bundle
import android.view.View
import com.topie.huaifang.HFGetFileActivity
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.*
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunPartyActPublishRequestBody
import com.topie.huaifang.http.subscribeResultOkApi
import com.topie.huaifang.view.HFDateDialog
import kotlinx.android.synthetic.main.function_party_act_publish_activity.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by arman on 2017/10/5.
 * 党支部活动发布
 */
class HFFunPartyActPublishActivity : HFBaseTitleActivity() {

    private val dateFormat = SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.getDefault())

    private var startTime = Date()
    private var endTime = Date()

    private var imagePair: Pair<String, String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.function_party_act_publish_activity)
        setBaseTitle("活动发布")
        //开始时间
        ll_fun_party_act_publish_start.setOnClickListener {
            HFDateDialog(this@HFFunPartyActPublishActivity).also {
                it.onDatePick = {
                    startTime = it
                    updateView()
                }
                it.mDate = startTime
            }.show()
        }
        //结束时间
        ll_fun_party_act_publish_end.setOnClickListener {
            HFDateDialog(this@HFFunPartyActPublishActivity).also {
                it.onDatePick = {
                    endTime = it
                    updateView()
                }
                it.mDate = endTime
            }.show()
        }
        //图片
        ll_fun_party_act_publish_img.setOnClickListener {
            HFGetFileActivity.getImage({
                imagePair = it.firstOrNull()?.takeIf { it.isNotEmpty() }?.to("")
                updateView()
                if (imagePair?.first.kIsNotEmpty()) {
                    val path = imagePair!!.first
                    HFRetrofit.hfService.uploadImage(File(path)).subscribeResultOkApi {
                        if (it.data?.attachmentUrl.kIsEmpty()) {
                            //
                        } else if (path == imagePair?.first) {
                            imagePair = path.to(it.data?.attachmentUrl ?: "")
                        }
                    }
                }
            }, 1)
        }
        ll_fun_party_act_publish.setOnClickListener {
            try {
                val requestBody = getRequestBody()
                ll_fun_party_act_publish.isEnabled = false
                HFRetrofit.hfService.postFunPartyActPublish(requestBody.toMap()).subscribeResultOkApi({
                    ll_fun_party_act_publish.isEnabled = true
                    kToastShort("发布成功")
                    finish()
                }, {
                    ll_fun_party_act_publish.isEnabled = true
                })
            } catch (e: Exception) {
                e.message?.kToastShort()
            }
        }
        updateView()
    }

    private fun updateView() {
        tv_fun_party_act_publish_start.text = dateFormat.format(startTime)
        tv_fun_party_act_publish_end.text = dateFormat.format(endTime)

        if (imagePair?.first.kIsEmpty()) {
            iv_fun_party_act_publish_img.visibility = View.GONE
        } else {
            iv_fun_party_act_publish_img.visibility = View.VISIBLE
            iv_fun_party_act_publish_img.loadImageUri(imagePair?.first.kParseFileUri())
        }
    }

    @Throws(IllegalStateException::class)
    private fun getRequestBody(): HFFunPartyActPublishRequestBody {
        val requestBody = HFFunPartyActPublishRequestBody()
        requestBody.topic = et_fun_party_act_publish_topic.text.toString().trim().takeIf {
            it.isNotEmpty()
        } ?: throw IllegalStateException("请填写活动主题")
        et_fun_party_act_publish_content.text.toString().trim().takeIf {
            it.isNotEmpty()
        } ?: throw IllegalStateException("请填写活动内容")
        requestBody.address = et_fun_party_act_publish_address.text.toString().trim()
        requestBody.publishUser = et_fun_party_act_publish_publisher.text.toString().trim().takeIf {
            it.isNotEmpty()
        } ?: throw IllegalStateException("请填写发布者")
        requestBody.image = imagePair?.second?.takeIf {
            it.isNotEmpty()
        } ?: throw IllegalStateException("请上传图片")
        requestBody.beginTime = startTime.kToSimpleFormat()
        requestBody.endTime = endTime.kToSimpleFormat()
        return requestBody
    }
}