package com.topie.huaifang

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.topie.huaifang.extensions.kGetCachePictureDir
import com.topie.huaifang.extensions.kStartActivity
import com.topie.huaifang.extensions.log
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by arman on 2017/10/1.
 * 获取图片的activity
 */
class HFGetPictureActivity : AppCompatActivity() {

    private var id: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        super.onCreate(savedInstanceState)
        id = intent.getIntExtra(EXTRA_ID, 0)
        dispatchTakePictureIntent()
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).takeIf {
            it.resolveActivity(packageManager) != null
        }
        val file = kGetCachePictureDir()?.let {
            val fileName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            File(it, "$fileName.jpg")
        }
        if (takePictureIntent != null && file != null) {
            val photoURI = FileProvider.getUriForFile(this, "com.topie.huaifang.fileprovider", file)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } else {
            onCancel(id)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        log("requestCode={$requestCode},resultCode={$resultCode}")
    }

    companion object {
        private const val EXTRA_ID = "EXTRA_ID"
        private const val REQUEST_IMAGE_CAPTURE = 100

        private var listener: Listener? = null
        private var id: Int = 0

        fun takePicture(listener: Listener) {
            this.listener?.onCancel()
            this.listener = listener
            id = (id.shl(1) + 1).takeIf { it != id } ?: 0
            Bundle().also {
                it.putInt(EXTRA_ID, id)
                kStartActivity(HFGetPictureActivity::class.java, it)
            }
        }

        fun onResult(id: Int, list: List<String>) {
            if (id != this.id) {
                return
            }
            listener?.onResult(list)
            listener = null
        }

        fun onCancel(id: Int) {
            if (id != this.id) {
                return
            }
            listener?.onCancel()
            listener = null
        }
    }

    interface Listener {
        fun onResult(list: List<String>)
        fun onCancel()
    }
}