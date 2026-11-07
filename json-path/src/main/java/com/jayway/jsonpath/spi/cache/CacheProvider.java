package com.jayway.jsonpath.spi.cache;

import static com.jayway.jsonpath.internal.Utils.notNull;

import com.jayway.jsonpath.JsonPathException;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class CacheProvider {

    private static final AtomicReferenceFieldUpdater<CacheProvider, Cache> UPDATER =
            AtomicReferenceFieldUpdater.newUpdater(CacheProvider.class, Cache.class, "cache");
    private static final CacheProvider instance = new CacheProvider();

    private volatile Cache cache;

    private static class CacheHolder {
        static final Cache CACHE;

        static {
            Cache cache = CacheProvider.instance.cache;

            if (cache == null) {
                cache = getDefaultCache();

                if (!UPDATER.compareAndSet(instance, null, cache)) {
                    cache = CacheProvider.instance.cache;
                }
            }
            CACHE = cache;
        }
    }

    public static void setCache(Cache cache) {
        notNull(cache, "Cache may not be null");
        if (!UPDATER.compareAndSet(instance, null, cache)) {
            throw new JsonPathException(
                    "Cache provider must be configured before cache is accessed and must not be registered twice.");
        }
    }

    public static Cache getCache() {
        return CacheHolder.CACHE;
    }

    private static Cache getDefaultCache() {
        return new LRUCache(400);
    }
}
