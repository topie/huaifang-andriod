package com.topie.huaifang.function.party

import `in`.srain.cube.views.ptr.PtrFrameLayout
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.davdian.ptr.DefaultPt2Handler
import com.davdian.ptr.Pt2FrameLayout
import com.davdian.ptr.ptl.PtlFrameLayout
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseFragment
import com.topie.huaifang.base.HFBaseRecyclerAdapter
import com.topie.huaifang.base.HFBaseRecyclerViewHolder
import com.topie.huaifang.base.HFViewHolderFactory
import com.topie.huaifang.extensions.kFormatTime
import com.topie.huaifang.extensions.kParseUrl
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunPartyResponseBody
import com.topie.huaifang.http.subscribeApi
import com.topie.huaifang.imageloader.HFImageView
import io.reactivex.disposables.Disposable

/**
 * Created by arman on 2017/9/24.
 * 社区党建 党支部活动
 */
class HFFunPartyActFragment : HFBaseFragment() {

    private val adapter = Adapter()
    private var disposable: Disposable? = null

    val handler: DefaultPt2Handler = object : DefaultPt2Handler() {

        override fun checkCanDoLoad(frame: PtlFrameLayout?, content: View?, footer: View?): Boolean {
            return super.checkCanDoLoad(frame, content, footer) && return adapter.list.size > 0
        }

        override fun onLoadMoreBegin(frame: PtlFrameLayout?) {
            getFunPartyActList(adapter.pageSize)
        }

        override fun onRefreshBegin(frame: PtrFrameLayout?) {
            getFunPartyActList(0)
        }
    }

    override fun onCreateViewSupport(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val inflate = layoutInflater.inflate(R.layout.base_pt2_recycler_layout, container, false) as Pt2FrameLayout
        inflate.setPt2Handler(handler)
        val recyclerView = inflate.findViewById<RecyclerView>(R.id.rv_base_pt2)
        recyclerView.layoutManager = LinearLayoutManager(inflater.context)
        recyclerView.adapter = adapter
        return inflate
    }

    override fun onResume() {
        super.onResume()
        if (adapter.list.isEmpty()) {
            getFunPartyActList(adapter.pageSize)
        }
    }

    private fun getFunPartyActList(pageSize: Int) {
        disposable?.takeIf { it.isDisposed.not() }?.dispose()
        disposable = HFRetrofit.hfService.getFunPartyActList().subscribeApi {
            it.data?.data?.takeIf { it.isNotEmpty() }?.let {
                if (pageSize == 0) {
                    adapter.list.clear()
                }
                adapter.list.addAll(it)
                adapter.pageSize = pageSize + 1
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        disposable?.takeIf { it.isDisposed.not() }?.dispose()
    }

    private class Adapter(var pageSize: Int = 0)
        : HFBaseRecyclerAdapter<HFFunPartyResponseBody.ListData, ViewHolder>(ViewHolder.CREATOR)

    private class ViewHolder(itemView: View) : HFBaseRecyclerViewHolder<HFFunPartyResponseBody.ListData>(itemView, true) {
        val imageView: HFImageView = itemView.findViewById(R.id.iv_fun_party_list_item)
        val tvTitle: TextView = itemView.findViewById(R.id.tv_fun_party_list_item_title)
        val tvTime: TextView = itemView.findViewById(R.id.tv_fun_party_list_item_time)
        val tvStatus: TextView = itemView.findViewById(R.id.tv_fun_party_list_item_status)
        val tvPublisher: TextView = itemView.findViewById(R.id.tv_fun_party_list_item_publisher)
        val tvApply: TextView = itemView.findViewById(R.id.tv_fun_party_list_item_apply)

        override fun onBindData(d: HFFunPartyResponseBody.ListData) {
            imageView.loadImageUri(d.image.kParseUrl())
            tvTitle.text = d.topic
            tvTime.text = d.beginTime.kFormatTime("时间 yyyy-MM-dd")
            tvStatus.text = d.status?.let {
                when (it) {
                    HFFunPartyResponseBody.ListData.STATUS_WAIT -> "未开始"
                    HFFunPartyResponseBody.ListData.STATUS_GOING -> "进行中"
                    HFFunPartyResponseBody.ListData.STATUS_FINISH -> "已结束"
                    else -> "未开始"
                }
            }
            tvPublisher.text = d.publishUser?.let { "发布者：$it" }
        }

        companion object CREATOR : HFViewHolderFactory<ViewHolder> {
            override fun create(parent: ViewGroup, viewType: Int): ViewHolder {
                val inflate = LayoutInflater.from(parent.context).inflate(R.layout.function_party_list_item, parent, false)
                return ViewHolder(inflate)
            }
        }
    }
}