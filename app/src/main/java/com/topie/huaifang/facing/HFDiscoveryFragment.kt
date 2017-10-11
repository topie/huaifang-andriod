package com.topie.huaifang.facing

import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseFragment
import com.topie.huaifang.base.HFBaseRecyclerAdapter
import com.topie.huaifang.base.HFBaseRecyclerViewHolder
import com.topie.huaifang.base.HFViewHolderFactory
import com.topie.huaifang.extensions.kFindViewById
import com.topie.huaifang.extensions.kGetString
import com.topie.huaifang.extensions.kStartActivity
import com.topie.huaifang.function.discovery.HFFunDisNeighborhoodActivity
import com.topie.huaifang.imageloader.HFImageView

/**
 * Created by arman on 2017/9/16.
 * 主界面，发现
 */
class HFDiscoveryFragment : HFBaseFragment() {

    override fun onCreateViewSupport(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val inflate = inflater.inflate(R.layout.facing_list, container, false)
        val recyclerView: RecyclerView = inflate.kFindViewById(R.id.rv_facing_list)
        recyclerView.layoutManager = LinearLayoutManager(inflate.context)
        //create adapter
        val list: ArrayList<Item> = arrayListOf()
        list.add(Item(R.mipmap.ic_facing_discovery_neighborhood, kGetString(R.string.facing_discovery_neighborhood), 0))
        list.add(Item(R.mipmap.ic_facing_discovery_activity, kGetString(R.string.facing_discovery_activity), 1))
        list.add(Item(R.mipmap.ic_facing_discovery_forum, kGetString(R.string.facing_discovery_forum), 2))
        list.add(Item(R.mipmap.ic_facing_discovery_around, kGetString(R.string.facing_discovery_around), 3))
        recyclerView.adapter = HFBaseRecyclerAdapter(ViewHolder.CREATOR, list)
        return inflate
    }

    private class Item(@DrawableRes val iconId: Int, val name: String?, val id: Int)

    private class ViewHolder(itemView: View) : HFBaseRecyclerViewHolder<Item>(itemView, true) {

        private val ivIcon: HFImageView = itemView.kFindViewById(R.id.iv_facing_list_icon)
        private val tvName: TextView = itemView.kFindViewById(R.id.tv_facing_list_name)

        override fun onBindData(d: Item) {
            ivIcon.setImageResource(d.iconId)
            tvName.text = d.name
        }

        override fun onItemClicked(d: Item?) {
            super.onItemClicked(d)
            when ((d?.id ?: -1)) {
                0 -> {
                    itemView.kStartActivity(HFFunDisNeighborhoodActivity::class.java)
                }
            }
        }

        companion object CREATOR : HFViewHolderFactory<ViewHolder> {

            override fun create(parent: ViewGroup, viewType: Int): ViewHolder {
                val from = LayoutInflater.from(parent.context)
                val itemView = from.inflate(R.layout.facing_list_item, parent, false)
                return ViewHolder(itemView)
            }
        }
    }
}