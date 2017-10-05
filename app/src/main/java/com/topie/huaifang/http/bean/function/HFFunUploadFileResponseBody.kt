package com.topie.huaifang.http.bean.function

import com.topie.huaifang.http.bean.HFBaseResponseBody

/**
 * Created by arman on 2017/10/5.
 */
class HFFunUploadFileResponseBody : HFBaseResponseBody() {
    var data: BodyData? = null

    class BodyData {
        var attachmentId: Int = 0
        var attachmentName: String? = null
        var attachmentType: Int = 0
        var attachmentSuffix: String? = null
        var attachmentPath: String? = null
        var attachmentUrl: String? = null
        var attachmentSize: Long = 0.toLong()
        var uploadLoginName: String? = null
    }
}