package com.topie.huaifang.function.infomation

import android.os.Bundle
import com.topie.huaifang.R
import com.topie.huaifang.account.HFAccountManager
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.kToastShort
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.account.HFAccountUserInfoRequestBody
import com.topie.huaifang.http.subscribeResultOkApi
import kotlinx.android.synthetic.main.base_title_layout.*
import kotlinx.android.synthetic.main.mine_info_nickname_activity.*

/**
 * Created by arman on 2017/10/24.
 * 个人信息编辑昵称
 */
class HFMineInfoNicknameActivity : HFBaseTitleActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mine_info_nickname_activity)
        setBaseTitleRight(R.string.default_ok)
        et_mine_info_nickname.setText(HFAccountManager.accountModel.userInfo?.nickname)
        tv_base_title_right.setOnClickListener {
            val nickname = et_mine_info_nickname.text.toString().trim()
            if (nickname.isEmpty()) {
                kToastShort("请输入昵称")
                return@setOnClickListener
            }
            HFRetrofit.hfService.postAccountInfo(HFAccountUserInfoRequestBody(null, nickname)).subscribeResultOkApi {
                HFAccountManager.accountModel.userInfo?.nickname = nickname
                kToastShort("昵称修改成功")
                finish()
            }
        }
    }
}