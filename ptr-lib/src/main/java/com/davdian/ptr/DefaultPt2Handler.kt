package com.davdian.ptr

import `in`.srain.cube.views.ptr.PtrFrameLayout
import android.view.View
import com.davdian.ptr.ptl.PtlFrameLayout

/**
 * @author arman
 * kotlin设计的接口，添加了默认实现
 */

abstract class DefaultPt2Handler : Pt2Handler {
    override fun checkCanDoRefresh(frame: PtrFrameLayout?, content: View?, header: View?): Boolean {
        return DefaultPt2Handler.checkCanDoRefresh(frame, content, header)
    }

    override fun checkCanDoLoad(frame: PtlFrameLayout?, content: View?, footer: View?): Boolean {
        return DefaultPt2Handler.checkCanDoLoad(frame, content, footer)
    }

    companion object {

        fun checkCanDoRefresh(frame: PtrFrameLayout?, content: View?, header: View?): Boolean {
            return content?.canScrollVertically(-1)?.not() ?: false
        }

        fun checkCanDoLoad(frame: PtlFrameLayout?, content: View?, footer: View?): Boolean {
            return content?.canScrollVertically(1)?.not() ?: false
        }

    }
}
