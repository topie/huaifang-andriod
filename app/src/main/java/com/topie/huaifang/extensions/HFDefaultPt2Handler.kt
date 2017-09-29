package com.topie.huaifang.extensions

import `in`.srain.cube.views.ptr.PtrFrameLayout
import android.view.View
import com.davdian.ptr.Pt2Handler
import com.davdian.ptr.ptl.PtlFrameLayout

/**
 * Created by arman on 2017/9/29.
 * 下拉刷新上拉加载的扩展
 */
class HFDefaultPt2Handler(private val checkDoRefresh: ((content: View) -> Boolean), private val checkDoLoadMore: ((content: View) -> Boolean),
                          private val onRefresh: () -> Unit, private val onLoadMore: (() -> Unit)) : Pt2Handler {
    constructor(onRefresh: () -> Unit, onLoadMore: (() -> Unit))
            : this({ checkCanDoRefresh(it) }, { checkCanDoLoad(it) }, onRefresh, onLoadMore)

    //自己判断是否需要下拉刷新
    constructor(checkDoRefresh: ((content: View) -> Boolean), onRefresh: () -> Unit)
            : this(checkDoRefresh, { false }, onRefresh, {})

    //content 是RecyclerView等直接决定是否可以下拉的View，只需要下拉刷新功能
    constructor(onRefresh: () -> Unit)
            : this({ checkCanDoRefresh(it) }, { false }, onRefresh, {})

    override fun checkCanDoRefresh(frame: PtrFrameLayout, content: View, header: View): Boolean {
        return checkDoRefresh(content)
    }

    override fun checkCanDoLoad(frame: PtlFrameLayout, content: View, footer: View): Boolean {
        return checkDoLoadMore(content)
    }

    override fun onLoadMoreBegin(frame: PtlFrameLayout?) {
        onLoadMore()
    }

    override fun onRefreshBegin(frame: PtrFrameLayout?) {
        onRefresh()
    }

    companion object {
        fun checkCanDoRefresh(content: View?): Boolean {
            return content != null && !content.canScrollVertically(-1)
        }

        fun checkCanDoLoad(content: View?): Boolean {
            return content != null && !content.canScrollVertically(1)
        }
    }
}