package com.arman.tools.acp

/**
 * Created by hupei on 2016/4/26.
 */
interface AcpListener {
    /**
     * 同意
     */
    fun onGranted()

    /**
     * 拒绝
     */
    fun onDenied(permissions: List<String>)
}
