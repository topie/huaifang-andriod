package com.topie.huaifang.function.live

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.topie.huaifang.HFGetFileActivity
import com.topie.huaifang.ImageBrowserActivity
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.kIsEmpty
import com.topie.huaifang.extensions.kToSimpleFormat
import com.topie.huaifang.extensions.kToastShort
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunLiveRepairsApplyRequestBody
import com.topie.huaifang.http.subscribeResultOkApi
import com.topie.huaifang.view.HFDateDialog
import com.topie.huaifang.view.HFImagesUploadLayout
import kotlinx.android.synthetic.main.function_live_repairs_apply_activity.*
import java.util.*

/**
 * Created by arman on 2017/10/7.
 * 物业报修 申请
 *
 */
class HFFunLiveRepairsApplyActivity : HFBaseTitleActivity() {

    private var repairsTime: Date? = null

    companion object {
        const val REQUEST_CODE_BROWSER = 200
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
                HFGetFileActivity.getImage({
                    it.forEach {
                        ul_fun_live_repairs_apply_select_img?.addPath(it)
                    }
                    refreshView()
                }, 8 - ul_fun_live_repairs_apply_select_img.getImageSize())
            }

            override fun onImageClicked(uri: Uri?, position: Int) {
                val arrayList = ul_fun_live_repairs_apply_select_img.getResultPairs().map { it.first }
                ImageBrowserActivity.openImageBrowserActivity(this@HFFunLiveRepairsApplyActivity, REQUEST_CODE_BROWSER, arrayList.size, arrayList, arrayList, position)
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

        ul_fun_live_repairs_apply_select_img.getResultPairs().also {
            it.forEach {
                if (it.second.kIsEmpty()) {
                    throw IllegalArgumentException("图片正在上传中，请稍后提交")
                }
            }
        }.let {
            requestBody.images = it.map { it.second }.joinToString()
        }
        return requestBody
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_BROWSER && resultCode == Activity.RESULT_OK) {
            val arrayList = data?.getStringArrayListExtra(ImageBrowserActivity.EXTRA_SELECT_LIST)
            ul_fun_live_repairs_apply_select_img.setPathList(arrayList)
            refreshView()
        }
    }
}