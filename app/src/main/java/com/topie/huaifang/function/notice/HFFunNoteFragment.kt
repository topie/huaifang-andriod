package com.topie.huaifang.function.notice

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.davdian.ptr.Pt2FrameLayout
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseFragment
import com.topie.huaifang.base.HFBaseRecyclerViewHolder
import com.topie.huaifang.extensions.*
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunPublicResponseBody
import com.topie.huaifang.http.subscribeResultOkApi

/**
 * Created by arman on 2017/10/12.
 * 通知公告
 */
class HFFunNoteFragment : HFBaseFragment() {

    var noteType: Int = 0// 0：社区公告 1：物业公告

    var inflate: Pt2FrameLayout? = null
    private val mAdapter: Adapter = Adapter()

    companion object {
        fun newInstance(type: Int): HFFunNoteFragment {
            val fragment = HFFunNoteFragment()
            fragment.noteType = type
            return fragment
        }
    }

    override fun onCreateViewSupport(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        inflate = inflater.inflate(R.layout.base_pt2_recycler_layout, container, false) as Pt2FrameLayout
        inflate!!.setPt2Handler(HFDefaultPt2Handler({ ->
            getData(1)
        }, { ->
            getData(mAdapter.mPageSize)
        }))
        val rv: RecyclerView = inflate!!.findViewById(R.id.rv_base_pt2)
        rv.layoutManager = LinearLayoutManager(inflater.context)
        rv.adapter = mAdapter
        return inflate!!
    }

    override fun onResume() {
        super.onResume()
        if (mAdapter.itemCount == 0) {
            getData(1)
        }
    }


    private fun getData(pageNum: Int = 1) {
        HFRetrofit.hfService.getFunPublicList(noteType, pageNum).subscribeResultOkApi({
            it.data?.data?.let {
                when (pageNum) {
                    0, 1 -> mAdapter.setList(it)
                    else -> mAdapter.addList(it)
                }
                mAdapter.notifyDataSetChanged()
            }
        }, {
            inflate?.complete2()
        }).kInto(pauseDisableList)
    }

    private class Adapter : RecyclerView.Adapter<ViewHolder>() {

        private val mList: MutableList<HFFunPublicResponseBody.ListData> = arrayListOf()
        var mPageSize = 1
            private set

        fun addList(list: List<HFFunPublicResponseBody.ListData>) {
            mList.addAll(list)
            mPageSize++
        }

        fun setList(list: List<HFFunPublicResponseBody.ListData>) {
            mList.clear()
            mList.addAll(list)
            mPageSize = 1
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindData(mList[position])
        }

        override fun getItemCount(): Int {
            return mList.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val kInflate = parent.kInflate(R.layout.function_live_public_list_item)
            return ViewHolder(kInflate)
        }

    }

    private class ViewHolder(itemView: View) : HFBaseRecyclerViewHolder<HFFunPublicResponseBody.ListData>(itemView, true) {

        val tvTitle: TextView = itemView.kFindViewById(R.id.tv_fun_live_public_title)
        val tvTime: TextView = itemView.kFindViewById(R.id.tv_fun_live_public_time)
        val tvPublisher: TextView = itemView.kFindViewById(R.id.tv_fun_live_public_publisher)

        override fun onItemClicked(d: HFFunPublicResponseBody.ListData?) {
            super.onItemClicked(d)
            d ?: return
            val bundle = Bundle()
            bundle.putSerializable(HFFunNoteDetailActivity.EXTRA_DATA, d)
            itemView.kStartActivity(HFFunNoteDetailActivity::class.java, bundle)
        }

        override fun onBindData(d: HFFunPublicResponseBody.ListData) {
            tvTitle.text = d.title
            tvTime.text = d.cTime
            tvPublisher.text = d.cUser
        }
    }

}