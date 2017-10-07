package com.topie.huaifang.function.dispute

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseRecyclerViewHolder
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.*
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFfunDisputeMediatorResponseBody
import com.topie.huaifang.http.subscribeResultOkApi
import com.topie.huaifang.view.HFTipDialog

/**
 * Created by arman on 2017/10/3.
 * 纠纷调解
 */
class HFFunDisputeMediatorActivity : HFBaseTitleActivity() {

    private val mAdapter = Adapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val recyclerView = RecyclerView(this)
        setContentView(recyclerView)
        setBaseTitle(R.string.facing_index_fun_dispute)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = mAdapter
        mAdapter.commObj = {

        }
        mAdapter.telObj = {
            val builder = HFTipDialog.Builder()
            builder.content = "确定拨打：${it.contactPhone}?"
            builder.onOkClicked = { kTel(it.contactPhone ?: "") }
            builder.show(this@HFFunDisputeMediatorActivity)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mAdapter.commObj = null
        mAdapter.telObj = null
    }

    override fun onResume() {
        super.onResume()
        if (mAdapter.itemCount == 0) {
            HFRetrofit.hfService.getFunDisputeMediatorList().subscribeResultOkApi {
                it.data?.data?.kInsteadTo(mAdapter.mList)
            }.kInto(pauseDisableList)
        }
    }

    private class Adapter : RecyclerView.Adapter<ViewHolder>() {

        val mList: MutableList<HFfunDisputeMediatorResponseBody.ListData> = arrayListOf()

        var commObj: ((d: HFfunDisputeMediatorResponseBody.ListData) -> Unit)? = null
        var telObj: ((d: HFfunDisputeMediatorResponseBody.ListData) -> Unit)? = null

        override fun getItemCount(): Int {
            return mList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindData(mList[position])
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val viewHolder = ViewHolder(parent.kInflate(R.layout.function_dispute_mediator_list_item))
            viewHolder.llComm.setOnClickListener {
                commObj?.invoke(mList[viewHolder.adapterPosition])
            }
            viewHolder.llTel.setOnClickListener {
                telObj?.invoke(mList[viewHolder.adapterPosition])
            }
            return viewHolder
        }

    }

    private class ViewHolder(itemView: View) : HFBaseRecyclerViewHolder<HFfunDisputeMediatorResponseBody.ListData>(itemView) {
        val llComm = itemView.kFindViewById<View>(R.id.ll_dispute_mediator_list_item_comm)
        val llTel = itemView.kFindViewById<View>(R.id.ll_dispute_mediator_list_item_tel)
        val tvTitle: TextView = itemView.kFindViewById(R.id.tv_dispute_mediator_list_item_title)
        val tvName: TextView = itemView.kFindViewById(R.id.tv_dispute_mediator_list_item_name)
        val tvLocation: TextView = itemView.kFindViewById(R.id.tv_dispute_mediator_list_item_location)

        override fun onBindData(d: HFfunDisputeMediatorResponseBody.ListData) {
            tvTitle.text = d.title
            tvName.text = d.contactPerson
            tvLocation.text = d.address
        }
    }
}