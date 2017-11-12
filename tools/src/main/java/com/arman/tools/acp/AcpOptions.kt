package com.arman.tools.acp

/**
 * Created by hupei on 2016/4/26.
 *
 */
class AcpOptions {
    /**
     * 申请权限理由框提示语
     */
    var rationalMessage: String = DEF_RATIONAL_MESSAGE
    /**
     * 拒绝框提示语
     */
    var deniedMessage: String = DEF_DENIED_MESSAGE
    /**
     * 拒绝框关闭按钮
     */
    var deniedCloseBtn: String = DEF_DENIED_CLOSE_BTN_TEXT
    /**
     * 拒绝框跳转设置权限按钮
     */
    var deniedSettingBtn: String = DEF_DENIED_SETTINGS_BTN_TEXT
    /**
     * 申请权限理由框按钮
     */
    var rationalBtnText: String = DEF_RATIONAL_BTN_TEXT
    /**
     * 需要申请的权限
     */
    var permissions: Array<String>? = null

    fun request(acpListener: AcpListener) {
        Acp.mInstance!!.acpManager.request(this, acpListener)
    }

    companion object {
        private val DEF_RATIONAL_MESSAGE = "此功能需要您授权，否则将不能正常使用"
        private val DEF_DENIED_MESSAGE = "您拒绝权限申请，此功能将不能正常使用，您可以去设置页面重新授权"
        private val DEF_DENIED_CLOSE_BTN_TEXT = "关闭"
        private val DEF_DENIED_SETTINGS_BTN_TEXT = "设置权限"
        private val DEF_RATIONAL_BTN_TEXT = "我知道了"
    }
}
