@file:Suppress("unused")

package com.topie.huaifang

import android.app.Activity
import android.app.Dialog
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import com.topie.huaifang.extensions.kGetCachePictureDir
import com.topie.huaifang.extensions.kStartActivity
import com.topie.huaifang.extensions.kToastShort
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission
import com.yanzhenjie.permission.PermissionNo
import com.yanzhenjie.permission.PermissionYes
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by arman on 2017/10/1.
 * 获取图片的activity
 */
class HFGetFileActivity : AppCompatActivity() {

    private var mId: Int = 0
    private var mLimit: Int = 0
    private var mTakePicFile: File? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        super.onCreate(savedInstanceState)
        mId = intent.getIntExtra(EXTRA_ID, 0)
        mLimit = intent.getIntExtra(EXTRA_LIMIT, 0)
        AndPermission.with(this)
                .requestCode(100)
                .permission(Permission.STORAGE)
                .rationale { _, rationale ->
                    AndPermission.rationaleDialog(this@HFGetFileActivity, rationale).show()
                }
                .callback(this)
                .start()
    }

    @PermissionNo(100)
    private fun onPermissionNo(deniedPermissions: List<String>) {
        onCancel(mId)
        finish()
    }

    @PermissionYes(100)
    private fun onPermissionYes(grantedPermissions: List<String>) {
        val type = intent.getIntExtra(EXTRA_TYPE, 0)
        when (type) {
            TYPE_GET_FILE -> {
                dispatchGetFileIntent()
            }
            TYPE_TAKE_PIC -> {
                dispatchTakePictureIntent()
            }
            TYPE_GET_IMG -> {
                showSelect()
            }
            else -> {
                onCancel(mId)
                finish()
            }
        }
    }

    private fun showSelect() {
        object : Dialog(this) {
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.image_select_layout)
                findViewById<View>(R.id.tv_image_select_camera).setOnClickListener {
                    dispatchTakePictureIntent()
                    dismiss()
                }
                findViewById<View>(R.id.tv_image_select_pictures).setOnClickListener {
                    dispatchGetImageIntent()
                    dismiss()
                }
            }
        }.show()
    }

    private fun dispatchGetImageIntent() {
        ImageScanActivity.openImageScanActivity(this, REQUEST_GET_IMG, mLimit, null)
    }

    private fun dispatchTakePictureIntent() {
        mTakePicFile = kGetCachePictureDir()?.let {
            val fileName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            File(it, "$fileName.jpg")
        }
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).takeIf {
            it.resolveActivity(packageManager) != null
        }
        if (takePictureIntent != null && mTakePicFile != null) {
            val photoURI = FileProvider.getUriForFile(this, "com.topie.huaifang.fileprovider", mTakePicFile)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } else {
            onCancel(mId)
            finish()
        }
    }

    private fun dispatchGetFileIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "*/*"
            startActivityForResult(intent, REQUEST_GET_FILE)
        } else {
            kToastShort("系统版本过低，不支持该功能")
            onCancel(mId)
            finish()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_GET_FILE -> {
                if (resultCode == Activity.RESULT_OK) {
                    onResult(mId, listOfNotNull(data?.data?.let { Util.getPath(applicationContext, it) }))
                } else {
                    onCancel(mId)
                }
            }
            REQUEST_IMAGE_CAPTURE -> {
                if (resultCode == Activity.RESULT_OK) {
                    onResult(mId, listOfNotNull(mTakePicFile?.absolutePath))
                } else {
                    onCancel(mId)
                }
            }
            REQUEST_GET_IMG -> {
                if (resultCode == Activity.RESULT_OK) {
                    onResult(mId, data?.getStringArrayListExtra(ImageScanActivity.EXTRA_SELECT_LIST) ?: arrayListOf())
                } else {
                    onCancel(mId)
                }
            }
        }
        finish()
    }

    companion object {
        private const val EXTRA_ID = "EXTRA_ID"
        private const val EXTRA_TYPE = "EXTRA_TYPE"
        private const val EXTRA_LIMIT = "EXTRA_LIMIT"
        private const val TYPE_GET_FILE = 1
        private const val TYPE_TAKE_PIC = 2
        private const val TYPE_GET_IMG = 3
        private const val REQUEST_IMAGE_CAPTURE = 100
        private const val REQUEST_GET_FILE = 200
        private const val REQUEST_GET_IMG = 300

        private var listener: Listener? = null
        private var id: Int = 0

        fun takePicture(onResult: ((list: List<String>) -> Unit)) {
            getFile(TYPE_TAKE_PIC, ListenerImpl(onResult))
        }

        fun takePicture(onResult: ((list: List<String>) -> Unit), onCancel: (() -> Unit)) {
            getFile(TYPE_TAKE_PIC, ListenerImpl(onResult, onCancel))
        }

        fun takePicture(listener: Listener) {
            getFile(TYPE_TAKE_PIC, listener)
        }

        fun getFileFromProvider(onResult: ((list: List<String>) -> Unit)) {
            getFile(TYPE_GET_FILE, ListenerImpl(onResult))
        }

        fun getImage(onResult: ((list: List<String>) -> Unit), limit: Int = 1) {
            getFile(TYPE_GET_IMG, ListenerImpl(onResult), limit)
        }

        private fun getFile(type: Int, listener: Listener, limit: Int = 1) {
            this.listener?.onCancel()
            this.listener = listener
            id = (id.shl(1) + 1).takeIf { it != id } ?: 0
            val bundle = Bundle()
            bundle.putInt(EXTRA_ID, id)
            bundle.putInt(EXTRA_TYPE, type)
            bundle.putInt(EXTRA_LIMIT, limit)
            kStartActivity(HFGetFileActivity::class.java, bundle)
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

    private class ListenerImpl(var mOnResult: ((list: List<String>) -> Unit),
                               var mOnCancel: (() -> Unit) = {}) : Listener {


        override fun onResult(list: List<String>) {
            mOnResult(list)
        }

        override fun onCancel() {
            mOnCancel()
        }
    }

    private object Util {
        /**
         * Get a file path from a Uri. This will get the the path for Storage Access
         * Framework Documents, as well as the _data field for the MediaStore and
         * other file-based ContentProviders.
         *
         * @param context The context.
         * @param uri The Uri to query.
         * @author paulburke
         */
        fun getPath(context: Context, uri: Uri): String? {

            // DocumentProvider
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]

                    if ("primary".equals(type, ignoreCase = true)) {
                        return Environment.getExternalStorageDirectory().absolutePath + "/" + split[1]
                    }

                    // TODO handle non-primary volumes
                } else if (isDownloadsDocument(uri)) {

                    val id = DocumentsContract.getDocumentId(uri)
                    val contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)!!)

                    return getDataColumn(context, contentUri, null, null)
                } else if (isMediaDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]

                    var contentUri: Uri? = null
                    when (type) {
                        "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }

                    val selection = "_id=?"
                    val selectionArgs = arrayOf(split[1])

                    return getDataColumn(context, contentUri, selection, selectionArgs)
                }// MediaProvider
                // DownloadsProvider
            } else if ("content".equals(uri.scheme, ignoreCase = true)) {
                return getDataColumn(context, uri, null, null)
            } else if ("file".equals(uri.scheme, ignoreCase = true)) {
                return uri.path
            }// File
            // MediaStore (and general)

            return null
        }

        /**
         * Get the value of the data column for this Uri. This is useful for
         * MediaStore Uris, and other file-based ContentProviders.
         *
         * @param context The context.
         * @param uri The Uri to query.
         * @param selection (Optional) Filter used in the query.
         * @param selectionArgs (Optional) Selection arguments used in the query.
         * @return The value of the _data column, which is typically a file path.
         */
        private fun getDataColumn(context: Context, uri: Uri?, selection: String?,
                                  selectionArgs: Array<String>?): String? {

            var cursor: Cursor? = null
            val column = "_data"
            val projection = arrayOf(column)

            try {
                cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
                if (cursor != null && cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndexOrThrow(column)
                    return cursor.getString(columnIndex)
                }
            } finally {
                if (cursor != null)
                    cursor.close()
            }
            return null
        }


        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is ExternalStorageProvider.
         */
        private fun isExternalStorageDocument(uri: Uri): Boolean {
            return "com.android.externalstorage.documents" == uri.authority
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is DownloadsProvider.
         */
        private fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents" == uri.authority
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is MediaProvider.
         */
        private fun isMediaDocument(uri: Uri): Boolean {
            return "com.android.providers.media.documents" == uri.authority
        }
    }
}