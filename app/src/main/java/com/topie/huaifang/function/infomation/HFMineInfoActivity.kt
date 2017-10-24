package com.topie.huaifang.function.infomation

import android.os.Bundle
import com.topie.huaifang.HFGetFileActivity
import com.topie.huaifang.R
import com.topie.huaifang.account.HFAccountManager
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.kParseUrl
import com.topie.huaifang.extensions.kStartActivity
import com.topie.huaifang.extensions.kToastShort
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.account.HFAccountUserInfoRequestBody
import com.topie.huaifang.http.subscribeResultOkApi
import kotlinx.android.synthetic.main.mine_info_activity.*
import java.io.File
import java.io.IOException

/**
 * Created by arman on 2017/10/24.
 * 编辑资料
 */
class HFMineInfoActivity : HFBaseTitleActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_info_activity)
        setBaseTitle("编辑资料")
        initView()
        ll_mine_info_head.setOnClickListener {
            HFGetFileActivity.getImage({
                val path = it.firstOrNull() ?: return@getImage
                HFRetrofit.hfService.uploadImage(File(path)).flatMap {
                    if (!it.resultOk || it.data?.attachmentUrl == null) {
                        throw IOException("图片上传失败")
                    }
                    val attachmentUrl = it.data?.attachmentUrl
                    HFRetrofit.hfService.postAccountInfo(HFAccountUserInfoRequestBody(attachmentUrl, null))
                }.subscribeResultOkApi {
                    kToastShort("头像上传成功")
                    HFAccountManager.refreshAccountData { initView() }
                }
            }, 1)
        }

        ll_mine_info_nickname.setOnClickListener {
            this@HFMineInfoActivity.kStartActivity(HFMineInfoNicknameActivity::class.java)
        }
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    private fun initView() {
        iv_mine_info_head?.loadImageUri(HFAccountManager.accountModel.userInfo?.headImage.kParseUrl())
        tv_mine_info_id?.text = HFAccountManager.accountModel.userInfo?.id?.toString()
        tv_mine_info_nickname?.text = HFAccountManager.accountModel.userInfo?.nickname
    }
}