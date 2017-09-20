package com.topie.huaifang.extensions

import android.support.annotation.IdRes
import android.view.View
import android.view.ViewGroup

/**
 * Created by arman on 2017/9/16.
 */
fun View.kReleaseSelf() {
    val viewGroup = parent as? ViewGroup
    viewGroup?.removeView(this)
}

fun View.kRemoveChildsWithout(@IdRes idRes: Int) {
    val viewGroup = parent as? ViewGroup
    viewGroup?.let {
        val childCount = it.childCount
        for (i in 0 until childCount) {
            when {
                it.getChildAt(i).id != idRes -> viewGroup.removeViewAt(i)
            }
        }
    }
}