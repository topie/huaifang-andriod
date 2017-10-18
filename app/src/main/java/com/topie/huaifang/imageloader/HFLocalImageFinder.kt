package com.topie.huaifang.imageloader

import android.content.Context
import android.provider.MediaStore.Images.Media
import android.util.Log
import io.reactivex.Observable

class LocalImageSet(val all: MutableList<String> = mutableListOf(), val tree: MutableMap<String, MutableList<String>> = mutableMapOf())

val projection = arrayOf(Media.BUCKET_DISPLAY_NAME, Media.DATA)


fun getLocalImageSet(context: Context): Observable<LocalImageSet> {
    return Observable.create<LocalImageSet> {
        val cursor = context.contentResolver.query(Media.EXTERNAL_CONTENT_URI, projection, null, null, Media.DATE_ADDED)
        if (cursor == null || !cursor.moveToLast()) {
            it.onNext(null)
            it.onComplete()
            cursor?.close()
            Log.i("getLocalImageSet", "cursor is empty")
            return@create
        }
        val imageSet = LocalImageSet(ArrayList(cursor.count))
        do {
            if (Thread.interrupted()) {
                Log.i("getLocalImageSet", "thread interrupted")
                return@create
            }
            val display = cursor.getString(cursor.getColumnIndex(projection[0]))
            val path = cursor.getString(cursor.getColumnIndex(projection[1]))
            Log.i("getLocalImageSet", "display = [$display],path = [$path]")
            imageSet.all.add(path)
            var list = imageSet.tree[display]
            if (list == null) {
                list = ArrayList()
                imageSet.tree.put(display, list)
            }
            list.add(path)
        } while (cursor.moveToPrevious())
        it.onNext(imageSet)
        it.onComplete()
        cursor.close()
    }
}