package com.topie.huaifang.facing

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseFragment

/**
 * Created by arman on 2017/9/16.
 */
class HFMineFragment : HFBaseFragment() {
    private var adapter: HFListAdapter? = null

    override fun onCreateViewSupport(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val inflate = inflater.inflate(R.layout.facing_list, container, false)
        val recyclerView = inflate.findViewById<RecyclerView>(R.id.rv_facing_list)
        recyclerView.layoutManager = LinearLayoutManager(inflate.context)
        //create adapter
        val list: ArrayList<HFListAdapter.HFListItem> = arrayListOf()
        list.add(HFListAdapter.HFListItem(R.mipmap.ic_facing_mine_friend, context.getString(R.string.facing_mine_friend)))
        list.add(HFListAdapter.HFListItem(R.mipmap.ic_facing_mine_room, context.getString(R.string.facing_mine_room)))
        list.add(HFListAdapter.HFListItem(R.mipmap.ic_facing_mine_company, context.getString(R.string.facing_mine_company)))
        list.add(HFListAdapter.HFListItem(R.mipmap.ic_facing_mine_party_member, context.getString(R.string.facing_mine_party_member)))
        list.add(HFListAdapter.HFListItem(R.mipmap.ic_facing_mine_label, context.getString(R.string.facing_mine_label)))
        list.add(HFListAdapter.HFListItem(R.mipmap.ic_facing_mine_suggestion, context.getString(R.string.facing_mine_suggestion)))
        list.add(HFListAdapter.HFListItem(R.mipmap.ic_facing_mine_setting, context.getString(R.string.facing_mine_setting)))
        adapter = HFListAdapter(inflater.context, list)
        recyclerView.adapter = adapter
        return inflate
    }
}