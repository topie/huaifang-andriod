package com.topie.huaifang.account

import com.topie.huaifang.http.bean.account.HFRoomInfo
import com.topie.huaifang.http.bean.account.HFUserInfo

/**
 * Created by arman on 2017/9/21.
 * 账户信息
 */
class HFAccountModel {
    var token: String? = null
    var userInfo: HFUserInfo? = null
    var roomInfo: HFRoomInfo? = null
}