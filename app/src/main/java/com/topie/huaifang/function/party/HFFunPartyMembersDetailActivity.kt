package com.topie.huaifang.function.party

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.kIsNotNull
import com.topie.huaifang.extensions.kToastShort
import com.topie.huaifang.http.HFRetrofit
import com.topie.huaifang.http.bean.function.HFFunPartyMemberResponseBody
import com.topie.huaifang.http.subscribeResultOkApi
import kotlinx.android.synthetic.main.function_party_members_detail_activity.*

/**
 * Created by arman on 2017/10/6.
 * 党员信息详情
 */
class HFFunPartyMembersDetailActivity : HFBaseTitleActivity() {

    private var mData: HFFunPartyMemberResponseBody.ListData? = null

    companion object {
        const val EXTRA_DATA = "extra_data"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mData = intent.getSerializableExtra(EXTRA_DATA) as HFFunPartyMemberResponseBody.ListData?
        setContentView(R.layout.function_party_members_detail_activity)
        tv_fun_party_members_detail_name.text = mData?.name
        tv_fun_party_members_detail_node.text = mData?.nodeName
        tv_fun_party_members_detail_flag.text = mData?.flag
        tv_fun_party_members_detail_time.text = mData?.enterDate
        val inflater = LayoutInflater.from(this)
        mData?.tag?.takeIf { it.kIsNotNull() }?.split(",")?.forEachIndexed { index, tag ->
            val inflate = inflater.inflate(R.layout.function_party_members_detail_tag_item, ll_fun_party_members_detail_tag)
            val tvTagPre = inflate.findViewById<View>(R.id.tv_fun_party_members_detail_tag_pre)
            val tvTag = inflate.findViewById<TextView>(R.id.tv_fun_party_members_detail_tag)
            tvTagPre.visibility = when (index) {
                0 -> View.VISIBLE
                else -> View.INVISIBLE
            }
            tvTag.text = tag
            inflater.inflate(R.layout.function_line_item, ll_fun_party_members_detail_tag)
        }

        tv_fun_party_members_detail_friend.setOnClickListener {
            HFRetrofit.hfService.addCommFriend(mData?.id ?: -1).subscribeResultOkApi {
                kToastShort("添加好友成功")
                finish()
            }
        }

        tv_fun_party_members_detail_msg.setOnClickListener {
            //TODO 发送消息
        }
    }
}