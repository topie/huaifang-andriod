package com.topie.huaifang.function.party

import `in`.srain.cube.views.ptr.PtrFrameLayout
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.TextView
import com.davdian.ptr.AbsPt2Handler
import com.davdian.ptr.Pt2FrameLayout
import com.davdian.ptr.ptl.PtlFrameLayout
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseFragment
import com.topie.huaifang.extensions.kGet
import com.topie.huaifang.extensions.kGetOne
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunPartyMemberResponseBody
import com.topie.huaifang.http.subscribeApi
import io.reactivex.disposables.Disposable

/**
 * Created by arman on 2017/9/25.
 * 党员信息公开
 */
class HFFunPartyMembersFragment : HFBaseFragment() {

    private var disposable: Disposable? = null

    private lateinit var adapter: Adapter
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
        adapter = Adapter(inflater.context)
        pt2FrameLayout = inflater.inflate(R.layout.base_pt2_list_layout, container, false) as Pt2FrameLayout
        pt2FrameLayout.setPt2Handler(handler)
        val listView: ExpandableListView = pt2FrameLayout.findViewById(R.id.elv_base_pt2) as ExpandableListView
        listView.setAdapter(adapter)
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
        disposable = HFRetrofit.hfService.getFunPartyMemberList().subscribeApi({
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

    class Adapter(context: Context) : BaseExpandableListAdapter() {

        val list: MutableList<Group> = arrayListOf()
        val inflater: LayoutInflater = LayoutInflater.from(context)

        override fun getGroup(groupPosition: Int): Group? {
            return list.kGet(groupPosition)
        }

        override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
            return true
        }

        override fun hasStableIds(): Boolean {
            return true
        }

        override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: inflater.inflate(R.layout.function_party_members_group_title, parent, false)
            val textView = view.findViewById(R.id.tv_fun_party_members_node) as TextView
            textView.text = getGroup(groupPosition)?.note
            return view
        }

        override fun getChildrenCount(groupPosition: Int): Int {
            return getGroup(groupPosition)?.getCount() ?: 0
        }

        override fun getChild(groupPosition: Int, childPosition: Int): HFFunPartyMemberResponseBody.ListData? {
            return getGroup(groupPosition)?.getItem(childPosition)
        }

        override fun getGroupId(groupPosition: Int): Long {
            return getGroup(groupPosition)?.nodeId?.toLong() ?: (-1).toLong()
        }

        override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: inflater.inflate(R.layout.function_party_members_group_item, parent, false)
            val tvName = view.findViewById(R.id.tv_fun_party_members_name) as TextView
            val tvFlag = view.findViewById(R.id.tv_fun_party_members_flag) as TextView
            tvName.text = getChild(groupPosition, childPosition)?.name
            tvFlag.text = getChild(groupPosition, childPosition)?.flag
            return view
        }

        override fun getChildId(groupPosition: Int, childPosition: Int): Long {
            return getChild(groupPosition, childPosition)?.nodeId?.toLong() ?: (-1).toLong()
        }

        override fun getGroupCount(): Int {
            return list.size
        }


    }

    class Group(var note: String? = null, var nodeId: Int = -1) {

        private val list: MutableList<HFFunPartyMemberResponseBody.ListData> = arrayListOf()

        companion object {
            fun convertToGroupList(list: List<HFFunPartyMemberResponseBody.ListData>): ArrayList<Group> {
                val groupList = ArrayList<Group>()
                list.forEach { item ->
                    groupList.kGetOne(
                            { item.nodeId == it.nodeId },
                            { Group(item.nodeName, item.nodeId) }
                    ).list.add(item)
                }
                return groupList
            }
        }

        fun getCount(): Int {
            return list.size
        }

        fun getItem(position: Int): HFFunPartyMemberResponseBody.ListData? {
            return list.kGet(position)
        }
    }
}