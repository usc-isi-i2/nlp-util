package edu.isi.vista

import com.google.common.collect.*

fun <T> Sequence<T>.toImmutableList(): ImmutableList<T> {
    return ImmutableList.copyOf(this.asIterable())
}

fun <K, V> Iterable<Pair<K, V>>.toImmutableMap(): ImmutableMap<K, V> {
    return ImmutableMap.copyOf(this.map { java.util.AbstractMap.SimpleEntry(it.first, it.second) })
}

fun <K, V> Sequence<Pair<K, V>>.toImmutableSetMultimap(): ImmutableSetMultimap<K, V> {
    return ImmutableSetMultimap.copyOf(this.map { java.util.AbstractMap.SimpleEntry(it.first, it.second) }.asIterable())
}

fun <K, V> Iterable<Pair<K, V>>.toImmutableSetMultimap(): ImmutableSetMultimap<K, V> {
    return ImmutableSetMultimap.copyOf(this.map { java.util.AbstractMap.SimpleEntry(it.first, it.second) }.asIterable())
}

fun <K, V> Map<K, V>.toImmutableMap(): ImmutableMap<K, V> = ImmutableMap.copyOf(this)

@Suppress("UnstableApiUsage")
fun <K : Comparable<K>, V> Sequence<Pair<Range<K>,V>>.toImmutableRangeMap() : ImmutableRangeMap<K, V> {
    val ret = ImmutableRangeMap.builder<K, V>()
    this.forEach { (k, v) -> ret.put(k, v) }
    return ret.build()
}
