package com.topie.huaifang.function.party

import android.os.Bundle
import android.view.View
import com.topie.huaifang.HFGetFileActivity
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.kIsEmpty
import com.topie.huaifang.extensions.kParseUrl
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.subscribeResultOkApi
import com.topie.huaifang.view.HFDateDialog
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission
import com.yanzhenjie.permission.PermissionNo
import com.yanzhenjie.permission.PermissionYes
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

    private var imgUrl: String? = null
    private var enclosureUrl: String? = null
    private var enclosureName: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndPermission.with(this)
                .requestCode(100)
                .permission(Permission.STORAGE)
                .rationale { _, rationale ->
                    AndPermission.rationaleDialog(this@HFFunPartyActPublishActivity, rationale).show()
                }
                .callback(this)
                .start()
    }

    @PermissionNo(100)
    private fun onPermissionNo(deniedPermissions: List<String>) {
        finish()
    }

    @PermissionYes(100)
    private fun onPermissionYes(grantedPermissions: List<String>) {
        onCreateSupper()
    }

    private fun onCreateSupper() {
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
            HFGetFileActivity.takePicture {
                val firstOrNull = it.firstOrNull()?.takeIf { it.isNotEmpty() }
                if (firstOrNull == null) {
                    imgUrl = null
                    updateView()
                } else {
                    HFRetrofit.hfService.uploadFile(File(firstOrNull)).subscribeResultOkApi {
                        if (it.data?.attachmentUrl.kIsEmpty()) {
                            imgUrl = null
                            updateView()
                        } else {
                            imgUrl = it.data?.attachmentUrl
                            updateView()
                        }
                    }
                }
            }
        }

        //附件
        ll_fun_party_act_publish_enclosure.setOnClickListener {
            HFGetFileActivity.getFileFromProvider {
                val firstOrNull = it.firstOrNull()?.takeIf { it.isNotEmpty() }
                if (firstOrNull == null) {
                    enclosureUrl = null
                    enclosureName = null
                    updateView()
                } else {
                    HFRetrofit.hfService.uploadFile(File(firstOrNull)).subscribeResultOkApi {
                        if (it.data?.attachmentUrl.kIsEmpty()) {
                            enclosureUrl = null
                            enclosureName = null
                            updateView()
                        } else {
                            enclosureUrl = it.data?.attachmentUrl
                            enclosureName = it.data?.attachmentName
                            updateView()
                        }
                    }
                }
            }
        }
        updateView()
    }

    private fun updateView() {
        tv_fun_party_act_publish_start.text = dateFormat.format(startTime)
        tv_fun_party_act_publish_end.text = dateFormat.format(endTime)

        if (imgUrl == null) {
            iv_fun_party_act_publish_img.visibility = View.GONE
        } else {
            iv_fun_party_act_publish_img.visibility = View.VISIBLE
            iv_fun_party_act_publish_img.loadImageUri(imgUrl.kParseUrl())
        }

        tv_fun_party_act_enclosure.text = enclosureName
    }
}