package com.topie.huaifang.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.TextView
import com.topie.huaifang.R
import com.topie.huaifang.util.HFDimensUtils


/**
 * Created by arman on 2017/9/20.
 */
class HFTipDialog private constructor(context: Context, private val builder: HFTipDialog.Builder) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_tip_dialog)
        setWindowScreenWidth()
        val textView: TextView = findViewById(R.id.tv_tip_dialog_content) as TextView
        textView.text = builder.content
    }

    private fun setWindowScreenWidth() {
        val p = window.attributes
        val display = window.windowManager.defaultDisplay
        p.width = Math.min(display.width, HFDimensUtils.dp2px(300.toFloat()))
        window.attributes = p
    }

    class Builder(val context: Context) {
        var content: String? = null

        fun setContent(content: String): Builder {
            this.content = content
            return this
        }

        fun create(): HFTipDialog {
            return HFTipDialog(context, this)
        }

        fun show(): HFTipDialog {
            val create = create()
            create.show()
            return create
        }
    }

}