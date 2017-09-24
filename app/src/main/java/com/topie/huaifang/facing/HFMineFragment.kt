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
import com.topie.huaifang.communication.HFCommFriendsActivity
import com.topie.huaifang.extensions.kStartActivity
import com.topie.huaifang.extensions.log
import com.topie.huaifang.imageloader.HFImageView

/**
 * Created by arman on 2017/9/16.
 * 我
 */
class HFMineFragment : HFBaseFragment() {


    override fun onCreateViewSupport(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val inflate = inflater.inflate(R.layout.facing_list, container, false)
        val recyclerView = inflate.findViewById<RecyclerView>(R.id.rv_facing_list)
        recyclerView.layoutManager = LinearLayoutManager(inflate.context)
        val list: ArrayList<HFListItem> = arrayListOf()
        list.add(HFListItem(R.mipmap.ic_facing_mine_friend, context.getString(R.string.facing_mine_friend), 0))
        list.add(HFListItem(R.mipmap.ic_facing_mine_room, context.getString(R.string.facing_mine_room), 1))
        list.add(HFListItem(R.mipmap.ic_facing_mine_company, context.getString(R.string.facing_mine_company), 2))
        list.add(HFListItem(R.mipmap.ic_facing_mine_party_member, context.getString(R.string.facing_mine_party_member), 3))
        list.add(HFListItem(R.mipmap.ic_facing_mine_label, context.getString(R.string.facing_mine_label), 4))
        list.add(HFListItem(R.mipmap.ic_facing_mine_suggestion, context.getString(R.string.facing_mine_suggestion), 5))
        list.add(HFListItem(R.mipmap.ic_facing_mine_setting, context.getString(R.string.facing_mine_setting), 6))
        recyclerView.adapter = HFBaseRecyclerAdapter(ViewHolder.CREATOR, list)
        return inflate
    }

    class ViewHolder(itemView: View) : HFBaseRecyclerViewHolder<HFListItem>(itemView, true) {

        private val ivIcon: HFImageView = itemView.findViewById(R.id.iv_facing_list_icon)
        private val tvName: TextView = itemView.findViewById(R.id.tv_facing_list_name)

        companion object CREATOR : HFViewHolderFactory<ViewHolder> {
            override fun create(parent: ViewGroup, viewType: Int): ViewHolder {
                val from = LayoutInflater.from(parent.context)
                val itemView = from.inflate(R.layout.facing_list_item, parent, false)
                return ViewHolder(itemView)
            }
        }

        override fun onBindData(d: HFListItem) {
            ivIcon.setImageResource(d.iconId)
            tvName.text = d.name
        }

        override fun onItemClicked(d: HFListItem?) {
            super.onItemClicked(d)
            when (d?.itemType ?: -1) {
                0 -> itemView.context.kStartActivity(HFCommFriendsActivity::class.java)
                else -> log("itemType = ${d?.itemType}")
            }
        }

    }


    class HFListItem(@DrawableRes val iconId: Int, val name: String?, val itemType: Int)
}