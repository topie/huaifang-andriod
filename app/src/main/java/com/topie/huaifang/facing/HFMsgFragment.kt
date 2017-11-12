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
import com.topie.huaifang.base.HFBaseParentViewHolder
import com.topie.huaifang.base.HFBaseRecyclerViewHolder
import com.topie.huaifang.base.HFEmptyRecyclerViewHolder
import com.topie.huaifang.extensions.*
import com.topie.huaifang.function.communication.HFCommChatActivity
import com.topie.huaifang.global.IMConstant
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.communication.HFCommMsgDetail
import com.topie.huaifang.http.subscribeResultOkApi
import com.topie.huaifang.imageloader.HFImageView
import com.topie.huaifang.util.HFDimensUtils
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 * Created by arman on 2017/9/16.
 * 我的消息列表
 */
class HFMsgFragment : HFBaseFragment() {

    private lateinit var pt2FrameLayout: Pt2FrameLayout
    private var adapter: Adapter = Adapter()
    private var mDisposable: Disposable? = null

    override fun onCreateViewSupport(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        pt2FrameLayout = inflater.inflate(R.layout.base_pt2_recycler_layout, container, false) as Pt2FrameLayout
        pt2FrameLayout.setPt2Handler(HFDefaultPt2Handler { getMsgList() })
        val recyclerView: RecyclerView = pt2FrameLayout.kFindViewById(R.id.rv_base_pt2)
        recyclerView.layoutManager = LinearLayoutManager(pt2FrameLayout.context)
        recyclerView.adapter = adapter
        return pt2FrameLayout
    }

    override fun onResume() {
        super.onResume()
        getMsgList()
    }

    private fun getMsgList() {
        mDisposable?.takeIf { !it.isDisposed }?.dispose()
        Observable.interval(0, IMConstant.FACING_MSG_PERIOD, TimeUnit.MILLISECONDS).flatMap {
            log("getMsgList[$it]")
            HFRetrofit.hfService.getCommMsgList()
        }.subscribeResultOkApi({
            it.data?.data?.let {
                adapter.setListData(it)
                adapter.notifyDataSetChanged()
            }
        }, {
            pt2FrameLayout.complete2()
        }).also { mDisposable = it }.kInto(pauseDisableList)
    }

    private class ViewHolder(parent: ViewGroup) : HFBaseParentViewHolder<HFCommMsgDetail>(parent, R.layout.facing_msg_list_item, true) {

        private val ivIcon: HFImageView = itemView.kFindViewById(R.id.iv_facing_msg_head)
        private val tvName: TextView = itemView.kFindViewById(R.id.tv_facing_msg_name)
        private val tvContent: TextView = itemView.kFindViewById(R.id.tv_facing_msg_content)
        private val tvTime: TextView = itemView.kFindViewById(R.id.tv_facing_msg_time)

        override fun onBindData(d: HFCommMsgDetail) {
            ivIcon.loadImageUri(d.icon?.kParseUrl())
            tvName.text = d.fromUserName
            tvContent.text = d.title
            tvTime.text = d.createTime?.kSimpleFormatToDate()?.kSplit()
        }

        override fun onItemClicked(d: HFCommMsgDetail?) {
            super.onItemClicked(d)
            if (d?.type == 1) {
                val userId = d.fromUserId
                val bundle = Bundle()
                bundle.putInt(HFCommChatActivity.EXTRA_CONNECT_USER_ID, userId)
                itemView.kStartActivity(HFCommChatActivity::class.java, bundle)
            }
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
                    holder.itemView.setBackgroundColor(kGetColor(R.color.colorLightGray))
                    holder.itemView.kHiddenChildren()
                    holder
                }
                else -> {
                    ViewHolder(parent)
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