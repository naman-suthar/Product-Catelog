package app.naman.lumostest

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import dagger.hilt.android.HiltAndroidApp
import okhttp3.Cache
import okhttp3.OkHttpClient

@HiltAndroidApp
class LumosTestApp : Application(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader =
        ImageLoader.Builder(this)
            // In-memory LRU cache — 25% of available app memory
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            // Coil disk cache — stores decoded/transformed bitmaps (100 MB)
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(100L * 1024 * 1024)
                    .build()
            }
            // OkHttp HTTP-level cache — stores raw compressed image bytes (50 MB)
            .callFactory(
                OkHttpClient.Builder()
                    .cache(
                        Cache(
                            directory = cacheDir.resolve("image_http_cache"),
                            maxSize = 50L * 1024 * 1024
                        )
                    )
                    // Override CDN cache-control headers so images survive offline.
                    // DummyJSON / CDN may send restrictive headers (no-cache / no-store);
                    // this interceptor forces a 7-day max-age on every image response.
                    .addNetworkInterceptor { chain ->
                        chain.proceed(chain.request()).newBuilder()
                            .removeHeader("Pragma")
                            .header("Cache-Control", "public, max-age=${7 * 24 * 3600}")
                            .build()
                    }
                    .build()
            )
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .crossfade(true)
            .build()
}
