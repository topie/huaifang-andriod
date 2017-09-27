package com.topie.huaifang.function.guide

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
import com.topie.huaifang.extensions.kToastLong
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunGuideListResponseBody
import com.topie.huaifang.http.bean.function.HFFunGuideMenuResponseBody
import com.topie.huaifang.http.composeApi
import io.reactivex.disposables.Disposable

/**
 * Created by arman on 2017/9/22.
 * 办事指南列表
 */
class HFFunGuideFragment : HFBaseFragment() {

    var listData: HFFunGuideMenuResponseBody.ListData? = null
    private var disposable: Disposable? = null
    private var pt2FrameLayout: Pt2FrameLayout? = null

    override fun onCreateViewSupport(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        pt2FrameLayout = inflater.inflate(R.layout.base_pt2_recycler_layout, container, false) as Pt2FrameLayout
        pt2FrameLayout!!.setPt2Handler(object : DefaultPt2Handler() {
            override fun checkCanDoLoad(frame: PtlFrameLayout?, content: View?, footer: View?): Boolean {
                return super.checkCanDoLoad(frame, content, footer) && adapter.list.size > 1
            }

            override fun onLoadMoreBegin(frame: PtlFrameLayout?) {
                getFunGuideList(listData?._pageNum ?: 0)
            }

            override fun onRefreshBegin(frame: PtrFrameLayout?) {
                getFunGuideList()
            }
        })
        val recyclerView: RecyclerView = pt2FrameLayout!!.findViewById(R.id.rv_base_pt2) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(inflater.context)
        recyclerView.adapter = adapter
        recyclerView.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        adapter.list.add(listData ?: HFFunGuideMenuResponseBody.ListData())
        return pt2FrameLayout!!
    }

    override fun onResume() {
        super.onResume()
        if (adapter.list.size == 1) {
            getFunGuideList()
        }
    }

    private fun getFunGuideList(pageNum: Int = 0) {
        disposable?.takeIf { !it.isDisposed }?.dispose()
        disposable = HFRetrofit.hfService.getFunGuideList(listData?.id ?: "").composeApi().subscribe({
            takeIf {
                pageNum == 0
            }?.adapter?.list?.removeAll {
                it !is HFFunGuideMenuResponseBody.ListData
            }
            it.data?.data?.let {
                adapter.list.addAll(it)
                listData?._pageNum = pageNum + 1
            }
        }, {
            it.message.kToastLong()
        }, {
            pt2FrameLayout?.complete2()
        })
    }

    override fun onPause() {
        super.onPause()
        disposable?.takeIf { !it.isDisposed }?.dispose()
    }

    val adapter = HFBaseRecyclerAdapter(Factory())

    class Factory : HFViewHolderFactory<HFBaseRecyclerViewHolder<Any>> {

        override fun create(parent: ViewGroup, viewType: Int): HFBaseRecyclerViewHolder<Any> {
            val from = LayoutInflater.from(parent.context)
            return when (viewType) {
                0 -> {
                    TopViewHolder(from.inflate(R.layout.function_guide_list_top, parent, false))
                }
                else -> {
                    ItemViewHolder(from.inflate(R.layout.function_guide_list_item, parent, false))
                }
            }
        }

        override fun getViewType(position: Int): Int {
            return when (position) {
                0 -> 0
                else -> 1
            }
        }

    }

    private class TopViewHolder(itemView: View) : HFBaseRecyclerViewHolder<Any>(itemView) {
        val tvName: TextView = (itemView.findViewById(R.id.tv_fun_guide_name) as TextView?)!!
        val tvAddress: TextView = (itemView.findViewById(R.id.tv_fun_guide_address) as TextView?)!!
        override fun onBindData(d: Any) {
            if (d is HFFunGuideMenuResponseBody.ListData) {
                tvName.text = d.name
                tvAddress.text = d.address
            } else {
                tvName.text = null
                tvAddress.text = null
            }
        }

    }

    private class ItemViewHolder(itemView: View) : HFBaseRecyclerViewHolder<Any>(itemView) {
        val tvName: TextView = (itemView.findViewById(R.id.tv_fun_guide_list_item_title) as TextView?)!!
        val tvTime: TextView = (itemView.findViewById(R.id.tv_fun_guide_list_item_time) as TextView?)!!
        val tvRead: TextView = (itemView.findViewById(R.id.tv_fun_guide_list_item_read) as TextView?)!!
        override fun onBindData(d: Any) {
            if (d is HFFunGuideListResponseBody.ListData) {
                tvName.text = d.title
                tvTime.text = d.publishTime
                tvRead.text = d.readCount
            } else {
                tvName.text = null
                tvTime.text = null
                tvRead.text = null
            }
        }
    }
}