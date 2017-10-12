package com.topie.huaifang.function.party

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
import com.topie.huaifang.extensions.kStartActivity
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunPartyPublicResponseBody
import com.topie.huaifang.http.subscribeResultOkApi
import io.reactivex.disposables.Disposable

/**
 * Created by arman on 2017/9/24.
 * 社区党建 党务公开
 */
class HFFunPartyPublicFragment : HFBaseFragment() {

    private lateinit var pt2FrameLayout: Pt2FrameLayout
    private val adapter = HFBaseRecyclerAdapter(ViewHolder.CREATOR)
    private var disposable: Disposable? = null

    private val handler: AbsPt2Handler = object : AbsPt2Handler() {

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
            getFunPartyActList()
        }
    }

    private fun getFunPartyActList() {
        disposable?.takeIf { it.isDisposed.not() }?.dispose()
        disposable = HFRetrofit.hfService.getFunPartyPublicList().subscribeResultOkApi({
            it.data?.data?.takeIf { it.isNotEmpty() }?.let {
                adapter.list.clear()
                adapter.list.addAll(it)
                adapter.notifyDataSetChanged()
            }
        }, {
            pt2FrameLayout.complete2()
        })
    }

    override fun onPause() {
        super.onPause()
        disposable?.takeIf { it.isDisposed.not() }?.dispose()
    }

    private class ViewHolder(itemView: View) : HFBaseRecyclerViewHolder<HFFunPartyPublicResponseBody.ListData>(itemView, true) {
        val tvTitle: TextView = itemView.kFindViewById(R.id.tv_fun_party_public_list_item_title)
        val tvTime: TextView = itemView.kFindViewById(R.id.tv_fun_party_public_list_item_time)
        val tvPublisher: TextView = itemView.kFindViewById(R.id.tv_fun_party_public_list_item_publisher)

        override fun onBindData(d: HFFunPartyPublicResponseBody.ListData) {
            tvTitle.text = d.title
            tvTime.text = d.publishTime
            tvPublisher.text = d.publishUser?.let { "发布者：$it" }
        }

        override fun onItemClicked(d: HFFunPartyPublicResponseBody.ListData?) {
            super.onItemClicked(d)
            if (d == null) {
                return
            }
            val bundle = Bundle()
            bundle.putSerializable(HFFunPartyPublicDetailActivity.EXTRA_DATA, d)
            itemView.kStartActivity(HFFunPartyPublicDetailActivity::class.java, bundle)
        }

        companion object CREATOR : HFViewHolderFactory<ViewHolder> {
            override fun create(parent: ViewGroup, viewType: Int): ViewHolder {
                val inflate = LayoutInflater.from(parent.context).inflate(R.layout.function_party_public_list_item, parent, false)
                return ViewHolder(inflate)
            }
        }
    }
}