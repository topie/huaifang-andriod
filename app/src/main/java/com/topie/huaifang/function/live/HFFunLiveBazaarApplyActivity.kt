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
import com.topie.huaifang.extensions.kParseUrl
import com.topie.huaifang.extensions.kToastShort
import com.topie.huaifang.extensions.log
import com.topie.huaifang.global.RequestCode
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunLiveBazaarApplyRequestBody
import com.topie.huaifang.http.subscribeResultOkApi
import com.topie.huaifang.view.HFImagesUploadLayout
import kotlinx.android.synthetic.main.function_live_bazaar_apply_activity.*
import kotlinx.android.synthetic.main.function_live_repairs_apply_activity.*
import java.io.File

/**
 * Created by arman on 2017/10/7.
 * 集市发布
 */
class HFFunLiveBazaarApplyActivity : HFBaseTitleActivity() {

    private var mTopic: Pair<String, String?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.function_live_bazaar_apply_activity)
        setBaseTitle("发话题")
        ul_fun_live_bazaar_apply_select_img.setOnItemClickListener(object : HFImagesUploadLayout.OnItemClickListener {
            override fun onAdd() {
                HFGetFileActivity.getImage({
                    it.forEach {
                        ul_fun_live_bazaar_apply_select_img?.addPath(it)
                    }
                    refreshView()
                }, 8 - ul_fun_live_bazaar_apply_select_img.getImageSize())
            }

            override fun onImageClicked(uri: Uri?, position: Int) {
                val arrayList = ul_fun_live_bazaar_apply_select_img.getResultPairs().map { it.first }
                ImageBrowserActivity.openImageBrowserActivity(this@HFFunLiveBazaarApplyActivity, RequestCode.IMAGE_BROWSER, arrayList.size, arrayList, arrayList, position)
            }
        })

        iv_fun_live_bazaar_topic.setOnClickListener {
            HFGetFileActivity.getImage({
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
            }, 1)
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
            addUserName = et_fun_live_bazaar_apply_name?.text?.toString()?.trim()?.takeIf {
                it.isNotEmpty()
            } ?: throw IllegalArgumentException("请填写用户名称")
            //内容
            content = et_fun_live_bazaar_apply_content?.text?.toString()?.trim()?.takeIf {
                it.isNotEmpty()
            } ?: throw IllegalArgumentException("请填写内容")
        }.also {
            //检查图片
            val list = ul_fun_live_bazaar_apply_select_img.getResultPairs()
            list.forEach {
                if (it.second.kIsEmpty()) {
                    throw IllegalArgumentException("图片正在上传中，请稍后提交")
                }
            }
            it.images = list.map { it.second }.joinToString()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCode.IMAGE_BROWSER && resultCode == Activity.RESULT_OK) {
            val arrayList = data?.getStringArrayListExtra(ImageBrowserActivity.EXTRA_SELECT_LIST)
            ul_fun_live_repairs_apply_select_img.setPathList(arrayList)
            refreshView()
        }
    }
}