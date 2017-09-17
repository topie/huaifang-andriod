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
class HFMsgFragment : HFBaseFragment() {

    private var adapter: HFListAdapter? = null

    override fun onCreateViewSupport(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val inflate = inflater.inflate(R.layout.facing_list, container, false)
        val recyclerView = inflate.findViewById<RecyclerView>(R.id.rv_facing_list)
        recyclerView.layoutManager = LinearLayoutManager(inflate.context)
        //create adapter
        val list: ArrayList<HFListAdapter.HFListItem> = arrayListOf()
        list.add(HFListAdapter.HFListItem(0, null, null, HFListAdapter.HFListItem.TYPE_SEARCH))
        list.add(HFListAdapter.HFListItem(R.mipmap.ic_facing_index_fun_live, null, context.getString(R.string.facing_index_fun_live)))
        list.add(HFListAdapter.HFListItem(R.mipmap.ic_facing_index_fun_announcement, null, context.getString(R.string.facing_index_fun_announcement)))
        list.add(HFListAdapter.HFListItem(R.mipmap.ic_facing_index_fun_yellow_book, null, context.getString(R.string.facing_index_fun_yellow_book)))
        adapter = HFListAdapter(inflater.context, list)
        recyclerView.adapter = adapter
        return inflate
    }

}