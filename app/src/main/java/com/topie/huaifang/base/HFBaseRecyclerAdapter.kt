package com.topie.huaifang.base

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

/**
 * Created by arman on 2017/9/22.
 */
class HFBaseRecyclerAdapter<D, VH : HFBaseRecyclerViewHolder<D>>(private val factory: HFViewHolderFactory<VH>, val list: MutableList<D> = arrayListOf()) : RecyclerView.Adapter<VH>() {

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bindData(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return factory.create(parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return factory.getViewType(position)
    }
}

abstract class HFBaseRecyclerViewHolder<in D>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bindData(d: D)
}

interface HFViewHolderFactory<out T : RecyclerView.ViewHolder> {

    fun create(parent: ViewGroup, viewType: Int): T

    fun getViewType(position: Int): Int {
        return 0
    }
}

