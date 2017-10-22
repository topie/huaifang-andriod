package com.topie.huaifang.function.discovery

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.topie.huaifang.HFGetFileActivity
import com.topie.huaifang.ImageBrowserActivity
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.kIsEmpty
import com.topie.huaifang.extensions.kToastShort
import com.topie.huaifang.global.RequestCode
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunLiveBazaarApplyRequestBody
import com.topie.huaifang.http.subscribeResultOkApi
import com.topie.huaifang.view.HFImagesUploadLayout
import kotlinx.android.synthetic.main.function_dis_neighborhood_apply_activity.*

/**
 * Created by arman on 2017/10/7.
 * 发现，邻里圈
 */
class HFFunDisNeighborhoodApplyActivity : HFBaseTitleActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.function_dis_neighborhood_apply_activity)
        setBaseTitle("发话题")
        ul_fun_dis_neighborhood_apply_select_img.setOnItemClickListener(object : HFImagesUploadLayout.OnItemClickListener {
            override fun onAdd() {
                HFGetFileActivity.getImage({
                    it.forEach {
                        ul_fun_dis_neighborhood_apply_select_img?.addPath(it)
                    }
                }, 8 - ul_fun_dis_neighborhood_apply_select_img.getImageSize())
            }

            override fun onImageClicked(uri: Uri?, position: Int) {
                val arrayList = ul_fun_dis_neighborhood_apply_select_img.getResultPairs().map { it.first }
                ImageBrowserActivity.openImageBrowserActivity(this@HFFunDisNeighborhoodApplyActivity, RequestCode.IMAGE_BROWSER, arrayList.size, arrayList, arrayList, position)
            }
        })

        tv_fun_dis_neighborhood_apply_submit.setOnClickListener {
            try {
                val requestBody = getBazaar()
                tv_fun_dis_neighborhood_apply_submit?.isEnabled = false
                HFRetrofit.hfService.postFunDisNeighborhood(requestBody).subscribeResultOkApi({
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

    @Throws(IllegalArgumentException::class)
    private fun getBazaar(): HFFunLiveBazaarApplyRequestBody {
        return HFFunLiveBazaarApplyRequestBody().apply {
            //内容
            content = et_fun_dis_neighborhood_apply_content?.text?.toString()?.trim()?.takeIf {
                it.isNotEmpty()
            } ?: throw IllegalArgumentException("请填写内容")
        }.also {
            //检查图片
            ul_fun_dis_neighborhood_apply_select_img.getResultPairs().forEach {
                if (it.second.kIsEmpty()) {
                    throw IllegalArgumentException("图片正在上传中，请稍后提交")
                }
            }
        }.apply {
            images = ul_fun_dis_neighborhood_apply_select_img.getResultPairs().map { it.second }.joinToString()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCode.IMAGE_BROWSER && resultCode == Activity.RESULT_OK) {
            val arrayList = data?.getStringArrayListExtra(ImageBrowserActivity.EXTRA_SELECT_LIST)
            ul_fun_dis_neighborhood_apply_select_img.setPathList(arrayList)
        }
    }
}