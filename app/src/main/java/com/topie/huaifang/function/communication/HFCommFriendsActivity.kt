package com.topie.huaifang.function.communication

import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseRecyclerAdapter
import com.topie.huaifang.base.HFBaseRecyclerViewHolder
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.base.HFViewHolderFactory
import com.topie.huaifang.extensions.kFindViewById
import com.topie.huaifang.extensions.kIsNotEmpty
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.communication.HFCommUserInfo
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
    }

    override fun onResume() {
        super.onResume()
        if (hfBaseRecyclerAdapter.list.isEmpty()) {
            getData()
        }
    }

    private fun getData() {
        HFRetrofit.hfService.getCommFriends().subscribeApi {
            it.data?.data?.takeIf {
                it.isNotEmpty()
            }?.let {
                hfBaseRecyclerAdapter.list.clear()
                hfBaseRecyclerAdapter.list.addAll(it)
                hfBaseRecyclerAdapter.notifyDataSetChanged()
            }
        }
    }

    class ViewHolder(itemView: View) : HFBaseRecyclerViewHolder<HFCommUserInfo>(itemView, true) {
        private val hfImageView: HFImageView = itemView.kFindViewById(R.id.iv_facing_list_icon)
        private val textView: TextView = itemView.kFindViewById(R.id.tv_facing_list_name)

        companion object CREATOR : HFViewHolderFactory<ViewHolder> {
            override fun create(parent: ViewGroup, viewType: Int): ViewHolder {
                val inflate = LayoutInflater.from(parent.context).inflate(R.layout.facing_list_item, parent, false)
                return ViewHolder(inflate)
            }
        }

        override fun onBindData(d: HFCommUserInfo) {
            hfImageView.loadImageUri(d.headImage?.let { Uri.parse(it) })
            textView.text = d.nickname?.takeIf { it.kIsNotEmpty() } ?: d.mobilePhone
        }

    }
}