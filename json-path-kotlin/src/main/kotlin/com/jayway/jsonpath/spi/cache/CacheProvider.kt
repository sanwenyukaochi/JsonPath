package com.jayway.jsonpath.spi.cache
import com.jayway.jsonpath.JsonPathException
import com.jayway.jsonpath.internal.Utils
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater

open class CacheProvider {
    @Volatile private var cache: Cache? = null

    private class CacheHolder {
        companion object {
            @JvmField val CACHE: Cache

            init {
                var cache = instance.cache
                if (cache == null) {
                    cache = getDefaultCache()
                    if (!UPDATER.compareAndSet(instance, null, cache)) cache = instance.cache
                }
                CACHE = checkNotNull(cache) { "Cache initialization did not produce a cache" }
            }
        }
    }

    companion object {
        private val UPDATER = AtomicReferenceFieldUpdater.newUpdater(CacheProvider::class.java, Cache::class.java, "cache")
        private val instance = CacheProvider()

        @JvmStatic fun setCache(cache: Cache) {
            Utils.notNull(cache, "Cache may not be null")
            if (!UPDATER.compareAndSet(instance, null, cache)) {
                throw JsonPathException("Cache provider must be configured before cache is accessed and must not be registered twice.")
            }
        }

        @JvmStatic fun getCache(): Cache = CacheHolder.CACHE

        @JvmStatic private fun getDefaultCache(): Cache = LRUCache(400)
    }
}
