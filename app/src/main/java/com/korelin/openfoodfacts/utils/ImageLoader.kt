package com.korelin.openfoodfacts.utils

import coil.ImageLoader
import coil.decode.ImageDecoderDecoder
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.size.Scale
import coil.transform.RoundedCornersTransformation
import android.content.Context

object ImageLoaderFactory {
    fun create(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.25) // 25% от доступной памяти
                    .build()
            }
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .components {
                add(ImageDecoderDecoder.Factory())
            }
            .crossfade(true)
            .crossfade(300)
            .build()
    }
}