package com.topie.huaifang.function.yellowpage

import android.os.Bundle
import android.support.v4.util.ArrayMap
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.TextView
import com.davdian.ptr.Pt2FrameLayout
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseParentViewHolder
import com.topie.huaifang.base.HFBaseRecyclerViewHolder
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.HFDefaultPt2Handler
import com.topie.huaifang.extensions.kFindViewById
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunYellowPageResponseBody
import com.topie.huaifang.http.subscribeResultOkApi
import com.topie.huaifang.kShowTelDialog
import com.topie.huaifang.util.HFLogger
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.base_pt2_recycler_layout.*

/**
 * Created by arman on 2017/9/29.
 * 迷你黄页
 */
class HFFunYellowPageActivity : HFBaseTitleActivity() {

    private var disposable: Disposable? = null

    private val adapter: Adapter = Adapter()
    private var pt2FrameLayout: Pt2FrameLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_pt2_recycler_layout)
        setBaseTitle(R.string.facing_index_fun_yellow_book)
        pt2FrameLayout = kFindViewById(R.id.pt2_base_recycler)
        pt2FrameLayout!!.setPt2Handler(HFDefaultPt2Handler { getFunPartyMembersList() })
        rv_base_pt2.layoutManager = LinearLayoutManager(this)
        rv_base_pt2.adapter = adapter
        adapter.onShowTelDialog = {
            this@HFFunYellowPageActivity.kShowTelDialog(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.onShowTelDialog = null
    }

    override fun onResume() {
        super.onResume()
        if (adapter.list.isEmpty()) {
            getFunPartyMembersList()
        }
    }

    private fun getFunPartyMembersList() {
        disposable?.takeIf { it.isDisposed.not() }?.dispose()
        disposable = HFRetrofit.hfService.getFunYellowPage().subscribeResultOkApi({
            it.data?.data?.let {
                Group.convertToGroupList(it)
            }?.let {
                adapter.list.clear()
                adapter.list.addAll(it)
                adapter.notifyDataSetChanged()
            }
        }, {
            HFLogger.log("complete")
            pt2FrameLayout?.complete2()
        })
    }

    override fun onPause() {
        super.onPause()
        disposable?.takeIf { it.isDisposed.not() }?.dispose()
    }

    private class Adapter(val list: MutableList<Group> = mutableListOf()) : RecyclerView.Adapter<HFBaseRecyclerViewHolder<Group>>() {

        var onShowTelDialog: ((mobile: String) -> Unit)? = null

        override fun onBindViewHolder(holder: HFBaseRecyclerViewHolder<Group>?, position: Int) {
            holder?.bindData(list[position])
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): HFBaseRecyclerViewHolder<Group> {
            return when (viewType) {
                Group.TYPE_BODY -> ItemViewHolder(parent!!).also { it.onShowTelDialog = { onShowTelDialog?.invoke(it) } }
                else -> TitleViewHolder(parent!!)
            }
        }

        override fun getItemViewType(position: Int): Int {
            return list[position].type
        }

    }

    private class TitleViewHolder(parent: ViewGroup) : HFBaseParentViewHolder<Group>(parent, R.layout.function_party_members_group_title, false) {
        val tvNode = itemView.kFindViewById<TextView>(R.id.tv_fun_party_members_node)
        override fun onBindData(d: Group) {
            tvNode.text = d.typeStr
        }
    }

    private class ItemViewHolder(parent: ViewGroup) : HFBaseParentViewHolder<Group>(parent, R.layout.function_yellow_page_list_item, true) {

        val tvName = itemView.kFindViewById<TextView>(R.id.tv_fun_yellow_page_name)
        val tvFlag = itemView.kFindViewById<TextView>(R.id.tv_fun_yellow_page_phone)

        var onShowTelDialog: ((mobile: String) -> Unit)? = null

        override fun onBindData(d: Group) {
            tvName.text = d.data.name
            tvFlag.text = d.data.mobilePhone
        }

        override fun onItemClicked(d: Group?) {
            super.onItemClicked(d)
            d?.data?.mobilePhone ?: return
            onShowTelDialog?.invoke(d.data.mobilePhone!!)
        }
    }

    class Group(val typeStr: String?, val data: HFFunYellowPageResponseBody.ListData, val type: Int) {

        companion object {

            const val TYPE_TITLE = 100
            const val TYPE_BODY = 200

            fun convertToGroupList(list: List<HFFunYellowPageResponseBody.ListData>): ArrayList<Group> {
                val sparseArray = ArrayMap<String, MutableList<Group>>()
                list.forEach { e ->
                    if (sparseArray[e.typeStr] == null) {
                        sparseArray.put(e.typeStr, mutableListOf())
                    }
                    sparseArray[e.typeStr]!!.add(Group(e.typeStr, e, TYPE_BODY))
                }
                val groupList = ArrayList<Group>()
                for (i in 0 until sparseArray.keys.size) {
                    val mutableList = sparseArray[sparseArray.keyAt(i)]!!
                    val title = mutableList[0].let { Group(it.typeStr, it.data, TYPE_TITLE) }
                    groupList.add(title)
                    groupList.addAll(mutableList)
                }
                return groupList
            }
        }
    }
}