package com.jayway.jsonpath.spi.cache
import com.jayway.jsonpath.JsonPath
import java.util.Deque
import java.util.LinkedList
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock

open class LRUCache(
    private val limit: Int,
) : Cache {
    private val lock = ReentrantLock()
    private val map: MutableMap<String, JsonPath> = ConcurrentHashMap()
    private val queue: Deque<String> = LinkedList()

    override fun put(
        key: String,
        value: JsonPath,
    ) {
        val oldValue = map.put(key, value)
        if (oldValue != null) removeThenAddKey(key) else addKey(key)
        if (map.size > limit) map.remove(removeLast())
    }

    override fun get(key: String): JsonPath? {
        val jsonPath = map[key]
        if (jsonPath != null) removeThenAddKey(key)
        return jsonPath
    }

    private fun addKey(key: String) {
        lock.lock()
        try {
            queue.addFirst(key)
        } finally {
            lock.unlock()
        }
    }

    private fun removeLast(): String {
        lock.lock()
        try {
            return queue.removeLast()
        } finally {
            lock.unlock()
        }
    }

    private fun removeThenAddKey(key: String) {
        lock.lock()
        try {
            queue.removeFirstOccurrence(key)
            queue.addFirst(key)
        } finally {
            lock.unlock()
        }
    }

    private fun removeFirstOccurrence(key: String) {
        lock.lock()
        try {
            queue.removeFirstOccurrence(key)
        } finally {
            lock.unlock()
        }
    }

    open fun getSilent(key: String): JsonPath? = map[key]

    open fun remove(key: String) {
        removeFirstOccurrence(key)
        map.remove(key)
    }

    open fun size(): Int = map.size

    override fun toString(): String = map.toString()
}
