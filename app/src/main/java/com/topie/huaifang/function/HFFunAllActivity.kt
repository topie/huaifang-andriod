package com.topie.huaifang.function

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.topie.huaifang.R
import com.topie.huaifang.base.HFBaseTitleActivity
import com.topie.huaifang.extensions.kStartActivity
import com.topie.huaifang.function.dispute.HFFunDisputeMediatorActivity
import com.topie.huaifang.function.guide.HFFunGuideActivity
import com.topie.huaifang.function.library.HFFunLibraryActivity
import com.topie.huaifang.function.live.HFFunLiveActivity
import com.topie.huaifang.function.live.HFFunLiveBazaarActivity
import com.topie.huaifang.function.live.HFFunLiveRepairsListActivity
import com.topie.huaifang.function.notice.HFFunPublicActivity
import com.topie.huaifang.function.party.HFFunPartyActivity
import com.topie.huaifang.function.village.HFFunVillageActivity
import com.topie.huaifang.function.yellowpage.HFFunYellowPageActivity
import kotlinx.android.synthetic.main.function_all_activity.*

/**
 * Created by arman on 2017/10/2.
 *  全部应用
 */
class HFFunAllActivity : HFBaseTitleActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.function_all_activity)
        setBaseTitle("全部应用")

        initTitle(ll_fun_all_common, R.mipmap.ic_fun_all_common, R.string.facing_index_functions)
        initTitle(ll_fun_all_party, R.mipmap.ic_fun_all_party, R.string.facing_index_fun_party)
        initTitle(ll_fun_all_service, R.mipmap.ic_fun_all_service, "特色服务")

        //常用功能
        initFun(ll_fun_all_common_0, R.mipmap.ic_facing_index_fun_announcement, R.string.facing_index_fun_announcement, {
            this@HFFunAllActivity.kStartActivity(HFFunPublicActivity::class.java)
        })
        initFun(ll_fun_all_common_1, R.mipmap.ic_facing_index_fun_guide, R.string.facing_index_fun_guide, {
            this@HFFunAllActivity.kStartActivity(HFFunGuideActivity::class.java)
        })
        initFun(ll_fun_all_common_2, R.mipmap.ic_facing_index_fun_bazaar, R.string.facing_index_fun_bazaar, {
            this@HFFunAllActivity.kStartActivity(HFFunLiveBazaarActivity::class.java)
        })
        initFun(ll_fun_all_common_3, R.mipmap.ic_facing_index_fun_live, R.string.facing_index_fun_live, {
            this@HFFunAllActivity.kStartActivity(HFFunLiveActivity::class.java)
        })
        initFun(ll_fun_all_common_4, R.mipmap.ic_facing_index_fun_yellow_book, R.string.facing_index_fun_yellow_book, {
            this@HFFunAllActivity.kStartActivity(HFFunYellowPageActivity::class.java)
        })
        initFun(ll_fun_all_common_5, R.mipmap.ic_facing_index_fun_live, R.string.facing_index_fun_village, {
            this@HFFunAllActivity.kStartActivity(HFFunVillageActivity::class.java)
        })
        //社区党建
        initFun(ll_fun_all_party_0, R.mipmap.ic_fun_all_party_pub, "党务公开", {
            val bundle = Bundle()
            bundle.putInt(HFFunPartyActivity.EXTRA_POSITION, HFFunPartyActivity.POSITION_PUB)
            this@HFFunAllActivity.kStartActivity(HFFunPartyActivity::class.java, bundle)
        })
        initFun(ll_fun_all_party_1, R.mipmap.ic_fun_all_party_act, "党支部活动", {
            val bundle = Bundle()
            bundle.putInt(HFFunPartyActivity.EXTRA_POSITION, HFFunPartyActivity.POSITION_ZHIBU)
            this@HFFunAllActivity.kStartActivity(HFFunPartyActivity::class.java, bundle)
        })
        initFun(ll_fun_all_party_2, R.mipmap.ic_fun_all_party_members, "党员信息", {
            val bundle = Bundle()
            bundle.putInt(HFFunPartyActivity.EXTRA_POSITION, HFFunPartyActivity.POSITION_MEMBERS)
            this@HFFunAllActivity.kStartActivity(HFFunPartyActivity::class.java, bundle)
        })

        //特色服务
        initFun(ll_fun_all_service_0, R.mipmap.ic_fun_all_service_dispute, "纠纷调解", {
            this@HFFunAllActivity.kStartActivity(HFFunDisputeMediatorActivity::class.java)
        })
        initFun(ll_fun_all_service_1, R.mipmap.ic_fun_all_service_library, "社区图书馆", {
            this@HFFunAllActivity.kStartActivity(HFFunLibraryActivity::class.java)
        })

        initFun(ll_fun_all_service_2, R.mipmap.ic_fun_all_service_repair, "物业报修", {
            this@HFFunAllActivity.kStartActivity(HFFunLiveRepairsListActivity::class.java)
        })


    }

    private fun initFun(v: View, id: Int, strId: Int, onClick: ((v: View) -> Unit)) {
        v.setOnClickListener(onClick)
        v.findViewById<TextView>(R.id.tv_facing_index_fun).setText(strId)
        v.findViewById<ImageView>(R.id.iv_facing_index_fun).setImageResource(id)
    }

    private fun initFun(v: View, id: Int, str: String, onClick: ((v: View) -> Unit)) {
        v.setOnClickListener(onClick)
        v.findViewById<TextView>(R.id.tv_facing_index_fun).text = str
        v.findViewById<ImageView>(R.id.iv_facing_index_fun).setImageResource(id)
    }

    private fun initTitle(v: View, id: Int, strId: Int) {
        v.findViewById<TextView>(R.id.tv_fun_all_title).setText(strId)
        v.findViewById<ImageView>(R.id.iv_fun_all_title).setImageResource(id)
    }

    private fun initTitle(v: View, id: Int, str: String) {
        v.findViewById<TextView>(R.id.tv_fun_all_title).text = str
        v.findViewById<ImageView>(R.id.iv_fun_all_title).setImageResource(id)
    }
}