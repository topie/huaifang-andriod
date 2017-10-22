package com.topie.huaifang.base

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.topie.huaifang.extensions.kInflate

/**
 * Created by arman on 2017/9/22.
 */
open class HFBaseRecyclerAdapter<D, VH : HFBaseRecyclerViewHolder<D>>(private val factory: HFViewHolderFactory<VH>, val list: MutableList<D> = arrayListOf()) : RecyclerView.Adapter<VH>() {

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bindData(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return factory.create(parent, viewType)
    }
}

abstract class HFBaseParentViewHolder<D>(parent: ViewGroup, @LayoutRes layoutId: Int, initItemClick: Boolean = false) :
        HFBaseRecyclerViewHolder<D>(parent.kInflate(layoutId), initItemClick) {

}

abstract class HFBaseRecyclerViewHolder<D>(itemView: View, initItemClick: Boolean = false) : RecyclerView.ViewHolder(itemView) {

    var data: D? = null
        private set

    init {
        if (initItemClick) {
            itemView.setOnClickListener {
                onItemClicked(data)
            }
        }
    }

    fun bindData(d: D) {
        data = d
        onBindData(d)
    }

    abstract fun onBindData(d: D)

    open fun onItemClicked(d: D?) {

    }
}

open class HFEmptyRecyclerViewHolder<T>(itemView: View) : HFBaseRecyclerViewHolder<T>(itemView, false) {

    override fun onBindData(d: T) {

    }
}

interface HFViewHolderFactory<out T : RecyclerView.ViewHolder> {

    fun create(parent: ViewGroup, viewType: Int): T

    fun getViewType(position: Int): Int {
        return 0
    }
}

