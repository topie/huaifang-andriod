package com.topie.huaifang.http.bean.communication

import com.topie.huaifang.http.bean.HFBaseResponseBody

/**
 * Created by arman on 2017/9/24.
 * 朋友列表
 */
class HFCommFriendsResponseBody : HFBaseResponseBody() {

    var data: BodyData? = null

    class BodyData {
        var data: List<HFCommUserInfo>? = null
    }

}
