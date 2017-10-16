package com.topie.huaifang.http.bean.index

import com.topie.huaifang.http.bean.HFBaseResponseBody

/**
 * Created by arman on 2017/10/16.
 * 首页头图
 */
class HFIndexTopImgResponseBody : HFBaseResponseBody() {
    var data: BodyData? = null

    class BodyData {
        var head: String? = null
    }
}