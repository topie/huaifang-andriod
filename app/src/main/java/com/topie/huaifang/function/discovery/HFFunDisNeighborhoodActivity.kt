package com.topie.huaifang.function.discovery

import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
import android.widget.TextView
import com.topie.huaifang.ImageBrowserActivity
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseRecyclerViewHolder
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.*
import com.topie.huaifang.global.RequestCode
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunDisNeighborhoodResponseBody
import com.topie.huaifang.http.subscribeResultOkApi
import com.topie.huaifang.util.HFDimensUtils
import com.topie.huaifang.view.HFImagesLayout
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
        setBaseTitle(R.string.facing_discovery_neighborhood)
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
        var mPageSize = 1
            private set

        fun addList(list: List<HFFunDisNeighborhoodResponseBody.ListData>) {
            mList.addAll(list)
            mPageSize++
        }

        fun setList(list: List<HFFunDisNeighborhoodResponseBody.ListData>) {
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
            val kInflate = parent.kInflate(R.layout.function_dis_neighborhood_list_item)
            return ViewHolder(kInflate as ViewGroup)
        }

    }

    private class ViewHolder(val mItemView: ViewGroup) : HFBaseRecyclerViewHolder<HFFunDisNeighborhoodResponseBody.ListData>(mItemView) {

        private val mTvName: TextView = itemView.findViewById(R.id.tv_fun_dis_neighborhood_name)
        private val mTvContent: TextView = itemView.findViewById(R.id.tv_fun_dis_neighborhood_content)
        private val mTvTime: TextView = itemView.findViewById(R.id.tv_fun_dis_neighborhood_time)
        //图片集合
        private val hfImagesLayout: HFImagesLayout = itemView.findViewById(R.id.il_fun_dis_neighborhood_images)
        //喜欢列表
        private val mLLLikeList: LinearLayout = itemView.findViewById(R.id.ll_fun_dis_neighborhood_like_list)
        //评论列表
        private val mLLCommList: LinearLayout = itemView.findViewById(R.id.ll_fun_dis_neighborhood_comm_list)
        var imageList: List<String>? = null

        init {
            hfImagesLayout.setOnItemClickListener(object : HFImagesLayout.OnItemClickListener {
                override fun onAdd() {

                }

                override fun onImageClicked(uri: Uri?, position: Int) {
                    val context = mItemView.context.kFindActivity() ?: mItemView.context
                    val list = imageList ?: arrayListOf()
                    ImageBrowserActivity.openImageBrowserActivity(context, RequestCode.IMAGE_BROWSER, -1, list, null, position)
                }
            })
        }

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
            //获取新数据
            imageList = images
                    ?.split(",")
                    ?.map { it.trim() }
                    ?.filter { it.isNotEmpty() }
                    ?.map { HFRetrofit.parseUrlToBase(it) }
            val list = imageList?.filterIndexed { index, _ -> index < 4 }?.map { Uri.parse(it) }
            if (list.kIsEmpty()) {
                hfImagesLayout.visibility = View.GONE
            } else {
                hfImagesLayout.visibility = View.VISIBLE
                hfImagesLayout.setPathList(list)
            }
        }

    }
}