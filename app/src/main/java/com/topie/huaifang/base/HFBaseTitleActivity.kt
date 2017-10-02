package com.topie.huaifang.base

import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout.LayoutParams
import android.widget.TextView
import com.topie.huaifang.R
import com.topie.huaifang.extensions.kClone
import com.topie.huaifang.extensions.kReleaseSelf
import com.topie.huaifang.extensions.kRemoveChildsWithout
import io.reactivex.disposables.Disposable

/**
 * Created by arman on 2017/9/20.
 */
abstract class HFBaseTitleActivity : AppCompatActivity() {
    var mRootView: ViewGroup? = null
        private set

    val pauseDisableList: MutableList<Disposable> = arrayListOf()

    override fun onPause() {
        super.onPause()
        val kClone = pauseDisableList.kClone()
        pauseDisableList.clear()
        kClone.forEach {
            if (!it.isDisposed) {
                it.dispose()
            }
        }
        kClone.clear()
    }

    override fun setContentView(layoutResID: Int) {
        checkRootView()
        val viewGroup = mRootView!!.findViewById(R.id.fl_base_title_root) as ViewGroup
        val inflate = LayoutInflater.from(this)
                .inflate(layoutResID, viewGroup, false)
        setContentView(inflate)
    }

    override fun setContentView(view: View?) {
        checkRootView()
        view?.let {
            val container = mRootView!!.findViewById(R.id.fl_base_title_container)
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
            mRootView = LayoutInflater.from(this)
                    .inflate(
                            R.layout.base_title_layout,
                            findViewById(android.R.id.content) as ViewGroup,
                            false
                    ) as ViewGroup?
            mRootView!!.findViewById(R.id.iv_base_title_back).setOnClickListener {
                onBackPressed()
            }
        }
        mRootView!!.kReleaseSelf()
        mRootView!!.kRemoveChildsWithout(R.id.fl_base_title_container)
        setContentViewSuper(mRootView)
    }

    fun setBaseTitle(id: Int) {
        val textView = mRootView!!.findViewById(R.id.tv_base_title) as TextView
        textView.setText(id)
    }

    fun setBaseTitle(title: String) {
        val textView = mRootView!!.findViewById(R.id.tv_base_title) as TextView
        textView.text = title
    }
}