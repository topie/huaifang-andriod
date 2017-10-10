package com.topie.huaifang.function.live

import android.net.Uri
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
import com.topie.huaifang.base.HFViewHolderFactory
import com.topie.huaifang.extensions.*
import com.topie.huaifang.function.kShowTelDialog
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunLiveBazaarResponseBody
import com.topie.huaifang.http.subscribeResultOkApi
import com.topie.huaifang.imageloader.HFImageView
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
            getBazaarList(0)
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
            getBazaarList(0)
        }
    }

    private fun getBazaarList(aPageSize: Int) {
        HFRetrofit.hfService.getFunLiveBazaarList(mType, aPageSize).subscribeResultOkApi({
            it.data?.data?.also {
                when (aPageSize) {
                    0 -> mAdapter.setList(it)
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
        var mPageSize = 0
            private set

        fun addList(list: List<HFFunLiveBazaarResponseBody.ListData>) {
            mList.addAll(list)
            mPageSize++
        }

        fun setList(list: List<HFFunLiveBazaarResponseBody.ListData>) {
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
            llImages.removeAllViews()
            d.images?.split(",")?.map {
                it.trim().kParseUrl() ?: Uri.EMPTY
            }?.forEachIndexed { index, uri ->
                if (index >= 3) {
                    return
                }
                val hfImageView = HFImageView(itemView.context).apply {
                    setAspectRatio(1.toFloat())
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT).also {
                        it.rightMargin = HFDimensUtils.dp2px(10.toFloat())
                    }
                    setAspectRatio(1.toFloat())
                    loadImageUri(uri)
                }
                llImages.addView(hfImageView)
            }

        }

        private class ImagesViewHolder(itemView: View) : HFBaseRecyclerViewHolder<Uri>(itemView, true) {
            val imageView: HFImageView = itemView as HFImageView
            override fun onBindData(d: Uri) {
                imageView.loadImageUri(d)
            }

            companion object CREATOR : HFViewHolderFactory<ImagesViewHolder> {

                override fun create(parent: ViewGroup, viewType: Int): ImagesViewHolder {
                    val hfImageView = HFImageView(parent.context)
                    val dp80 = HFDimensUtils.dp2px(80.toFloat())
                    hfImageView.layoutParams = RecyclerView.LayoutParams(dp80, dp80).also {
                        it.rightMargin = HFDimensUtils.dp2px(10.toFloat())
                    }
                    hfImageView.setAspectRatio(1.toFloat())
                    return ImagesViewHolder(hfImageView)
                }
            }
        }

    }

}