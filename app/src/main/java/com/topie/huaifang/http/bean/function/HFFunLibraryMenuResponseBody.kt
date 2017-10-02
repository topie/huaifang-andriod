package com.topie.huaifang.http.bean.function

import com.topie.huaifang.http.bean.HFBaseResponseBody

/**
 * Created by arman on 2017/10/2.
 * 社区图书馆
 */
class HFFunLibraryMenuResponseBody : HFBaseResponseBody() {

    var data: BodyData? = null

    class BodyData {
        var total: Int? = null
        var data: List<String>? = null
    }
}