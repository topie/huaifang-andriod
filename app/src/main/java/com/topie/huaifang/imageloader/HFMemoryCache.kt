package com.topie.huaifang.imageloader

import android.graphics.Bitmap
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.resource.bitmap.BitmapResource
import java.security.MessageDigest

/**
 * Created by arman on 2017/9/14.
 */

object HFMemoryCache {
    const val KEY_RES = "com.topie.huaifang.imageloader"
    internal var mHasInit = false
    internal lateinit var mMemoryCache: LruResourceCache
    internal lateinit var mBitmapPool: BitmapPool

    class ResourceKey(val id: Int) : com.bumptech.glide.load.Key {

        override fun updateDiskCacheKey(messageDigest: MessageDigest?) {
            messageDigest?.update((KEY_RES + ",id=" + id).toByte())
        }

        override fun hashCode(): Int {
            return id
        }

        override fun equals(other: Any?): Boolean {
            return other is ResourceKey && other.id == id
        }
    }

    fun put(id: Int, bitmap: Bitmap?) {
        if (mHasInit && bitmap != null) {
            mMemoryCache.put(ResourceKey(id), BitmapResource(bitmap, mBitmapPool))
        }
    }

    fun get(id: Int): Bitmap? {
        return if (mHasInit) {
            mMemoryCache.get(ResourceKey(id))?.get() as? Bitmap
        } else {
            null
        }
    }
}