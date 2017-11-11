package com.topie.huaifang.function.live

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.davdian.ptr.Pt2FrameLayout
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseFragment
import com.topie.huaifang.base.HFBaseRecyclerViewHolder
import com.topie.huaifang.extensions.*
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunLiveBazaarResponseBody
import com.topie.huaifang.http.subscribeResultOkApi
import com.topie.huaifang.imageloader.HFImageView
import com.topie.huaifang.kShowTelDialog
import com.topie.huaifang.util.HFDimensUtils

/**
 * Created by arman on 2017/10/10.
 * 社区集市
 */
class HFFunLiveBazaarFragment : HFBaseFragment() {

    //0: 跳骚市场；1：车位共享
    private var mType: Int = 0
    private var inflate: Pt2FrameLayout? = null

    private val mAdapter = Adapter()

    companion object {

        fun newInstance(type: Int): HFFunLiveBazaarFragment {
            return HFFunLiveBazaarFragment().apply { mType = type }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAdapter.mOnCallTel = {
            activity?.kShowTelDialog(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mAdapter.mOnCallTel = null
    }

    override fun onCreateViewSupport(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        inflate = inflater.inflate(R.layout.base_pt2_recycler_layout, container, false) as Pt2FrameLayout
        inflate!!.setPt2Handler(HFDefaultPt2Handler({ ->
            getBazaarList(1)
        }, { ->
            getBazaarList(mAdapter.mPageSize)
        }))
        val recyclerView = inflate!!.findViewById<RecyclerView>(R.id.rv_base_pt2)
        recyclerView.layoutManager = LinearLayoutManager(inflater.context)
        recyclerView.adapter = mAdapter
        return inflate!!
    }

    override fun onResume() {
        super.onResume()
        if (mAdapter.itemCount == 0) {
            getBazaarList(1)
        }
    }

    private fun getBazaarList(aPageSize: Int) {
        HFRetrofit.hfService.getFunLiveBazaarList(mType, aPageSize).subscribeResultOkApi({
            it.data?.data?.also {
                when (aPageSize) {
                    0, 1 -> mAdapter.setList(it)
                    mAdapter.mPageSize -> {
                        mAdapter.addList(it)
                    }
                }
                mAdapter.notifyDataSetChanged()
            }
        }, {
            inflate?.complete2()
        }).kInto(pauseDisableList)
    }

    private class Adapter : RecyclerView.Adapter<ViewHolder>() {

        private val mList: MutableList<HFFunLiveBazaarResponseBody.ListData> = arrayListOf()
        var mOnCallTel: ((phoneNum: String) -> Unit)? = null
        var mPageSize = 1
            private set

        fun addList(list: List<HFFunLiveBazaarResponseBody.ListData>) {
            mList.addAll(list)
            mPageSize++
        }

        fun setList(list: List<HFFunLiveBazaarResponseBody.ListData>) {
            mList.clear()
            mList.addAll(list)
            mPageSize = 2
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindData(mList[position])
        }

        override fun getItemCount(): Int {
            return mList.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val kInflate = parent.kInflate(R.layout.function_live_bazaar_list_item)
            return ViewHolder(kInflate).also {
                it.mOnCallTel = {
                    mOnCallTel?.invoke(it)
                }
            }
        }

    }

    private class ViewHolder(itemView: View) : HFBaseRecyclerViewHolder<HFFunLiveBazaarResponseBody.ListData>(itemView, true) {

        private val tvName: TextView = itemView.findViewById(R.id.tv_fun_live_bazaar_name)
        private val tvTime: TextView = itemView.findViewById(R.id.tv_fun_live_bazaar_time)
        private val tvContent: TextView = itemView.findViewById(R.id.tv_fun_live_bazaar_content)
        private val llImages: LinearLayout = itemView.findViewById(R.id.ll_fun_live_bazaar_images)
        var mOnCallTel: ((phoneNum: String) -> Unit)? = null

        init {
            itemView.findViewById<View>(R.id.ll_fun_live_bazaar_tel).setOnClickListener {
                mOnCallTel?.invoke(data?.contactPhone ?: "-1")
            }
        }

        override fun onItemClicked(d: HFFunLiveBazaarResponseBody.ListData?) {
            super.onItemClicked(d)
            if (d == null) {
                return
            }
            val bundle = Bundle()
            bundle.putSerializable(HFFunLiveBazaarDetailActivity.EXTRA_DATA, d)
            itemView.kStartActivity(HFFunLiveBazaarDetailActivity::class.java, bundle)
        }

        override fun onBindData(d: HFFunLiveBazaarResponseBody.ListData) {
            tvName.text = d.addUserName
            tvTime.text = d.publishTime
            tvContent.text = d.content
            //删除旧的View
            val imageViewList = ArrayList<HFImageView>().apply {
                for (i in 0 until llImages.childCount) {
                    val childAt = llImages.getChildAt(i)
                    if (childAt is HFImageView) {
                        childAt.setImageDrawable(null)
                        add(childAt)
                    }
                }
                llImages.removeAllViews()
            }
            //获取新数据
            val list = d.images?.split(",") ?: arrayListOf(null, null, null)
            //添加新的View
            for (i in 0..2) {
                val hfImageView = imageViewList.kGet(i) ?: HFImageView(itemView.context).apply {
                    setAspectRatio(1.toFloat())
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT).apply {
                        rightMargin = when (i) {
                            2 -> 0
                            else -> HFDimensUtils.dp2px(10.toFloat())
                        }
                        weight = 1.toFloat()
                    }
                }
                hfImageView.loadImageUri(list.kGet(i)?.trim()?.kParseUrl())
                llImages.addView(hfImageView)
            }

        }
    }

}