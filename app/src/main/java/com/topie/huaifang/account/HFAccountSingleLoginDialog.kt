package com.topie.huaifang.account

import android.app.Activity
import com.topie.huaifang.HFActivityManager
import com.topie.huaifang.view.HFTipDialog
import java.lang.ref.WeakReference

/**
 * Created by arman on 2017/10/12.
 * 提示登录的弹窗
 */
object HFAccountSingleLoginDialog {

    var dialog: HFTipDialog? = null
    var resumedActivity: WeakReference<Activity>? = null

    fun showDialog() {
//        HFActivityManager.resumedActivity?.get()?.takeIf {
//            dialog?.isShowing?.and(resumedActivity?.get()?.equals()) ?: false
//        }
    }
}