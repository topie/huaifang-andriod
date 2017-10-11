package com.topie.huaifang.http.bean.function

/**
 * Created by arman on 2017/10/11.
 * 身份认证
 */
class HFFunIdentityEditRequestBody {

    var houseId: String? = null
    var personType: Int = 0 //0住户，1租户
    var name: String? = null
    var identifyNumber: String? = null

}