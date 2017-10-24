package com.topie.huaifang.function.discovery

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import com.topie.huaifang.HFGetFileActivity
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.*
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunDisActionPublishRequestBody
import com.topie.huaifang.http.subscribeResultOkApi
import com.topie.huaifang.util.HFDimensUtils
import com.topie.huaifang.view.HFDateDialog
import kotlinx.android.synthetic.main.function_dis_action_type_dialog.*
import kotlinx.android.synthetic.main.function_party_action_publish_activity.*
import java.io.File
import java.util.*

/**
 * Created by arman on 2017/10/22.
 * 发现--活动
 */
class HFFunDisActionPublishActivity : HFBaseTitleActivity() {

    private var startTime: Date? = null
    private var endTime: Date? = null
    private var applyEndTime: Date? = null
    private var actionType: String? = null
    private var imagePair: Pair<String, String>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.function_party_action_publish_activity)
        setBaseTitle("活动发布")
        ll_fun_party_action_publish_type.setOnClickListener {
            TypeDialog(this@HFFunDisActionPublishActivity, { position, str ->
                actionType = str
                tv_fun_party_action_publish_type?.text = str
            })
        }
        //开始时间
        ll_fun_party_action_publish_start.setOnClickListener {
            HFDateDialog(this@HFFunDisActionPublishActivity).also {
                it.onDatePick = {
                    startTime = it
                    updateView()
                }
                it.mDate = startTime ?: Date()
            }.show()
        }
        //结束时间
        ll_fun_party_action_publish_end.setOnClickListener {
            HFDateDialog(this@HFFunDisActionPublishActivity).also {
                it.onDatePick = {
                    endTime = it
                    updateView()
                }
                it.mDate = endTime ?: Date()
            }.show()
        }
        //报名截止
        ll_fun_party_action_publish_apply_end.setOnClickListener {
            HFDateDialog(this@HFFunDisActionPublishActivity).also {
                it.onDatePick = {
                    applyEndTime = it
                    updateView()
                }
                it.mDate = applyEndTime ?: Date()
            }.show()
        }
        //图片
        ll_fun_party_action_publish_img.setOnClickListener {
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
        ll_fun_party_action_publish.setOnClickListener {
            try {
                val requestBody = getRequestBody()
                ll_fun_party_action_publish.isEnabled = false
                HFRetrofit.hfService.postFunDisAction(requestBody.toMap()).subscribeResultOkApi({
                    ll_fun_party_action_publish.isEnabled = true
                    kToastShort("发布成功")
                    finish()
                }, {
                    ll_fun_party_action_publish.isEnabled = true
                })
            } catch (e: Exception) {
                e.message?.kToastShort()
            }
        }
        updateView()
    }

    private fun updateView() {
        tv_fun_party_action_publish_start.text = startTime?.kToSimpleFormat()
        tv_fun_party_action_publish_end.text = endTime?.kToSimpleFormat()
        tv_fun_party_action_publish_apply_end.text = applyEndTime?.kToSimpleFormat()

        if (imagePair?.first.kIsEmpty()) {
            iv_fun_party_action_publish_img.visibility = View.GONE
        } else {
            iv_fun_party_action_publish_img.visibility = View.VISIBLE
            iv_fun_party_action_publish_img.loadImageUri(imagePair?.first.kParseFileUri())
        }
    }

    @Throws(IllegalStateException::class)
    private fun getRequestBody(): HFFunDisActionPublishRequestBody {
        val requestBody = HFFunDisActionPublishRequestBody()
        //活动主题
        requestBody.topic = et_fun_party_action_publish_topic.text.toString().trim().takeIf {
            it.isNotEmpty()
        } ?: throw IllegalStateException("请填写活动主题")
        //活动类型
        requestBody.type = actionType ?: throw IllegalStateException("请填选择活动类型")
        //活动开始时间
        requestBody.beginTime = startTime?.kToSimpleFormat()
        //活动结束时间
        requestBody.endTime = endTime?.kToSimpleFormat()
        //活动报名结束时间
        requestBody.applyEndTime = applyEndTime?.kToSimpleFormat()
        //活动人数显示
        requestBody.limit = et_fun_party_action_publish_limit.text.toString().trim().toIntOrNull() ?: 0
        //活动地点
        requestBody.address = et_fun_party_action_publish_address.text.toString().trim()
        //活动内容
        et_fun_party_action_publish_content.text.toString().trim().takeIf {
            it.isNotEmpty()
        } ?: throw IllegalStateException("请填写活动内容")
        //活动图片
        requestBody.image = imagePair?.second?.takeIf {
            it.isNotEmpty()
        } ?: throw IllegalStateException("请上传图片")
        return requestBody
    }

    private class TypeDialog(context: Context?, val onItemSelect: ((position: Int, str: String) -> Unit)) : Dialog(context) {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.function_dis_action_type_dialog)
            rb_fun_dis_action_type0.setOnClickListener {
                onItemSelect.invoke(0, (it as RadioButton).text.toString())
            }
            rb_fun_dis_action_type1.setOnClickListener {
                onItemSelect.invoke(1, (it as RadioButton).text.toString())
            }
            rb_fun_dis_action_type2.setOnClickListener {
                onItemSelect.invoke(2, (it as RadioButton).text.toString())
            }
            rb_fun_dis_action_type3.setOnClickListener {
                onItemSelect.invoke(3, (it as RadioButton).text.toString())
            }

            setWindowScreenWidth()
        }

        private fun setWindowScreenWidth() {
            val p = window.attributes
            val display = window.windowManager.defaultDisplay
            p.width = Math.min(display.width, HFDimensUtils.dp2px(300.toFloat()))
            window.attributes = p
        }
    }
}