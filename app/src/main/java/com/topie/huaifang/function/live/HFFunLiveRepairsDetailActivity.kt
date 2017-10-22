package com.topie.huaifang.function.live

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.topie.huaifang.ImageBrowserActivity
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.kFindViewById
import com.topie.huaifang.extensions.kInflate
import com.topie.huaifang.extensions.kInto
import com.topie.huaifang.extensions.kIsEmpty
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunLiveRepairsListResponseBody
import com.topie.huaifang.http.bean.function.HFFunLiveRepairsProgressResponseBody
import com.topie.huaifang.http.subscribeResultOkApi
import com.topie.huaifang.view.HFImagesLayout
import kotlinx.android.synthetic.main.function_live_repairs_detail.*

/**
 * Created by arman on 2017/10/9.
 * 业务保修详情
 */
class HFFunLiveRepairsDetailActivity : HFBaseTitleActivity() {

    private var mData: HFFunLiveRepairsListResponseBody.ListData? = null
    private var mProgressDataList: List<HFFunLiveRepairsProgressResponseBody.ListData>? = null

    companion object {
        const val EXTRA_DATA = "extra_data"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.function_live_repairs_detail)
        mData = intent.getSerializableExtra(EXTRA_DATA) as HFFunLiveRepairsListResponseBody.ListData?

        tv_fun_live_repairs_title.text = mData?.reportTitle
        tv_fun_live_repairs_new_time.text = mData?.reportTime
        tv_fun_live_repairs_status.text = mData?.status

        tv_fun_live_repairs_detail_title.text = mData?.reportTitle
        tv_fun_live_repairs_room.text = mData?.roomNumber
        tv_fun_live_repairs_name.text = mData?.contactPerson
        tv_fun_live_repairs_tel.text = mData?.contactPhone
        tv_fun_live_repairs_time.text = mData?.reportTime
        tv_fun_live_repairs_apply_content.text = "保修内容：${mData?.reportContent ?: ""}"

        val list = mData?.images?.split(",")?.map {
            //判断url是否相对地址，如果是添加baseUrl
            HFRetrofit.parseUrlToBase(it.trim())
        }
        if (list == null || list.isEmpty()) {
            ll_fun_live_repairs_apply_select_img.visibility = View.GONE
        } else {
            ll_fun_live_repairs_apply_select_img.visibility = View.VISIBLE
            il_fun_live_repairs_images.setPathList(list)
        }
        il_fun_live_repairs_images.setOnItemClickListener(object : HFImagesLayout.OnItemClickListener {

            override fun onAdd() {

            }

            override fun onImageClicked(uri: Uri?, position: Int) {
                ImageBrowserActivity.openImageBrowserActivity(this@HFFunLiveRepairsDetailActivity, 0, -1, list!!.map { it.toString() }, null, position)
            }

        })
    }

    override fun onResume() {
        super.onResume()
        if (mProgressDataList.kIsEmpty()) {
            HFRetrofit.hfService.getFunLiveRepairsProgress(mData?.id ?: -1).subscribeResultOkApi {
                it.data?.data?.let {
                    val ll = ll_fun_live_repairs_progress ?: return@let
                    ll.removeAllViews()
                    it.forEach {
                        val inflate = ll.kInflate(R.layout.function_live_repairs_progress_item)
                        val tvTitle: TextView = inflate.kFindViewById(R.id.tv_fun_live_repairs_progress_name)
                        val tvTime: TextView = inflate.kFindViewById(R.id.tv_fun_live_repairs_progress_time)
                        val tvStatus: TextView = inflate.kFindViewById(R.id.tv_fun_live_repairs_progress_status)
                        val tvRepairerName: TextView = inflate.kFindViewById(R.id.tv_fun_live_repairs_repairer_name)
                        val tvRepairerTel: TextView = inflate.kFindViewById(R.id.tv_fun_live_repairs_repairer_tel)
                        val tvRepairTime: TextView = inflate.kFindViewById(R.id.tv_fun_live_repairs_repair_time)

                        tvTitle.text = it.contactPerson
                        tvTime.text = it.processTime
                        tvStatus.text = it.status

                        tvRepairerName.text = it.contactPerson
                        tvRepairerTel.text = it.contactPhone
                        tvRepairTime.text = it.processTime

                        ll.addView(inflate)
                    }
                    mProgressDataList = it
                }
            }.kInto(pauseDisableList)
        }
    }
}