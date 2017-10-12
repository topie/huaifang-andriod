package com.topie.huaifang.function.identity

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseRecyclerViewHolder
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.kInflate
import com.topie.huaifang.extensions.kToastShort
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunIdentityEditRequestBody
import com.topie.huaifang.http.bean.function.HFFunIdentityNoteResponseBody
import com.topie.huaifang.http.subscribeResultOkApi
import com.topie.huaifang.util.HFDimensUtils
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.function_identity_note_dialog.*
import kotlinx.android.synthetic.main.function_indentity_edit_activity.*

/**
 * Created by arman on 2017/10/11.
 * 我的身份信息
 */
class HFFunIdentityEditActivity : HFBaseTitleActivity() {


    private val mNoteList: SparseArray<Pair<Int, String>?> = SparseArray()

    private val mNoteOnClick: ((v: View) -> Unit) = {

        val index = when (it.id) {
            R.id.ll_fun_identity_xq -> 0
            R.id.ll_fun_identity_lh -> 1
            R.id.ll_fun_identity_dy -> 2
            R.id.ll_fun_identity_lc -> 3
            R.id.ll_fun_identity_mp -> 4
            else -> -1
        }
        when (index) {
            -1 -> kToastShort("unknown index")
            else -> {
                val node = when (index) {
                    0 -> 0.to("")
                    else -> mNoteList[index - 1]
                }
                if (node == null) {
                    kToastShort("")
                } else {
                    toShowNodeList(index, node)
                }
            }
        }
    }

    private var showNoteDis: Disposable? = null

    private fun toShowNodeList(index: Int, parent: Pair<Int, String>) {
        showNoteDis?.takeIf { !it.isDisposed }?.dispose()
        val onNext: (HFFunIdentityNoteResponseBody) -> Unit = {
            it.data?.data?.takeIf {
                it.isNotEmpty().also { isNotEmpty ->
                    if (!isNotEmpty) {
                        kToastShort("可选项为空")
                    }
                }
            }?.map {
                it.id.to(it.name ?: it.roomNumber ?: it.id.toString())
            }?.takeIf {
                //activity没有关闭
                !isFinishing
            }?.also {
                NoteDialog(it, this@HFFunIdentityEditActivity).also {
                    it.mOnItemClick = {
                        for (i in index..4) {
                            mNoteList.put(i, null)
                        }
                        mNoteList.put(index, it)
                        initRoomNode()
                    }
                }.show()
            }
        }
        showNoteDis = when (index) {
            4 -> HFRetrofit.hfService.getFunIdentityRoom(parent.first).subscribeResultOkApi(onNext)
            else -> HFRetrofit.hfService.getFunIdentityNote(parent.first).subscribeResultOkApi(onNext)
        }
    }

    private fun initRoomNode() {
        tv_fun_identity_xq?.text = mNoteList[0]?.second
        tv_fun_identity_lh?.text = mNoteList[1]?.second
        tv_fun_identity_dy?.text = mNoteList[2]?.second
        tv_fun_identity_lc?.text = mNoteList[3]?.second
        tv_fun_identity_mp?.text = mNoteList[4]?.second
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.function_indentity_edit_activity)
        setBaseTitle("编辑个人信息")
        ll_fun_identity_xq.setOnClickListener(mNoteOnClick)
        ll_fun_identity_lh.setOnClickListener(mNoteOnClick)
        ll_fun_identity_dy.setOnClickListener(mNoteOnClick)
        ll_fun_identity_lc.setOnClickListener(mNoteOnClick)
        ll_fun_identity_mp.setOnClickListener(mNoteOnClick)
        tv_fun_identity_submit.setOnClickListener {
            try {
                HFRetrofit.hfService.postFunIdentity(getEditBody()).subscribeResultOkApi {
                    kToastShort("认证成功")
                    if (!isFinishing) {
                        finish()
                    }
                }
            } catch (ex: Exception) {
                ex.message?.kToastShort()
            }
        }
    }

    @Throws(IllegalStateException::class)
    private fun getEditBody(): HFFunIdentityEditRequestBody {
        return HFFunIdentityEditRequestBody().also {
            it.pName = et_fun_identity_name.text.toString().trim().takeIf { it.isNotEmpty() } ?:
                    throw IllegalStateException("请填写姓名")
            it.pPersonType = when {
                rb_fun_identity_sf_zhu.isChecked -> "住户"
                else -> "租户"
            }
            it.pIdentifyNumber = et_fun_identity_number.text.toString().trim().takeIf { it.isNotEmpty() } ?:
                    throw IllegalStateException("请填写身份证号")
            it.houseId = mNoteList[4]?.first?.toString() ?:
                    throw IllegalStateException("请完善房间信息")
        }
    }

    class NoteDialog(val list: List<Pair<Int, String>>, context: Context) : Dialog(context) {

        var mOnItemClick: ((pair: Pair<Int, String>) -> Unit)? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.function_identity_note_dialog)
            setWindowScreenWidth()
            rv_fun_identity_notes.layoutManager = LinearLayoutManager(context)
            rv_fun_identity_notes.adapter = Adapter(list).also {
                it.mOnItemClick = {
                    mOnItemClick?.invoke(it)
                    dismiss()
                }
            }
        }

        private fun setWindowScreenWidth() {
            val p = window.attributes
            val display = window.windowManager.defaultDisplay
            p.width = Math.min(display.width, HFDimensUtils.dp2px(300.toFloat()))
            window.attributes = p
        }

        private class Adapter(val list: List<Pair<Int, String>>) : RecyclerView.Adapter<ViewHolder>() {

            var mOnItemClick: ((pair: Pair<Int, String>) -> Unit)? = null

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val kInflate = parent.kInflate(R.layout.function_identity_note_item)
                return ViewHolder(kInflate).also { it.mOnItemClick = mOnItemClick }
            }

            override fun getItemCount(): Int {
                return list.size
            }

            override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
                holder?.bindData(list[position])
            }

        }

        private class ViewHolder(itemView: View) : HFBaseRecyclerViewHolder<Pair<Int, String>>(itemView, true) {

            val tvText: TextView = itemView.findViewById(R.id.tv_fun_identity_note_text)
            var mOnItemClick: ((pair: Pair<Int, String>) -> Unit)? = null

            override fun onBindData(d: Pair<Int, String>) {
                tvText.text = d.second
            }

            override fun onItemClicked(d: Pair<Int, String>?) {
                super.onItemClicked(d)
                d ?: return
                mOnItemClick?.invoke(d)
            }
        }
    }
}