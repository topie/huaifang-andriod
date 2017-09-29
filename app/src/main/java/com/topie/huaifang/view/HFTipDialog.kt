package com.topie.huaifang.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.topie.huaifang.R
import com.topie.huaifang.util.HFDimensUtils
import kotlinx.android.synthetic.main.view_tip_dialog.*


/**
 * Created by arman on 2017/9/20.
 */
class HFTipDialog private constructor(context: Context, private val builder: HFTipDialog.Builder) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_tip_dialog)
        setWindowScreenWidth()
        tv_tip_dialog_content.text = builder.content
        tv_tip_dialog_cancel.setOnClickListener { builder.onCancelClicked?.invoke() }
        tv_tip_dialog_ok.setOnClickListener { builder.onOkClicked?.invoke() }
    }

    private fun setWindowScreenWidth() {
        val p = window.attributes
        val display = window.windowManager.defaultDisplay
        p.width = Math.min(display.width, HFDimensUtils.dp2px(300.toFloat()))
        window.attributes = p
    }

    class Builder() {
        var content: String? = null
        var onOkClicked: (() -> Unit)? = null
        var onCancelClicked: (() -> Unit)? = null

        fun create(context: Context): HFTipDialog {
            return HFTipDialog(context, this)
        }

        fun show(context: Context): HFTipDialog {
            val create = create(context)
            create.show()
            return create
        }
    }

}