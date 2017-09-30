package com.topie.huaifang.http.bean.communication

/**
 *  用户信息
 */
class HFCommUserInfo {
    companion object {
        /**
         * 激活
         */
        const val LOGIN_STATUS_VALID: Int = 0
        /**
         * 锁定
         */
        const val LOGIN_STATUS_DISVALID: Int = 1
        /**
         * 未认证
         */
        const val STATUS_VERIFY_NOT = 0
        /**
         * 待审核
         */
        const val STATUS_VERIFY_PREPARE = 1
        /**
         * 审核通过
         */
        const val STATUS_VERIFY_GRANT = 2
        /**
         * 审核未通过
         */
        const val STATUS_VERIFY_DENIED = 3
    }

    var mobilePhone: String? = null
    var nickname: String? = null
    var realname: String? = null
    var headImage: String? = null
    var regTime: String? = null
    var status: Int = 0
    var loginStatus: Int = 0
    var id: Int = 0
}