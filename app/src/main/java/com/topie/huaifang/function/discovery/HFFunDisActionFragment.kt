package com.topie.huaifang.function.discovery

import `in`.srain.cube.views.ptr.PtrFrameLayout
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.davdian.ptr.AbsPt2Handler
import com.davdian.ptr.Pt2FrameLayout
import com.davdian.ptr.ptl.PtlFrameLayout
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseFragment
import com.topie.huaifang.base.HFBaseRecyclerAdapter
import com.topie.huaifang.base.HFBaseRecyclerViewHolder
import com.topie.huaifang.base.HFViewHolderFactory
import com.topie.huaifang.extensions.kFindViewById
import com.topie.huaifang.extensions.kInto
import com.topie.huaifang.extensions.kParseUrl
import com.topie.huaifang.extensions.kStartActivity
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunDisActionListResponseBody
import com.topie.huaifang.http.subscribeResultOkApi
import com.topie.huaifang.imageloader.HFImageView

/**
 * Created by arman on 2017/9/24.
 * 发现--活动
 */
class HFFunDisActionFragment : HFBaseFragment() {

    private var mType: Int = -1
    private lateinit var pt2FrameLayout: Pt2FrameLayout
    private val adapter = Adapter()

    companion object {
        fun newInstance(type: Int): HFFunDisActionFragment {
            val fragment = HFFunDisActionFragment()
            fragment.mType = type
            return fragment
        }
    }

    private val handler: AbsPt2Handler = object : AbsPt2Handler() {

        override fun checkCanDoLoad(frame: PtlFrameLayout?, content: View?, footer: View?): Boolean {
            return super.checkCanDoLoad(frame, content, footer) && return adapter.list.size > 0
        }

        override fun onLoadMoreBegin(frame: PtlFrameLayout?) {
            getFunPartyActList(adapter.pageSize)
        }

        override fun onRefreshBegin(frame: PtrFrameLayout?) {
            getFunPartyActList(1)
        }
    }

    override fun onCreateViewSupport(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        pt2FrameLayout = inflater.inflate(R.layout.base_pt2_recycler_layout, container, false) as Pt2FrameLayout
        pt2FrameLayout.setPt2Handler(handler)
        val recyclerView: RecyclerView = pt2FrameLayout.kFindViewById(R.id.rv_base_pt2)
        recyclerView.layoutManager = LinearLayoutManager(inflater.context)
        recyclerView.adapter = adapter
        return pt2FrameLayout
    }

    override fun onResume() {
        super.onResume()
        if (adapter.list.isEmpty()) {
            getFunPartyActList(1)
        }
    }

    private fun getFunPartyActList(pageSize: Int) {
        HFRetrofit.hfService.getFunDisActionList(pageSize).subscribeResultOkApi({
            it.data?.data?.takeIf { it.isNotEmpty() }?.let {
                if (pageSize == 0 || pageSize == 1) {
                    adapter.list.clear()
                    adapter.list.addAll(it)
                    adapter.pageSize = 1
                    adapter.notifyDataSetChanged()
                } else if (pageSize == adapter.pageSize) {
                    adapter.list.addAll(it)
                    adapter.pageSize = pageSize + 1
                    adapter.notifyDataSetChanged()
                }
            }
        }, {
            pt2FrameLayout.complete2()
        }).kInto(pauseDisableList)
    }

    private class Adapter(var pageSize: Int = 1)
        : HFBaseRecyclerAdapter<HFFunDisActionListResponseBody.ListData, ViewHolder>(ViewHolder.CREATOR)

    private class ViewHolder(itemView: View) : HFBaseRecyclerViewHolder<HFFunDisActionListResponseBody.ListData>(itemView, true) {
        val imageView: HFImageView = itemView.kFindViewById(R.id.iv_fun_party_list_item)
        val tvTitle: TextView = itemView.kFindViewById(R.id.tv_fun_party_list_item_title)
        val tvTime: TextView = itemView.kFindViewById(R.id.tv_fun_party_list_item_time)
        val tvStatus: TextView = itemView.kFindViewById(R.id.tv_fun_party_list_item_status)
        val tvPublisher: TextView = itemView.kFindViewById(R.id.tv_fun_party_list_item_publisher)
        val tvApply: TextView = itemView.kFindViewById(R.id.tv_fun_party_list_item_apply)

        override fun onBindData(d: HFFunDisActionListResponseBody.ListData) {
            imageView.loadImageUri(d.image.kParseUrl())
            tvTitle.text = d.topic
            tvTime.text = d.beginTime
            tvStatus.text = d.status?.let {
                when (it) {
                    HFFunDisActionListResponseBody.ListData.STATUS_WAIT -> "未开始"
                    HFFunDisActionListResponseBody.ListData.STATUS_GOING -> "进行中"
                    HFFunDisActionListResponseBody.ListData.STATUS_FINISH -> "已结束"
                    else -> "未开始"
                }
            }
            tvPublisher.text = d.publishUser?.let { "发布者：$it" }
            tvApply.text = d.total.toString()
        }

        override fun onItemClicked(d: HFFunDisActionListResponseBody.ListData?) {
            super.onItemClicked(d)
            if (d != null) {
                val bundle = Bundle()
                bundle.putSerializable(HFFunDisActionDetailActivity.EXTRA_DETAIL, d)
                itemView.kStartActivity(HFFunDisActionDetailActivity::class.java, bundle)
            }
        }

        companion object CREATOR : HFViewHolderFactory<ViewHolder> {
            override fun create(parent: ViewGroup, viewType: Int): ViewHolder {
                val inflate = LayoutInflater.from(parent.context).inflate(R.layout.function_party_list_item, parent, false)
                return ViewHolder(inflate)
            }
        }
    }
}