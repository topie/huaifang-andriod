package com.topie.huaifang.extensions

import android.app.Activity
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
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

/**
 * 隐藏子View
 */
fun View.kHiddenChildren() {
    if (this is ViewGroup) {
        for (i in 0 until childCount) {
            getChildAt(i).visibility = View.GONE
        }
    }
}

@Suppress("UNCHECKED_CAST")
fun <T : View> View.kFindViewById(@IdRes id: Int): T {
    return findViewById(id)
}

@Suppress("UNCHECKED_CAST")
fun <T : View> Activity.kFindViewById(@IdRes id: Int): T {
    return findViewById(id)
}

/**
 * 遍历子View
 */
fun View.kForeach(each: (v: View) -> Unit) {
    if (this is ViewGroup) {
        for (i in 0 until childCount) {
            each(getChildAt(i))
        }
    }
}

fun ViewGroup.kInflate(@LayoutRes layout: Int, attachToRoot: Boolean = false): View {
    val from = LayoutInflater.from(context)
    return from.inflate(layout, this, attachToRoot)
}