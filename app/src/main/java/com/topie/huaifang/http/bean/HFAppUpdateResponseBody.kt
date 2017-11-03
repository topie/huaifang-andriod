package com.topie.huaifang.http.bean

/**
 * Created by arman on 2017/10/31.
 */
class HFAppUpdateResponseBody : HFBaseResponseBody() {

    var data: BodyData? = null

    class BodyData {
        var version: String? = null
        var downloadUrl: String? = null
        var forceUpdate: Int = 0
        var publishTime: String? = null
    }
}