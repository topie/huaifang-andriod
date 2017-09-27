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
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunPartyPublicResponseBody
import com.topie.huaifang.http.subscribeApi
import io.reactivex.disposables.Disposable

/**
 * Created by arman on 2017/9/24.
 * 社区党建 党务公开
 */
class HFFunPartyPublicFragment : HFBaseFragment() {

    private val adapter = HFBaseRecyclerAdapter(ViewHolder.CREATOR)
    private var disposable: Disposable? = null

    private val handler: DefaultPt2Handler = object : DefaultPt2Handler() {

        override fun checkCanDoLoad(frame: PtlFrameLayout?, content: View?, footer: View?): Boolean {
            return false
        }

        override fun onLoadMoreBegin(frame: PtlFrameLayout?) {
        }

        override fun onRefreshBegin(frame: PtrFrameLayout?) {
            getFunPartyActList()
        }
    }

    override fun onCreateViewSupport(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val inflate = inflater.inflate(R.layout.base_pt2_recycler_layout, container, false) as Pt2FrameLayout
        inflate.setPt2Handler(handler)
        val recyclerView: RecyclerView = inflate.findViewById(R.id.rv_base_pt2) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(inflater.context)
        recyclerView.adapter = adapter
        return inflate
    }

    override fun onResume() {
        super.onResume()
        if (adapter.list.isEmpty()) {
            getFunPartyActList()
        }
    }

    private fun getFunPartyActList() {
        disposable?.takeIf { it.isDisposed.not() }?.dispose()
        disposable = HFRetrofit.hfService.getFunPartyPublicList().subscribeApi {
            it.data?.data?.takeIf { it.isNotEmpty() }?.let {
                adapter.list.clear()
                adapter.list.addAll(it)
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        disposable?.takeIf { it.isDisposed.not() }?.dispose()
    }

    private class ViewHolder(itemView: View) : HFBaseRecyclerViewHolder<HFFunPartyPublicResponseBody.ListData>(itemView, true) {
        val tvTitle: TextView = itemView.findViewById(R.id.tv_fun_party_public_list_item_title) as TextView
        val tvTime: TextView = itemView.findViewById(R.id.tv_fun_party_public_list_item_time) as TextView
        val tvPublisher: TextView = itemView.findViewById(R.id.tv_fun_party_public_list_item_publisher) as TextView

        override fun onBindData(d: HFFunPartyPublicResponseBody.ListData) {
            tvTitle.text = d.title
            tvTime.text = d.publishTime.kFormatTime("时间 yyyy-MM-dd")
            tvPublisher.text = d.publishUser?.let { "发布者：$it" }
        }

        companion object CREATOR : HFViewHolderFactory<ViewHolder> {
            override fun create(parent: ViewGroup, viewType: Int): ViewHolder {
                val inflate = LayoutInflater.from(parent.context).inflate(R.layout.function_party_public_list_item, parent, false)
                return ViewHolder(inflate)
            }
        }
    }
}