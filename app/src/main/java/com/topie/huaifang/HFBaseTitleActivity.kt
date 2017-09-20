package com.topie.huaifang

import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout.LayoutParams
import android.widget.TextView
import com.topie.huaifang.extensions.kReleaseSelf
import com.topie.huaifang.extensions.kRemoveChildsWithout

/**
 * Created by arman on 2017/9/20.
 */
abstract class HFBaseTitleActivity : AppCompatActivity() {
    var mRootView: ViewGroup? = null
        private set

    override fun setContentView(layoutResID: Int) {
        checkRootView()
        val inflate = LayoutInflater.from(this)
                .inflate(layoutResID, mRootView!!.findViewById(R.id.fl_base_title_root), false)
        setContentView(inflate)
    }

    override fun setContentView(view: View?) {
        checkRootView()
        view?.let {
            val container = mRootView!!.findViewById<View>(R.id.fl_base_title_container)
            val height = container.layoutParams.height.let { Math.max(0, it) }
            val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            lp.topMargin = height
            mRootView!!.addView(it, lp)
        }
    }

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        checkRootView()
        view?.let {
            mRootView!!.addView(it, params)
        }
    }

    @Suppress("MemberVisibilityCanPrivate")
    fun setContentViewSuper(view: View?) {
        super.setContentView(view)
    }

    private fun checkRootView() {
        if (mRootView == null) {
            mRootView = mRootView ?: LayoutInflater.from(this)
                    .inflate(
                            R.layout.base_title_layout,
                            findViewById(android.R.id.content),
                            false
                    ) as ViewGroup?
        }
        mRootView!!.kReleaseSelf()
        mRootView!!.kRemoveChildsWithout(R.id.fl_base_title_container)
        setContentViewSuper(mRootView)
    }

    fun setBaseTitle(id: Int) {
        val textView = mRootView!!.findViewById<TextView>(R.id.tv_base_title)
        textView.setText(id)
    }

    fun setBaseTitle(title: String) {
        val textView = mRootView!!.findViewById<TextView>(R.id.tv_base_title)
        textView.text = title
    }
}