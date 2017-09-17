package com.topie.huaifang.facing

import android.content.Context
import android.net.Uri
import android.support.annotation.DrawableRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.topie.huaifang.R
import com.topie.huaifang.imageloader.HFImageView

open internal class HFListAdapter(val context: Context, private val list: List<HFListItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder = when (viewType) {
            HFListItem.TYPE_SEARCH -> {
                SearchViewHolder.create(parent.context, parent)
            }
            HFListItem.TYPE_NORMAL -> {
                HFListViewHolder.create(parent.context, parent)
            }
            else -> throw IllegalArgumentException("unknown viewType [$viewType]")
        }
        viewHolder.itemView.setOnClickListener {
            onItemClick(viewHolder.adapterPosition, list[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    open fun onItemClick(position: Int, HFListItem: HFListItem) {

    }

    override fun getItemViewType(position: Int): Int {
        return list[position].viewType
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (holder is HFListViewHolder) {
            holder.bindData(list[position])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        companion object {
            fun create(context: Context, parent: ViewGroup): SearchViewHolder {
                val from = LayoutInflater.from(context)
                val itemView = from.inflate(R.layout.facing_list_search, parent, false)
                return SearchViewHolder(itemView)
            }
        }
    }

    class HFListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivIcon: HFImageView = itemView.findViewById(R.id.iv_facing_list_icon)
        private val tvName: TextView = itemView.findViewById(R.id.tv_facing_list_name)

        fun bindData(HFListItem: HFListItem) {
            tvName.text = HFListItem.name
            when {
                HFListItem.icon != null -> ivIcon.loadImageUri(HFListItem.icon)
                HFListItem.iconId != 0 -> ivIcon.setImageResource(HFListItem.iconId)
                else -> ivIcon.setImageBitmap(null)
            }

        }

        companion object {
            fun create(context: Context, parent: ViewGroup): HFListViewHolder {
                val from = LayoutInflater.from(context)
                val itemView = from.inflate(R.layout.facing_list_item, parent, false)
                return HFListViewHolder(itemView)
            }
        }

    }

    class HFListItem(@DrawableRes val iconId: Int, val icon: Uri?, val name: String?, val viewType: Int = TYPE_NORMAL) {
        companion object {
            const val TYPE_NORMAL = 0
            const val TYPE_SEARCH = 1

        }

    }

}
