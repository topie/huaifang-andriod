package com.topie.huaifang.facing

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.topie.huaifang.R
import com.topie.huaifang.account.HFAccountManager
import com.topie.huaifang.base.HFBaseFragment
import com.topie.huaifang.extensions.kStartActivity
import com.topie.huaifang.function.guide.HFFunGuideActivity
import com.topie.huaifang.function.live.HFFunLiveActivity
import com.topie.huaifang.function.notice.HFFunPublicActivity
import com.topie.huaifang.function.party.HFFunPartyActivity
import com.topie.huaifang.login.HFLoginActivity

/**
 * Created by arman on 2017/9/16.
 * app 首页
 */
class HFIndexFragment : HFBaseFragment() {

    override fun onCreateViewSupport(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val inflate = inflater.inflate(R.layout.facing_index_fragment, container, false)
        initFunctions(inflate, savedInstanceState)
        return inflate
    }

    private fun initFunctions(view: View, savedInstanceState: Bundle?) {
        val fun0 = view.findViewById(R.id.ll_facing_fun_0)
        (fun0.findViewById(R.id.iv_facing_index_fun) as ImageView).setImageResource(R.mipmap.ic_facing_index_fun_announcement)
        (fun0.findViewById(R.id.tv_facing_index_fun) as TextView).setText(R.string.facing_index_fun_announcement)
        val fun1 = view.findViewById(R.id.ll_facing_fun_1)
        (fun1.findViewById(R.id.iv_facing_index_fun) as ImageView).setImageResource(R.mipmap.ic_facing_index_fun_guide)
        (fun1.findViewById(R.id.tv_facing_index_fun) as TextView).setText(R.string.facing_index_fun_guide)
        val fun2 = view.findViewById(R.id.ll_facing_fun_2)
        (fun2.findViewById(R.id.iv_facing_index_fun) as ImageView).setImageResource(R.mipmap.ic_facing_index_fun_bazaar)
        (fun2.findViewById(R.id.tv_facing_index_fun) as TextView).setText(R.string.facing_index_fun_bazaar)
        val fun3 = view.findViewById(R.id.ll_facing_fun_3)
        (fun3.findViewById(R.id.iv_facing_index_fun) as ImageView).setImageResource(R.mipmap.ic_facing_index_fun_live)
        (fun3.findViewById(R.id.tv_facing_index_fun) as TextView).setText(R.string.facing_index_fun_live)
        val fun4 = view.findViewById(R.id.ll_facing_fun_4)
        (fun4.findViewById(R.id.iv_facing_index_fun) as ImageView).setImageResource(R.mipmap.ic_facing_index_fun_party)
        (fun4.findViewById(R.id.tv_facing_index_fun) as TextView).setText(R.string.facing_index_fun_party)
        val fun5 = view.findViewById(R.id.ll_facing_fun_5)
        (fun5.findViewById(R.id.iv_facing_index_fun) as ImageView).setImageResource(R.mipmap.ic_facing_index_fun_yellow_book)
        (fun5.findViewById(R.id.tv_facing_index_fun) as TextView).setText(R.string.facing_index_fun_yellow_book)

        fun0.setOnClickListener {
            context?.let {
                if (HFAccountManager.isLogin) {
                    startActivity(Intent(it, HFFunPublicActivity::class.java))
                } else {
                    val intent = Intent(it, HFLoginActivity::class.java)
                    intent.putExtra(HFLoginActivity.EXTRA_IS_REGISTER, true)
                    startActivity(intent)
                }
            }
        }

        fun1.setOnClickListener {
            context?.let {
                if (HFAccountManager.isLogin) {
                    val intent = Intent(it, HFFunGuideActivity::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(it, HFLoginActivity::class.java)
                    intent.putExtra(HFLoginActivity.EXTRA_IS_REGISTER, false)
                    startActivity(intent)
                }
            }
        }

        fun3.setOnClickListener {
            context.kStartActivity(HFFunLiveActivity::class.java)
        }

        fun4.setOnClickListener {
            context.kStartActivity(HFFunPartyActivity::class.java)
        }

        fun5.setOnClickListener {

        }

    }
}