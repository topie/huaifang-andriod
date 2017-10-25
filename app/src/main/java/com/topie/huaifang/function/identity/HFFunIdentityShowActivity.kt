package com.topie.huaifang.function.identity

import android.os.Bundle
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.kInto
import com.topie.huaifang.extensions.kStartActivity
import com.topie.huaifang.extensions.kToastShort
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.account.HFRoomInfo
import com.topie.huaifang.http.subscribeApi
import com.topie.huaifang.view.HFTipDialog
import kotlinx.android.synthetic.main.function_indentity_show_activity.*

/**
 * Created by arman on 2017/10/11.
 * 我的身份信息
 */
class HFFunIdentityShowActivity : HFBaseTitleActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.function_indentity_show_activity)
        setBaseTitle("我的信息")
    }

    override fun onResume() {
        super.onResume()
        HFRetrofit.hfService.getFunIdentityDate().subscribeApi {
            when {
                it.resultOk -> it.data?.also {
                    initData(it)
                } ?: kToastShort("我的信息数据为空")
                it.code == 500 -> toEdit()
                else -> it.convertMessage().kToastShort()
            }
        }.kInto(pauseDisableList)
    }

    private fun initData(data: HFRoomInfo) {
        if (isFinishing) {
            return
        }
        tv_fun_identity_company.text = data.company
        tv_fun_identity_sf.text = data.sf
        tv_fun_identity_name.text = data.name
        tv_fun_identity_number.text = data.idn
        tv_fun_identity_xq.text = data.xq
        tv_fun_identity_lh.text = data.lh
        tv_fun_identity_dy.text = data.dy
        tv_fun_identity_lc.text = data.lc
        tv_fun_identity_mp.text = data.mp
    }

    private fun toEdit() {
        val builder = HFTipDialog.Builder()
        builder.content = "使用该功能需要进行身份认证，立即进行身份认证？?"
        builder.onOkClicked = {
            this@HFFunIdentityShowActivity.kStartActivity(HFFunIdentityEditActivity::class.java)
            finish()
        }
        builder.show(this)

    }
}