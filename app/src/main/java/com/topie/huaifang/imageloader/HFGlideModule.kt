package com.topie.huaifang.imageloader

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPoolAdapter
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.target.ViewTarget
import com.topie.huaifang.R

/**
 * Created by arman on 2017/9/14.
 */
@GlideModule
class HFGlideModule : AppGlideModule() {
    override fun registerComponents(context: Context?, glide: Glide?, registry: Registry?) {
        super.registerComponents(context, glide, registry)
    }

    override fun applyOptions(context: Context?, builder: GlideBuilder?) {
        super.applyOptions(context, builder)
        val memorySizeCalculator = MemorySizeCalculator.Builder(context).build()
        HFMemoryCache.mMemoryCache = LruResourceCache(memorySizeCalculator.memoryCacheSize)
        val size = memorySizeCalculator.bitmapPoolSize
        if (size > 0) {
            HFMemoryCache.mBitmapPool = LruBitmapPool(size)
        } else {
            HFMemoryCache.mBitmapPool = BitmapPoolAdapter()
        }
        builder?.setMemorySizeCalculator(memorySizeCalculator)?.setMemoryCache(HFMemoryCache.mMemoryCache)
        HFMemoryCache.mHasInit = true
        ViewTarget.setTagId(R.id.id_image_tag)
    }

    override fun isManifestParsingEnabled(): Boolean {
        return super.isManifestParsingEnabled()
    }
}