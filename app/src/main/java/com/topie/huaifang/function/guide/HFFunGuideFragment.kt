package com.topie.huaifang.function.guide

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
import com.topie.huaifang.base.HFBaseRecyclerViewHolder
import com.topie.huaifang.extensions.kFindActivity
import com.topie.huaifang.extensions.kFindViewById
import com.topie.huaifang.extensions.kParseUrl
import com.topie.huaifang.extensions.kStartActivity
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunGuideListResponseBody
import com.topie.huaifang.http.bean.function.HFFunGuideMenuResponseBody
import com.topie.huaifang.http.subscribeResultOkApi
import com.topie.huaifang.imageloader.HFImageView
import com.topie.huaifang.kShowTelDialog
import io.reactivex.disposables.Disposable

/**
 * Created by arman on 2017/9/22.
 * 办事指南列表
 */
class HFFunGuideFragment : HFBaseFragment() {

    private var listData: HFFunGuideMenuResponseBody.ListData? = null
    private var disposable: Disposable? = null
    private var pt2FrameLayout: Pt2FrameLayout? = null
    private val adapter = Adapter()

    companion object {
        fun newInstance(aData: HFFunGuideMenuResponseBody.ListData?): HFFunGuideFragment {
            val fragment = HFFunGuideFragment()
            fragment.listData = aData
            return fragment
        }
    }

    override fun onCreateViewSupport(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        pt2FrameLayout = inflater.inflate(R.layout.base_pt2_recycler_layout, container, false) as Pt2FrameLayout
        pt2FrameLayout!!.setPt2Handler(object : AbsPt2Handler() {
            override fun checkCanDoLoad(frame: PtlFrameLayout?, content: View?, footer: View?): Boolean {
                return super.checkCanDoLoad(frame, content, footer) && adapter.list.size > 1
            }

            override fun onLoadMoreBegin(frame: PtlFrameLayout?) {
                getFunGuideList(adapter.pageNumber)
            }

            override fun onRefreshBegin(frame: PtrFrameLayout?) {
                getFunGuideList()
            }
        })
        val recyclerView: RecyclerView = pt2FrameLayout!!.kFindViewById(R.id.rv_base_pt2)
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

    private fun getFunGuideList(pageNum: Int = 1) {
        disposable?.takeIf { !it.isDisposed }?.dispose()
        disposable = HFRetrofit.hfService.getFunGuideList(listData?.id ?: "").subscribeResultOkApi({
            it.data?.data?.let {
                when (pageNum) {
                    0, 1 -> adapter.setList(it)
                    else -> adapter.addList(it)
                }
                adapter.notifyDataSetChanged()
            }
        }, {
            pt2FrameLayout?.complete2()
        })
    }

    override fun onPause() {
        super.onPause()
        disposable?.takeIf { !it.isDisposed }?.dispose()
    }

    class Adapter(val list: MutableList<Any> = arrayListOf()) : RecyclerView.Adapter<HFBaseRecyclerViewHolder<Any>>() {

        var pageNumber: Int = 1
            private set

        companion object {
            const val TYPE_TOP = 0
            const val TYPE_NORMAL = 1
        }

        fun setList(aList: List<HFFunGuideListResponseBody.ListData>) {
            list.removeAll { it is HFFunGuideListResponseBody.ListData }
            list.addAll(aList)
            pageNumber = 2
        }

        fun addList(aList: List<HFFunGuideListResponseBody.ListData>) {
            list.addAll(aList)
            pageNumber++
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: HFBaseRecyclerViewHolder<Any>?, position: Int) {
            holder?.bindData(list[position])
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HFBaseRecyclerViewHolder<Any> {
            val from = LayoutInflater.from(parent.context)
            return when (viewType) {
                TYPE_TOP -> {
                    TopViewHolder(from.inflate(R.layout.function_guide_list_top, parent, false))
                }
                else -> {
                    ItemViewHolder(from.inflate(R.layout.function_guide_list_item, parent, false))
                }
            }
        }

        override fun getItemViewType(position: Int): Int {
            return when (position) {
                0 -> TYPE_TOP
                else -> TYPE_NORMAL
            }
        }
    }

    private class TopViewHolder(itemView: View) : HFBaseRecyclerViewHolder<Any>(itemView) {
        val tvName: TextView = itemView.kFindViewById(R.id.tv_fun_guide_name)
        val tvAddress: TextView = itemView.kFindViewById(R.id.tv_fun_guide_address)
        var phone: String? = null

        init {
            itemView.findViewById<View>(R.id.ll_fun_tel).setOnClickListener {
                val context = itemView.context
                context.kFindActivity()?.takeIf {
                    phone != null
                }?.also {
                    it.kShowTelDialog(phone!!)
                }
            }

        }

        override fun onBindData(d: Any) {
            if (d is HFFunGuideMenuResponseBody.ListData) {
                tvName.text = d.name
                tvAddress.text = d.address
                phone = d.phone
            } else {
                tvName.text = null
                tvAddress.text = null
                phone = null
            }
        }
    }

    private class ItemViewHolder(itemView: View) : HFBaseRecyclerViewHolder<Any>(itemView, true) {
        val imageView: HFImageView = itemView.kFindViewById(R.id.iv_fun_guide_list_item)
        val tvName: TextView = itemView.kFindViewById(R.id.tv_fun_guide_list_item_title)
        val tvTime: TextView = itemView.kFindViewById(R.id.tv_fun_guide_list_item_time)
        val tvRead: TextView = itemView.kFindViewById(R.id.tv_fun_guide_list_item_read)
        override fun onBindData(d: Any) {
            if (d is HFFunGuideListResponseBody.ListData) {
                imageView.loadImageUri(d.image?.kParseUrl())
                tvName.text = d.title
                tvTime.text = d.publishTime
                val text = "${d.readCount}人阅读"
                tvRead.text = text
            } else {
                tvName.text = null
                tvTime.text = null
                tvRead.text = null
            }
        }

        override fun onItemClicked(d: Any?) {
            super.onItemClicked(d)
            val bundle = Bundle()
            if (d is HFFunGuideListResponseBody.ListData) {
                bundle.putInt(HFFunGuideDetailActivity.EXTRA_ID, d.id)
                itemView.kStartActivity(HFFunGuideDetailActivity::class.java, bundle)
            }
        }
    }
}