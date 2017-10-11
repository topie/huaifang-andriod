package com.topie.huaifang.function.discovery

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
import android.widget.TextView
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseRecyclerViewHolder
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.*
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunDisNeighborhoodResponseBody
import com.topie.huaifang.http.subscribeResultOkApi
import com.topie.huaifang.imageloader.HFImageView
import com.topie.huaifang.util.HFDimensUtils
import kotlinx.android.synthetic.main.base_pt2_recycler_layout.*
import kotlinx.android.synthetic.main.base_title_layout.*

/**
 * Created by arman on 2017/10/11.
 * 邻里圈
 */
class HFFunDisNeighborhoodActivity : HFBaseTitleActivity() {

    private val mAdapter = Adapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_pt2_recycler_layout)
        setBaseTitle(R.string.facing_index_fun_bazaar)
        setBaseTitleRight("发布")
        tv_base_title_right.setOnClickListener {
            this@HFFunDisNeighborhoodActivity.kStartActivity(HFFunDisNeighborhoodApplyActivity::class.java)
        }
        pt2_base_recycler.setPt2Handler(HFDefaultPt2Handler({ ->
            getNeighborhoodList(0)
        }, { ->
            getNeighborhoodList(mAdapter.mPageSize)
        }))
        rv_base_pt2.layoutManager = LinearLayoutManager(this)
        rv_base_pt2.adapter = mAdapter
    }

    private fun getNeighborhoodList(aPageSize: Int) {
        HFRetrofit.hfService.getFunDisNeighborhoodList(aPageSize).subscribeResultOkApi({
            it.data?.data?.also {
                when (aPageSize) {
                    0 -> mAdapter.setList(it)
                    mAdapter.mPageSize -> mAdapter.addList(it)
                }
                mAdapter.notifyDataSetChanged()
            }
        }, {
            pt2_base_recycler?.complete2()
        }).kInto(pauseDisableList)
    }

    override fun onResume() {
        super.onResume()
        if (mAdapter.itemCount == 0) {
            getNeighborhoodList(0)
        }
    }

    private class Adapter : RecyclerView.Adapter<ViewHolder>() {

        private val mList: MutableList<HFFunDisNeighborhoodResponseBody.ListData> = arrayListOf()
        var mOnCallTel: ((phoneNum: String) -> Unit)? = null
        var mPageSize = 0
            private set

        fun addList(list: List<HFFunDisNeighborhoodResponseBody.ListData>) {
            mList.addAll(list)
            mPageSize++
        }

        fun setList(list: List<HFFunDisNeighborhoodResponseBody.ListData>) {
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
            val kInflate = parent.kInflate(R.layout.function_dis_neighborhood_list_item)
            return ViewHolder(kInflate as ViewGroup)
        }

    }

    private class ViewHolder(val mItemView: ViewGroup) : HFBaseRecyclerViewHolder<HFFunDisNeighborhoodResponseBody.ListData>(mItemView) {

        private val mTvName: TextView = itemView.findViewById(R.id.tv_fun_dis_neighborhood_name)
        private val mTvContent: TextView = itemView.findViewById(R.id.tv_fun_dis_neighborhood_content)
        private val mTvTime: TextView = itemView.findViewById(R.id.tv_fun_dis_neighborhood_time)
        //图片集合
        private val mLLImages: LinearLayout = itemView.findViewById(R.id.ll_fun_dis_neighborhood_images)
        //喜欢列表
        private val mLLLikeList: LinearLayout = itemView.findViewById(R.id.ll_fun_dis_neighborhood_like_list)
        //评论列表
        private val mLLCommList: LinearLayout = itemView.findViewById(R.id.ll_fun_dis_neighborhood_comm_list)

        override fun onBindData(d: HFFunDisNeighborhoodResponseBody.ListData) {
            bindImages(d.images)
            bindLikeList(d.likes)
            bindCommList(d.comments)
            mTvName.text = d.addUserName
            mTvTime.text = d.publishTime
            mTvContent.text = d.content
        }

        private fun bindCommList(list: List<HFFunDisNeighborhoodResponseBody.CommData>?) {
            //删除旧的View
            val viewList = ArrayList<View>().apply {
                (0 until mLLCommList.childCount)
                        .map { mLLCommList.getChildAt(it) }
                        .forEach { add(it) }
                mLLCommList.removeAllViews()
            }
            for (i in 0..(list?.size ?: 0).coerceAtMost(2)) {
                val praiseItem = viewList.kGet(i) ?:
                        mItemView.kInflate(R.layout.function_dis_neighborhood_comm_item).apply {
                            layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                                rightMargin = when (i) {
                                    2 -> 0
                                    else -> HFDimensUtils.dp2px(10.toFloat())
                                }
                                weight = 1.toFloat()
                            }
                        }
                praiseItem.findViewById<TextView>(R.id.tv_fun_dis_neighborhood_comm_name)
                        .text = list?.kGet(i)?.userName
                praiseItem.findViewById<TextView>(R.id.tv_fun_dis_neighborhood_comm_content)
                        .text = list?.kGet(i)?.content
            }
        }

        private fun bindLikeList(list: List<HFFunDisNeighborhoodResponseBody.LikeData>?) {
            //删除旧的View
            val viewList = ArrayList<View>().apply {
                (0 until mLLLikeList.childCount)
                        .map { mLLLikeList.getChildAt(it) }
                        .forEach { add(it) }
                mLLLikeList.removeAllViews()
            }
            for (i in 0..(list?.size ?: 0).coerceAtMost(2)) {
                val praiseItem = viewList.kGet(i) ?:
                        mItemView.kInflate(R.layout.function_dis_neighborhood_praise_item).apply {
                            layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                                rightMargin = when (i) {
                                    2 -> 0
                                    else -> HFDimensUtils.dp2px(10.toFloat())
                                }
                                weight = 1.toFloat()
                            }
                        }
                praiseItem.findViewById<TextView>(R.id.tv_fun_dis_neighborhood_praise_name)
                        .text = list?.kGet(i)?.userName
            }

        }

        private fun bindImages(images: String?) {
            //删除旧的View
            val imageViewList = ArrayList<HFImageView>().apply {
                for (i in 0 until mLLImages.childCount) {
                    val childAt = mLLImages.getChildAt(i)
                    if (childAt is HFImageView) {
                        childAt.setImageDrawable(null)
                        add(childAt)
                    }
                }
                mLLImages.removeAllViews()
            }
            //获取新数据
            val list = images?.split(",") ?: arrayListOf(null, null, null)
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
                mLLImages.addView(hfImageView)
            }
        }

    }
}