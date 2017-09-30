package com.topie.huaifang.extensions

/**
 * Created by arman on 2017/9/25.
 */

fun <K, V> Map<K, V>.kFirstKeyOrNull(collect: ((k: K) -> Boolean)): K? {
    keys.forEach {
        if (collect(it)) {
            return it
        }
    }
    return null
}

fun <K, V> MutableMap<K, V>.kGetOrCreate(k: K, creator: ((k: K) -> V)): V {
    val v = get(k) ?: creator(k)
    put(k, v)
    return v
}

fun <K, V> MutableMap<K, V>.kGetValueNotNull(collect: ((k: K) -> Boolean), keyCreator: () -> K, valueCreator: (k: K) -> V): V {
    keys.forEach {
        if (collect(it)) {
            val value = get(it) ?: valueCreator(it)
            put(it, value)
            return value
        }
    }
    val key = keyCreator()
    val value = valueCreator(key)
    put(key, value)
    return value
}

fun <K, V> MutableMap<K, V>.kGetValueNotNull(k: K, valueCreator: (k: K) -> V): V {
    val value = get(k) ?: valueCreator(k)
    put(k, value)
    return value
}

fun <T> MutableList<T>.kGetOne(collect: ((t: T) -> Boolean), oneCreator: () -> T): T {
    val first = first(collect)
    if (first.kIsNotNull()) {
        return first
    }
    val t = oneCreator()
    add(t)
    return t
}

fun <T> List<T>.kGet(position: Int): T? {
    return if (size > position) {
        get(position)
    } else {
        null
    }
}
