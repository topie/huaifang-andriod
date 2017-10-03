package com.topie.huaifang.function

import android.os.Bundle
import android.support.annotation.DrawableRes
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
import com.topie.huaifang.communication.HFCommFriendsActivity
import com.topie.huaifang.extensions.kGetString
import com.topie.huaifang.extensions.kStartActivity
import com.topie.huaifang.extensions.log
import com.topie.huaifang.function.dispute.HFFunDisputeMediatorActivity
import com.topie.huaifang.function.guide.HFFunGuideActivity
import com.topie.huaifang.function.library.HFFunLibraryActivity
import com.topie.huaifang.function.live.HFFunLiveActivity
import com.topie.huaifang.function.notice.HFFunPublicActivity
import com.topie.huaifang.function.party.HFFunPartyActivity
import com.topie.huaifang.function.yellowpage.HFFunYellowPageActivity
import com.topie.huaifang.imageloader.HFImageView

/**
 * Created by arman on 2017/10/2.
 *  全部应用
 */
class HFFunAllActivity : HFBaseTitleActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.facing_list)
        setBaseTitle("全部应用")
        val recyclerView: RecyclerView = findViewById(R.id.rv_facing_list) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        val list: ArrayList<HFListItem> = arrayListOf()
        list.add(HFListItem(R.mipmap.ic_facing_index_fun_announcement, kGetString(R.string.facing_index_fun_announcement), 0))
        list.add(HFListItem(R.mipmap.ic_facing_index_fun_guide, kGetString(R.string.facing_index_fun_guide), 1))
        list.add(HFListItem(R.mipmap.ic_facing_index_fun_bazaar, kGetString(R.string.facing_index_fun_bazaar), 2))
        list.add(HFListItem(R.mipmap.ic_facing_index_fun_live, kGetString(R.string.facing_index_fun_live), 3))
        list.add(HFListItem(R.mipmap.ic_facing_index_fun_party, kGetString(R.string.facing_index_fun_party), 4))
        list.add(HFListItem(R.mipmap.ic_facing_index_fun_yellow_book, kGetString(R.string.facing_index_fun_yellow_book), 5))
        list.add(HFListItem(R.mipmap.ic_facing_index_fun_library, kGetString(R.string.facing_index_fun_library), 6))
        list.add(HFListItem(R.mipmap.ic_facing_index_fun_dispute, kGetString(R.string.facing_index_fun_dispute), 7))
        recyclerView.adapter = HFBaseRecyclerAdapter(ViewHolder, list)
    }

    class ViewHolder(itemView: View) : HFBaseRecyclerViewHolder<HFListItem>(itemView, true) {

        private val ivIcon: HFImageView = itemView.findViewById(R.id.iv_facing_list_icon) as HFImageView
        private val tvName: TextView = itemView.findViewById(R.id.tv_facing_list_name) as TextView

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
                0 -> itemView.kStartActivity(HFFunPublicActivity::class.java)
                1 -> itemView.kStartActivity(HFFunGuideActivity::class.java)
                2 -> itemView.kStartActivity(HFCommFriendsActivity::class.java)
                3 -> itemView.kStartActivity(HFFunLiveActivity::class.java)
                4 -> itemView.kStartActivity(HFFunPartyActivity::class.java)
                5 -> itemView.kStartActivity(HFFunYellowPageActivity::class.java)
                6 -> itemView.kStartActivity(HFFunLibraryActivity::class.java)
                7 -> itemView.kStartActivity(HFFunDisputeMediatorActivity::class.java)
                else -> log("itemType = ${d?.itemType}")
            }
        }

    }


    class HFListItem(@DrawableRes val iconId: Int, val name: String?, val itemType: Int)
}