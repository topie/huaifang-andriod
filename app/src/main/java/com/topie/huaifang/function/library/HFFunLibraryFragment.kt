package com.topie.huaifang.function.library

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
import com.topie.huaifang.base.HFBaseRecyclerAdapter
import com.topie.huaifang.base.HFBaseRecyclerViewHolder
import com.topie.huaifang.base.HFViewHolderFactory
import com.topie.huaifang.extensions.*
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunLibraryBookResponseBody
import com.topie.huaifang.http.subscribeResultOkApi
import com.topie.huaifang.imageloader.HFImageView

/**
 * Created by arman on 2017/10/2.
 * 社区图书馆
 */
class HFFunLibraryFragment : HFBaseFragment() {
    //分类
    private lateinit var category: String

    private var pt2FrameLayout: Pt2FrameLayout? = null

    private val list: MutableList<HFFunLibraryBookResponseBody.ListData> = arrayListOf()
    private val mAdapter = HFBaseRecyclerAdapter(ViewHolder.CREATOR,list)
    private var mPageNum = 0

    companion object {
        fun newInstance(category: String): HFFunLibraryFragment {
            val fragment = HFFunLibraryFragment()
            fragment.category = category
            return fragment
        }
    }

    override fun onCreateViewSupport(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        pt2FrameLayout = inflater.inflate(R.layout.base_pt2_recycler_layout, container, false) as Pt2FrameLayout
        pt2FrameLayout!!.setPt2Handler(HFDefaultPt2Handler({ ->
            getListData(1)
        }, { ->
            getListData(mPageNum)
        }))
        val recyclerView: RecyclerView = pt2FrameLayout!!.kFindViewById(R.id.rv_base_pt2)
        recyclerView.layoutManager = LinearLayoutManager(inflater.context)
        recyclerView.adapter = mAdapter
        mAdapter.notifyDataSetChanged()
        return pt2FrameLayout!!
    }

    override fun onResume() {
        super.onResume()
        if (list.size == 0) {
            getListData(0)
        }
    }

    /**
     * 获取数据
     */
    private fun getListData(pageNum: Int) {
        HFRetrofit.hfService.getFunLibraryList(category, pageNum).subscribeResultOkApi({
            it.data?.data?.takeIf {
                it.isNotEmpty()
            }?.also {
                when (pageNum) {
                    0,1 -> {  //从第0页获取数据可认为是刷新页面
                        mPageNum = pageNum + 1
                        list.clear()
                        list.addAll(it)
                        mAdapter.notifyDataSetChanged()
                    }
                    mPageNum -> {
                        mPageNum = pageNum + 1
                        list.addAll(it)
                        mAdapter.notifyItemRangeInserted(list.size - it.size, it.size)
                    }
                    else -> {
                        log("$pageNum != ${this@HFFunLibraryFragment.mPageNum}")
                    }
                }
            }
        }, {
            pt2FrameLayout?.complete2()
        }).kInto(pauseDisableList)
    }

    private class ViewHolder(itemView: View) : HFBaseRecyclerViewHolder<HFFunLibraryBookResponseBody.ListData>(itemView, true) {

        private val ivCover: HFImageView = itemView.kFindViewById(R.id.iv_fun_library_cover)
        private val tvStatus: TextView = itemView.kFindViewById(R.id.tv_fun_library_list_item_status)
        private val tvName: TextView = itemView.kFindViewById(R.id.tv_fun_library_list_item_name)
        private val tvAuthor: TextView = itemView.kFindViewById(R.id.tv_fun_library_list_item_author)
        private val tvDesc: TextView = itemView.kFindViewById(R.id.tv_fun_library_list_item_desc)
        override fun onBindData(d: HFFunLibraryBookResponseBody.ListData) {
            ivCover.loadImageUri(d.image?.kParseUrl())
            tvStatus.text = d.status
            tvName.text = d.bookName
            tvAuthor.text = d.author
            tvDesc.text = d.content
        }

        companion object CREATOR : HFViewHolderFactory<ViewHolder> {

            override fun create(parent: ViewGroup, viewType: Int): ViewHolder {
                val from = LayoutInflater.from(parent.context)
                val inflate = from.inflate(R.layout.function_library_book_list_item, parent, false)
                return ViewHolder(inflate)
            }

        }
    }
}
