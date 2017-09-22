package com.topie.huaifang.function.guide

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseFragment
import com.topie.huaifang.base.HFBaseRecyclerAdapter
import com.topie.huaifang.base.HFBaseRecyclerViewHolder
import com.topie.huaifang.base.HFViewHolderFactory
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

    var menu: HFFunGuideMenuResponseBody.Menu? = null
    var disposable: Disposable? = null

    override fun onCreateViewSupport(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val recyclerView = RecyclerView(inflater.context)
        recyclerView.layoutManager = LinearLayoutManager(inflater.context)
        recyclerView.adapter = adapter
        recyclerView.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        adapter.list.add(menu ?: HFFunGuideMenuResponseBody.Menu())
        return recyclerView
    }

    override fun onResume() {
        super.onResume()
        if (adapter.list.size == 1) {
            disposable = HFRetrofit.hfService.getFunGuideList(menu?.id ?: "").composeApi().subscribe({
                adapter.list.removeAll {
                    it !is HFFunGuideMenuResponseBody.Menu
                }
                it?.data?.data?.let {
                    adapter.list.addAll(it)
                }
            }, {

            })
        }
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
        val tvName = itemView.findViewById<TextView>(R.id.tv_fun_guide_name)!!
        val tvAddress = itemView.findViewById<TextView>(R.id.tv_fun_guide_address)!!
        override fun bindData(d: Any) {
            if (d is HFFunGuideMenuResponseBody.Menu) {
                tvName.text = d.name
                tvAddress.text = d.address
            } else {
                tvName.text = null
                tvAddress.text = null
            }
        }

    }

    private class ItemViewHolder(itemView: View) : HFBaseRecyclerViewHolder<Any>(itemView) {
        val tvName = itemView.findViewById<TextView>(R.id.tv_fun_guide_list_item_title)!!
        val tvTime = itemView.findViewById<TextView>(R.id.tv_fun_guide_list_item_time)!!
        val tvRead = itemView.findViewById<TextView>(R.id.tv_fun_guide_list_item_read)!!
        override fun bindData(d: Any) {
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