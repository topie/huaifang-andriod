package com.topie.huaifang.http.bean.function

import com.topie.huaifang.http.bean.HFBaseResponseBody

/**
 * Created by arman on 2017/10/7.
 * 物业报修提交
 */
class HFFunLiveRepairsApplyRequestBody : HFBaseResponseBody() {
    var roomNumber: String? = null
    var contactPhone: String? = null
    var contactPerson: String? = null
    var reportTime: String? = null
    var reportTitle: String? = null
    var images: String? = null
    var reportContent: String? = null
}