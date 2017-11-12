package com.arman.tools.acp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager

class AcpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //不接受触摸屏事件
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        if (savedInstanceState == null)
            Acp.mInstance!!.acpManager.checkRequestPermissionRationale(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Acp.mInstance!!.acpManager.checkRequestPermissionRationale(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Acp.mInstance!!.acpManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Acp.mInstance!!.acpManager.onActivityResult(requestCode, resultCode, data)
    }
}
