package com.topie.huaifang.http.bean.function

import com.topie.huaifang.http.bean.HFBaseResponseBody

/**
 * Created by arman on 2017/10/11.
 * 身份认证
 */
class HFFunIdentityResponseBody : HFBaseResponseBody() {

    var data: BodyData? = null

    class BodyData {
        var sf: String? = null  //身份
        var xq: String? = null  //小区
        var lh: String? = null  //楼号
        var dy: String? = null  //单元
        var lc: String? = null  //楼层
        var mp: String? = null  //门牌
        var name: String? = null    //名称
        var idn: String? = null //身份证
    }
}