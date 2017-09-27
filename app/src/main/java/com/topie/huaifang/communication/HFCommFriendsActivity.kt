package com.topie.huaifang.communication

import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.topie.huaifang.HFBaseTitleActivity
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseRecyclerAdapter
import com.topie.huaifang.base.HFBaseRecyclerViewHolder
import com.topie.huaifang.base.HFViewHolderFactory
import com.topie.huaifang.extensions.kIsNotEmpty
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.communication.HFCommFriendsResponseBody
import com.topie.huaifang.http.subscribeApi
import com.topie.huaifang.imageloader.HFImageView

/**
 * Created by arman on 2017/9/24.
 * 我的好友
 */
class HFCommFriendsActivity : HFBaseTitleActivity() {

    private val hfBaseRecyclerAdapter = HFBaseRecyclerAdapter(ViewHolder.CREATOR)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val recyclerView = RecyclerView(applicationContext)
        setContentView(recyclerView)
        setBaseTitle(R.string.facing_mine_friend)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = hfBaseRecyclerAdapter
        HFRetrofit.hfService.getCommFriends().subscribeApi {
            it.data?.data?.takeIf { it.isNotEmpty() }?.let { hfBaseRecyclerAdapter.list.addAll(it) }
        }
    }

    class ViewHolder(itemView: View) : HFBaseRecyclerViewHolder<HFCommFriendsResponseBody.ListData>(itemView) {
        private val hfImageView = itemView.findViewById(R.id.iv_facing_list_icon) as HFImageView
        private val textView = itemView.findViewById(R.id.tv_facing_list_name) as TextView

        companion object CREATOR : HFViewHolderFactory<ViewHolder> {
            override fun create(parent: ViewGroup, viewType: Int): ViewHolder {
                val inflate = LayoutInflater.from(parent.context).inflate(R.layout.facing_list_item, parent, false)
                return ViewHolder(inflate)
            }
        }

        override fun onBindData(d: HFCommFriendsResponseBody.ListData) {
            hfImageView.loadImageUri(d.headImage?.let { Uri.parse(it) })
            textView.text = d.nickname?.takeIf { it.kIsNotEmpty() } ?: d.mobilePhone
        }

    }
}