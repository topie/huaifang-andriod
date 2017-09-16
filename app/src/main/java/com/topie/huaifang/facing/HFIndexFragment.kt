package com.topie.huaifang.facing

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.topie.huaifang.R
import com.topie.huaifang.extensions.log
import com.topie.huaifang.imageloader.HFImageView
import me.yokeyword.fragmentation.SupportFragment

/**
 * Created by arman on 2017/9/16.
 */
class HFIndexFragment : SupportFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        log("view is ${view == null}")
        val inflate = inflater.inflate(R.layout.facing_index_fragment, container, false)
        val imageView = inflate.findViewById<HFImageView>(R.id.iv_facing_index_test)
        imageView?.loadImageUri(Uri.parse("https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=3071482608,410654301&fm=173&s=A67B7B8782031EF65719A095030050C3&w=640&h=482&img.JPEG"))
        return inflate
    }
}