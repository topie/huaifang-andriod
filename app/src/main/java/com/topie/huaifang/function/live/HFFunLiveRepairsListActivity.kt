package com.topie.huaifang.function.live

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseRecyclerViewHolder
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.*
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunLiveRepairsListResponseBody
import com.topie.huaifang.http.subscribeResultOkApi
import com.topie.huaifang.imageloader.HFImageView
import kotlinx.android.synthetic.main.base_pt2_recycler_layout.*
import kotlinx.android.synthetic.main.base_title_layout.*

/**
 * Created by arman on 2017/10/7.
 * 物业报修列表
 */
class HFFunLiveRepairsListActivity : HFBaseTitleActivity() {

    private val mAdapter: Adapter = Adapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_pt2_recycler_layout)
        setBaseTitle("物业报修")
        setBaseTitleRight("报修")
        pt2_base_recycler.setPt2Handler(HFDefaultPt2Handler())
        rv_base_pt2.layoutManager = LinearLayoutManager(this)
        rv_base_pt2.adapter = mAdapter
        tv_base_title_right.setOnClickListener {
            this@HFFunLiveRepairsListActivity.kStartActivity(HFFunLiveRepairsApplyActivity::class.java)
        }
    }

    override fun onResume() {
        super.onResume()
        if (mAdapter.list.isEmpty()) {
            HFRetrofit.hfService.getFunLiveRepairsList().subscribeResultOkApi {
                it.data?.data?.takeIf { it.isNotEmpty() }?.kInsteadTo(mAdapter.list)
                mAdapter.notifyDataSetChanged()
            }.kInto(pauseDisableList)
        }
    }

    class Adapter : RecyclerView.Adapter<HFBaseRecyclerViewHolder<HFFunLiveRepairsListResponseBody.ListData>>() {

        val list: MutableList<HFFunLiveRepairsListResponseBody.ListData> = arrayListOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HFBaseRecyclerViewHolder<HFFunLiveRepairsListResponseBody.ListData> {
            val itemView = parent.kInflate(R.layout.function_live_repairs_list_item)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: HFBaseRecyclerViewHolder<HFFunLiveRepairsListResponseBody.ListData>, position: Int) {
            holder.bindData(list.kGet(position) ?: HFFunLiveRepairsListResponseBody.ListData())
        }

        override fun getItemCount(): Int {
            return list.size
        }

    }

    class ViewHolder(itemView: View) : HFBaseRecyclerViewHolder<HFFunLiveRepairsListResponseBody.ListData>(itemView, true) {
        private val ivHead: HFImageView = itemView.findViewById(R.id.iv_fun_live_repairs_head)
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_fun_live_repairs_title)
        private val tvTime: TextView = itemView.findViewById(R.id.tv_fun_live_repairs_time)
        private val tvStatus: TextView = itemView.findViewById(R.id.tv_fun_live_repairs_status)

        override fun onBindData(d: HFFunLiveRepairsListResponseBody.ListData) {
            tvTitle.text = d.reportTitle
            tvTime.text = d.reportTime
            tvStatus.text = d.status
        }

        override fun onItemClicked(d: HFFunLiveRepairsListResponseBody.ListData?) {
            super.onItemClicked(d)
            d?.let {
                Bundle()
            }?.also {
                it.putSerializable(HFFunLiveRepairsDetailActivity.EXTRA_DATA, d)
                itemView.kStartActivity(HFFunLiveRepairsDetailActivity::class.java, it)
            }
        }
    }
}