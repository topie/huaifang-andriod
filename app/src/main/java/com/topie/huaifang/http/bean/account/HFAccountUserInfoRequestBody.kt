package com.topie.huaifang.http.bean.account

import com.topie.huaifang.extensions.kIsNotEmpty

/**
 * Created by arman on 2017/10/24.
 * 个人信息编辑
 */
class HFAccountUserInfoRequestBody(val headImage: String?, val nickName: String?) {
    fun toMap(): HashMap<String, String> {
        val hashMap = HashMap<String, String>()
        if (headImage.kIsNotEmpty()){
            hashMap.put("headImage",headImage!!)
        }
        if (nickName.kIsNotEmpty()){
            hashMap.put("nickName",nickName!!)
        }
        return hashMap
    }
}