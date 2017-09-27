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
class HFDiscoveryFragment : HFBaseFragment() {

    private var adapter: HFListAdapter? = null

    override fun onCreateViewSupport(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val inflate = inflater.inflate(R.layout.facing_list, container, false)
        val recyclerView = inflate.findViewById(R.id.rv_facing_list) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(inflate.context)
        //create adapter
        val list: ArrayList<HFListAdapter.HFListItem> = arrayListOf()
        list.add(HFListAdapter.HFListItem(R.mipmap.ic_facing_discovery_neighborhood, context.getString(R.string.facing_discovery_neighborhood)))
        list.add(HFListAdapter.HFListItem(R.mipmap.ic_facing_discovery_activity, context.getString(R.string.facing_discovery_activity)))
        list.add(HFListAdapter.HFListItem(R.mipmap.ic_facing_discovery_forum, context.getString(R.string.facing_discovery_forum)))
        list.add(HFListAdapter.HFListItem(R.mipmap.ic_facing_discovery_around, context.getString(R.string.facing_discovery_around)))
        adapter = HFListAdapter(inflater.context, list)
        recyclerView.adapter = adapter
        return inflate
    }
}