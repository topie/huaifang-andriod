package com.topie.huaifang.facing

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.LayoutParams
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.davdian.ptr.Pt2FrameLayout
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseFragment
import com.topie.huaifang.base.HFBaseRecyclerViewHolder
import com.topie.huaifang.base.HFEmptyRecyclerViewHolder
import com.topie.huaifang.extensions.*
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.communication.HFCommMsgDetail
import com.topie.huaifang.http.subscribeApi
import com.topie.huaifang.imageloader.HFImageView
import com.topie.huaifang.util.HFDimensUtils
import io.reactivex.disposables.Disposable

/**
 * Created by arman on 2017/9/16.
 * 我的消息列表
 */
class HFMsgFragment : HFBaseFragment() {

    private lateinit var pt2FrameLayout: Pt2FrameLayout
    private var adapter: Adapter = Adapter()
    private var disposable: Disposable? = null

    override fun onCreateViewSupport(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        pt2FrameLayout = inflater.inflate(R.layout.base_pt2_recycler_layout, container, false) as Pt2FrameLayout
        pt2FrameLayout.setPt2Handler(HFDefaultPt2Handler { getMsgList() })
        val recyclerView: RecyclerView = pt2FrameLayout.findViewById(R.id.rv_base_pt2) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(pt2FrameLayout.context)
        recyclerView.adapter = adapter
        return pt2FrameLayout
    }

    override fun onResume() {
        super.onResume()
        getMsgList()
    }

    override fun onPause() {
        super.onPause()
        disposable?.takeIf { !it.isDisposed }?.dispose()
    }

    private fun getMsgList() {
        disposable?.takeIf { !it.isDisposed }?.dispose()
        disposable = HFRetrofit.hfService.getCommMsgList().subscribeApi({
            it.data?.data?.let {
                adapter.setListData(it)
                adapter.notifyDataSetChanged()
            }
        }, {
            pt2FrameLayout.complete2()
        })
    }

    private class ViewHolder(itemView: View) : HFBaseRecyclerViewHolder<HFCommMsgDetail>(itemView, true) {
        private val ivIcon: HFImageView = itemView.findViewById(R.id.iv_facing_list_icon) as HFImageView

        private val tvName: TextView = itemView.findViewById(R.id.tv_facing_list_name) as TextView
        override fun onBindData(d: HFCommMsgDetail) {
            ivIcon.loadImageUri(d.icon?.kParseUrl())
            tvName.text = d.title
        }

    }

    private class Adapter : RecyclerView.Adapter<HFBaseRecyclerViewHolder<HFCommMsgDetail>>() {

        private val list: MutableList<HFCommMsgDetail> = arrayListOf()

        init {
            val detail = HFCommMsgDetail()
            this.list.add(0, detail)
        }

        fun setListData(list: List<HFCommMsgDetail>) {
            this.list.clear()
            val detail = HFCommMsgDetail()
            this.list.add(0, detail)
            this.list.addAll(list)
        }

        override fun onBindViewHolder(holder: HFBaseRecyclerViewHolder<HFCommMsgDetail>, position: Int) {
            holder.bindData(list.kGet(position) ?: HFCommMsgDetail())
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HFBaseRecyclerViewHolder<HFCommMsgDetail> {
            return when (viewType) {
                -1 -> {
                    val holder = HFEmptyRecyclerViewHolder<HFCommMsgDetail>(View(parent.context))
                    holder.itemView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, HFDimensUtils.dp2px(10.toFloat()))
                    holder.itemView.setBackgroundColor(kGetColor(R.color.colorGray))
                    holder.itemView.kHiddenChildren()
                    holder
                }
                else -> {
                    val from = LayoutInflater.from(parent.context)
                    val itemView = from.inflate(R.layout.facing_list_item, parent, false)
                    ViewHolder(itemView)
                }
            }
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun getItemViewType(position: Int): Int {
            return when (position) {
                0 -> -1
                else -> 1
            }
        }

    }

}