package com.topie.huaifang.http.bean.login

import com.topie.huaifang.http.bean.HFBaseResponseBody

/**
 * 检查手机号是否唯一
 */
class HFCheckPhoneResponseBody : HFBaseResponseBody() {
    var data: Boolean = false
}