package com.topie.huaifang.extensions

import android.view.View
import android.view.ViewGroup

/**
 * Created by arman on 2017/9/16.
 */
fun View.kReleaseSelfe() {
    val viewGroup = parent as? ViewGroup
    viewGroup?.removeView(this)
}