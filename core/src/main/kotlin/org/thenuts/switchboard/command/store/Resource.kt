package org.thenuts.switchboard.command.store

import kotlin.reflect.KProperty

data class Box<T>(val inner: T)
fun <T> Box<T>?.unwrapOr(supplier: () -> T): T {
    if (this == null)
        return supplier()
    return this.inner
}

class Resource(val key: Any) {
    internal var box: Box<Any>? = null
}

sealed class ResourceHandler<T>(val priority: Int) {
    var box: Box<Any>? = null

    class Readable<T>(priority: Int) : ResourceHandler<T>(priority) {
        fun read(): T {
            return box.unwrapOr {
                throw IllegalAccessException("accessing uninitialized store")
            } as T
        }

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T = read()
    }

    class Writeable<T>(priority: Int) : ResourceHandler<T>(priority) {
        fun prev(): T {
            return box.unwrapOr {
                throw IllegalAccessException("accessing uninitialized store")
            } as T
        }

        fun write(value: T) {
            box = Box(value as Any)
        }

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T = prev()

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            write(value)
        }
    }
}

interface Delegate<T> {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T
}

interface MutableDelegate<T> : Delegate<T> {
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T)
}