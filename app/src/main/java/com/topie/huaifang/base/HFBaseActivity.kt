package com.topie.huaifang.base

import android.support.v7.app.AppCompatActivity
import com.topie.huaifang.extensions.kClone
import io.reactivex.disposables.Disposable

/**
 * Created by arman on 2017/10/2.
 *
 */
abstract class HFBaseActivity : AppCompatActivity() {

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
}