package com.topie.huaifang.http.bean.account

import com.topie.huaifang.http.bean.HFBaseResponseBody

/**
 * Created by arman on 2017/10/17.
 * 用户信息
 */
class HFAccountResponseBody : HFBaseResponseBody() {

    var data: BodyData? = null

    class BodyData {
        var base: HFUserInfo? = null
        var shenfen: HFRoomInfo? = null
    }
}