package com.topie.huaifang

import android.app.Activity
import com.topie.huaifang.extensions.kTel
import com.topie.huaifang.view.HFTipDialog

fun Activity.kShowTelDialog(phoneNumber: String) {
    val builder = HFTipDialog.Builder()
    builder.content = "确定拨打：$phoneNumber?"
    builder.onOkClicked = { kTel(phoneNumber) }
    builder.show(this)
}