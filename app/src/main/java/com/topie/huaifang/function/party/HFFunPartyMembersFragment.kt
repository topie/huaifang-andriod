package com.topie.huaifang.function.party

import `in`.srain.cube.views.ptr.PtrFrameLayout
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.davdian.ptr.AbsPt2Handler
import com.davdian.ptr.Pt2FrameLayout
import com.davdian.ptr.ptl.PtlFrameLayout
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseFragment
import com.topie.huaifang.base.HFBaseParentViewHolder
import com.topie.huaifang.base.HFBaseRecyclerViewHolder
import com.topie.huaifang.extensions.kFindViewById
import com.topie.huaifang.extensions.kStartActivity
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunPartyMemberResponseBody
import com.topie.huaifang.http.subscribeResultOkApi
import io.reactivex.disposables.Disposable

/**
 * Created by arman on 2017/9/25.
 * 党员信息公开
 */
class HFFunPartyMembersFragment : HFBaseFragment() {

    private var disposable: Disposable? = null

    private val adapter = Adapter()
    private lateinit var pt2FrameLayout: Pt2FrameLayout

    private val handler: AbsPt2Handler = object : AbsPt2Handler() {

        override fun checkCanDoLoad(frame: PtlFrameLayout?, content: View?, footer: View?): Boolean {
            return false
        }

        override fun onLoadMoreBegin(frame: PtlFrameLayout?) {

        }

        override fun onRefreshBegin(frame: PtrFrameLayout?) {
            getFunPartyMembersList()
        }
    }

    override fun onCreateViewSupport(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        pt2FrameLayout = inflater.inflate(R.layout.base_pt2_recycler_layout, container, false) as Pt2FrameLayout
        pt2FrameLayout.setPt2Handler(handler)
        val recyclerView: RecyclerView = pt2FrameLayout.kFindViewById(R.id.rv_base_pt2)
        recyclerView.layoutManager = LinearLayoutManager(inflater.context)
        recyclerView.adapter = adapter
        return pt2FrameLayout
    }

    override fun onResume() {
        super.onResume()
        if (adapter.list.isEmpty()) {
            getFunPartyMembersList()
        }
    }

    private fun getFunPartyMembersList() {
        disposable?.takeIf { it.isDisposed.not() }?.dispose()
        disposable = HFRetrofit.hfService.getFunPartyMemberList().subscribeResultOkApi({
            it.data?.data?.takeIf { it.isNotEmpty() }?.let {
                Group.convertToGroupList(it)
            }?.let {
                adapter.list.clear()
                adapter.list.addAll(it)
                adapter.notifyDataSetChanged()
            }
        }, {
            pt2FrameLayout.complete2()
        })
    }

    override fun onPause() {
        super.onPause()
        disposable?.takeIf { it.isDisposed.not() }?.dispose()
    }

    private class Adapter(val list: MutableList<Group> = mutableListOf()) : RecyclerView.Adapter<HFBaseRecyclerViewHolder<Group>>() {

        override fun onBindViewHolder(holder: HFBaseRecyclerViewHolder<Group>?, position: Int) {
            holder?.bindData(list[position])
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): HFBaseRecyclerViewHolder<Group> {
            return when (viewType) {
                Group.TYPE_BODY -> ItemViewHolder(parent!!)
                else -> TitleViewHolder(parent!!)
            }
        }

        override fun getItemViewType(position: Int): Int {
            return list[position].type
        }

    }

    private class ItemViewHolder(parent: ViewGroup) : HFBaseParentViewHolder<Group>(parent, R.layout.function_party_members_group_item, true) {
        val tvName = itemView.kFindViewById<TextView>(R.id.tv_fun_party_members_name)
        val tvFlag = itemView.kFindViewById<TextView>(R.id.tv_fun_party_members_flag)
        override fun onBindData(d: Group) {
            tvName.text = d.data.name
            tvFlag.text = d.data.flag
        }
    }

    private class TitleViewHolder(parent: ViewGroup) : HFBaseParentViewHolder<Group>(parent, R.layout.function_party_members_group_title, false) {
        val tvNode = itemView.kFindViewById<TextView>(R.id.tv_fun_party_members_node)
        override fun onBindData(d: Group) {
            tvNode.text = d.note
        }

        override fun onItemClicked(d: Group?) {
            super.onItemClicked(d)
            d ?: return
            val bundle = Bundle()
            bundle.putSerializable(HFFunPartyMembersDetailActivity.EXTRA_DATA, d.data)
            itemView.kStartActivity(HFFunPartyMembersDetailActivity::class.java, bundle)
        }
    }

    private class Group(val note: String? = null, val nodeId: Int = -1, val data: HFFunPartyMemberResponseBody.ListData, val type: Int) {

        companion object {

            const val TYPE_TITLE = 100
            const val TYPE_BODY = 200

            fun convertToGroupList(list: List<HFFunPartyMemberResponseBody.ListData>): ArrayList<Group> {
                val sparseArray = SparseArray<MutableList<Group>>()
                list.forEach { e ->
                    if (sparseArray[e.nodeId] == null) {
                        sparseArray.put(e.nodeId, mutableListOf())
                    }
                    sparseArray[e.nodeId].add(Group(e.partyNodeName, e.nodeId, e, TYPE_BODY))
                }
                val groupList = ArrayList<Group>()
                for (i in 0 until sparseArray.size()) {
                    val mutableList = sparseArray[sparseArray.keyAt(i)]
                    val title = mutableList[0].let { Group(it.note, it.nodeId, it.data, TYPE_TITLE) }
                    groupList.add(title)
                    groupList.addAll(mutableList)
                }
                return groupList
            }
        }
    }
}