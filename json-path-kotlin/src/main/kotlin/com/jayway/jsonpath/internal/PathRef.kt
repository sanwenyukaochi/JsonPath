package com.jayway.jsonpath.internal

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.InvalidModificationException
import com.jayway.jsonpath.MapFunction
import com.jayway.jsonpath.PathNotFoundException
import com.jayway.jsonpath.spi.json.JsonProvider

abstract class PathRef private constructor(
    @JvmField protected var parent: Any?,
) : Comparable<PathRef> {
    abstract fun getAccessor(): Any?

    abstract fun set(
        newVal: Any?,
        configuration: Configuration,
    )

    abstract fun convert(
        mapFunction: MapFunction,
        configuration: Configuration,
    )

    abstract fun delete(configuration: Configuration)

    abstract fun add(
        newVal: Any?,
        configuration: Configuration,
    )

    abstract fun put(
        key: String,
        newVal: Any?,
        configuration: Configuration,
    )

    abstract fun renameKey(
        oldKey: String,
        newKeyName: String,
        configuration: Configuration,
    )

    protected open fun renameInMap(
        targetMap: Any?,
        oldKeyName: String,
        newKeyName: String,
        configuration: Configuration,
    ) {
        if (configuration.jsonProvider().isMap(targetMap)) {
            if (configuration.jsonProvider().getMapValue(targetMap, oldKeyName) ===
                JsonProvider.UNDEFINED
            ) {
                throw PathNotFoundException("No results for Key $oldKeyName found in map!")
            }
            configuration
                .jsonProvider()
                .setProperty(
                    targetMap,
                    newKeyName,
                    configuration.jsonProvider().getMapValue(targetMap, oldKeyName),
                )
            configuration.jsonProvider().removeProperty(targetMap, oldKeyName)
        } else {
            throw InvalidModificationException("Can only rename properties in a map")
        }
    }

    protected open fun targetInvalid(target: Any?): Boolean = target === JsonProvider.UNDEFINED || target == null

    override fun compareTo(other: PathRef): Int {
        val accessor = checkNotNull(getAccessor()) { "A no-op path reference cannot be ordered" }
        val otherAccessor = checkNotNull(other.getAccessor()) { "A no-op path reference cannot be ordered" }
        return accessor.toString().compareTo(otherAccessor.toString()) * -1
    }

    private open class RootPathRef(
        parent: Any?,
    ) : PathRef(parent) {
        override fun getAccessor(): Any? = "$"

        override fun set(
            newVal: Any?,
            configuration: Configuration,
        ): Unit = throw InvalidModificationException("Invalid set operation")

        override fun convert(
            mapFunction: MapFunction,
            configuration: Configuration,
        ): Unit = throw InvalidModificationException("Invalid map operation")

        override fun delete(configuration: Configuration): Unit = throw InvalidModificationException("Invalid delete operation")

        override fun add(
            newVal: Any?,
            configuration: Configuration,
        ) {
            if (configuration.jsonProvider().isArray(parent)) {
                configuration
                    .jsonProvider()
                    .setArrayIndex(
                        parent,
                        configuration.jsonProvider().length(parent),
                        newVal,
                    )
            } else {
                throw InvalidModificationException("Invalid add operation. $ is not an array")
            }
        }

        override fun put(
            key: String,
            newVal: Any?,
            configuration: Configuration,
        ) {
            if (configuration.jsonProvider().isMap(parent)) {
                configuration.jsonProvider().setProperty(parent, key, newVal)
            } else {
                throw InvalidModificationException("Invalid put operation. $ is not a map")
            }
        }

        override fun renameKey(
            oldKey: String,
            newKeyName: String,
            configuration: Configuration,
        ) {
            val target = parent
            if (targetInvalid(target)) {
                return
            }
            renameInMap(target, oldKey, newKeyName, configuration)
        }
    }

    private open class ArrayIndexPathRef(
        parent: Any?,
        private var index: Int,
    ) : PathRef(parent) {
        override fun set(
            newVal: Any?,
            configuration: Configuration,
        ) {
            configuration.jsonProvider().setArrayIndex(parent, index, newVal)
        }

        override fun convert(
            mapFunction: MapFunction,
            configuration: Configuration,
        ) {
            val currentValue = configuration.jsonProvider().getArrayIndex(parent, index)
            configuration
                .jsonProvider()
                .setArrayIndex(parent, index, mapFunction.map(currentValue, configuration))
        }

        override fun delete(configuration: Configuration) {
            configuration.jsonProvider().removeProperty(parent, index)
        }

        override fun add(
            newVal: Any?,
            configuration: Configuration,
        ) {
            val target = configuration.jsonProvider().getArrayIndex(parent, index)
            if (targetInvalid(target)) {
                return
            }
            if (configuration.jsonProvider().isArray(target)) {
                configuration.jsonProvider().setProperty(target, null, newVal)
            } else {
                throw InvalidModificationException("Can only add to an array")
            }
        }

        override fun put(
            key: String,
            newVal: Any?,
            configuration: Configuration,
        ) {
            val target = configuration.jsonProvider().getArrayIndex(parent, index)
            if (targetInvalid(target)) {
                return
            }
            if (configuration.jsonProvider().isMap(target)) {
                configuration.jsonProvider().setProperty(target, key, newVal)
            } else {
                throw InvalidModificationException("Can only add properties to a map")
            }
        }

        override fun renameKey(
            oldKey: String,
            newKeyName: String,
            configuration: Configuration,
        ) {
            val target = configuration.jsonProvider().getArrayIndex(parent, index)
            if (targetInvalid(target)) {
                return
            }
            renameInMap(target, oldKey, newKeyName, configuration)
        }

        override fun getAccessor(): Any? = index

        override fun compareTo(other: PathRef): Int {
            if (other is ArrayIndexPathRef) {
                return Integer.compare(other.index, index)
            }
            return super.compareTo(other)
        }
    }

    private open class ObjectPropertyPathRef(
        parent: Any?,
        private var property: String,
    ) : PathRef(parent) {
        override fun set(
            newVal: Any?,
            configuration: Configuration,
        ) {
            configuration.jsonProvider().setProperty(parent, property, newVal)
        }

        override fun convert(
            mapFunction: MapFunction,
            configuration: Configuration,
        ) {
            val currentValue = configuration.jsonProvider().getMapValue(parent, property)
            configuration
                .jsonProvider()
                .setProperty(parent, property, mapFunction.map(currentValue, configuration))
        }

        override fun delete(configuration: Configuration) {
            configuration.jsonProvider().removeProperty(parent, property)
        }

        override fun add(
            newVal: Any?,
            configuration: Configuration,
        ) {
            val target = configuration.jsonProvider().getMapValue(parent, property)
            if (targetInvalid(target)) {
                return
            }
            if (configuration.jsonProvider().isArray(target)) {
                configuration
                    .jsonProvider()
                    .setArrayIndex(
                        target,
                        configuration.jsonProvider().length(target),
                        newVal,
                    )
            } else {
                throw InvalidModificationException("Can only add to an array")
            }
        }

        override fun put(
            key: String,
            newVal: Any?,
            configuration: Configuration,
        ) {
            val target = configuration.jsonProvider().getMapValue(parent, property)
            if (targetInvalid(target)) {
                return
            }
            if (configuration.jsonProvider().isMap(target)) {
                configuration.jsonProvider().setProperty(target, key, newVal)
            } else {
                throw InvalidModificationException("Can only add properties to a map")
            }
        }

        override fun renameKey(
            oldKey: String,
            newKeyName: String,
            configuration: Configuration,
        ) {
            val target = configuration.jsonProvider().getMapValue(parent, property)
            if (targetInvalid(target)) {
                return
            }
            renameInMap(target, oldKey, newKeyName, configuration)
        }

        override fun getAccessor(): Any? = property
    }

    private open class ObjectMultiPropertyPathRef(
        parent: Any?,
        private var properties: Collection<@JvmSuppressWildcards String>,
    ) : PathRef(parent) {
        override fun set(
            newVal: Any?,
            configuration: Configuration,
        ) {
            for (property in properties) {
                configuration.jsonProvider().setProperty(parent, property, newVal)
            }
        }

        override fun convert(
            mapFunction: MapFunction,
            configuration: Configuration,
        ) {
            for (property in properties) {
                val currentValue = configuration.jsonProvider().getMapValue(parent, property)
                if (currentValue !== JsonProvider.UNDEFINED) {
                    configuration
                        .jsonProvider()
                        .setProperty(
                            parent,
                            property,
                            mapFunction.map(currentValue, configuration),
                        )
                }
            }
        }

        override fun delete(configuration: Configuration) {
            for (property in properties) {
                configuration.jsonProvider().removeProperty(parent, property)
            }
        }

        override fun add(
            newVal: Any?,
            configuration: Configuration,
        ): Unit = throw InvalidModificationException("Add can not be performed to multiple properties")

        override fun put(
            key: String,
            newVal: Any?,
            configuration: Configuration,
        ): Unit = throw InvalidModificationException("Put can not be performed to multiple properties")

        override fun renameKey(
            oldKey: String,
            newKeyName: String,
            configuration: Configuration,
        ): Unit =
            throw InvalidModificationException(
                "Rename can not be performed to multiple properties",
            )

        override fun getAccessor(): Any? = Utils.join("&&", properties)
    }

    companion object {
        @JvmField
        val NO_OP: PathRef =
            object : PathRef(null) {
                override fun getAccessor(): Any? = null

                override fun set(
                    newVal: Any?,
                    configuration: Configuration,
                ) {}

                override fun convert(
                    mapFunction: MapFunction,
                    configuration: Configuration,
                ) {}

                override fun delete(configuration: Configuration) {}

                override fun add(
                    newVal: Any?,
                    configuration: Configuration,
                ) {}

                override fun put(
                    key: String,
                    newVal: Any?,
                    configuration: Configuration,
                ) {}

                override fun renameKey(
                    oldKey: String,
                    newKeyName: String,
                    configuration: Configuration,
                ) {}
            }

        @JvmStatic
        fun create(
            obj: Any?,
            property: String,
        ): PathRef = ObjectPropertyPathRef(obj, property)

        @JvmStatic
        fun create(
            obj: Any?,
            properties: Collection<@JvmSuppressWildcards String>,
        ): PathRef = ObjectMultiPropertyPathRef(obj, properties)

        @JvmStatic
        fun create(
            array: Any?,
            index: Int,
        ): PathRef = ArrayIndexPathRef(array, index)

        @JvmStatic
        fun createRoot(root: Any?): PathRef = RootPathRef(root)
    }
}
