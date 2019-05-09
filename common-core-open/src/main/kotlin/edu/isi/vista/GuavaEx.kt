package edu.isi.vista

import com.google.common.collect.*
import java.util.function.Function

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


/**
 * Returns an ImmutableListMultimap containing the elements from the given sequence indexed by the key returned from
 * keySelector function applied to each element.
 *
 * Each item of the sequence will be appear as a value in the resulting multimap,
 * yielding a multimap with the same size as the input sequence.
 * The key used to store that value in the multimap will be the result of calling the function on that value.
 *
 * In the returned multimap, keys appear in the order they are first encountered, and the values corresponding to
 * each key appear in the same order as they are encountered.
 * The returned map preserves the entry iteration order of the original sequence.
 *
 * This is just a wrapper around Guava's Multimaps.index()
 *
 * The operation is terminal.
 */
fun <T, K> Sequence<T>.associateByToListMultimap(keySelector: (T) -> K) : ImmutableListMultimap<K, T> {
    val ret = ImmutableListMultimap.builder<K, T>()
    this.forEach { ret.put(keySelector(it), it) }
    return ret.build()
}

/**
 * Returns an ImmutableSetMultimap containing the elements from the given sequence indexed by the key returned from
 * keySelector function applied to each element.
 *
 * Each item of the sequence will be appear as a value in the resulting multimap,
 * yielding a multimap with the same size as the input sequence. The key used to store that value in the multimap will be the result of calling the function on that value. The resulting multimap is created as an immutable snapshot. In the returned multimap, keys appear in the order they are first encountered, and the values corresponding to each key appear in the same order as they are encountered.
 *
 * Order guarantees are pending https://github.com/google/guava/issues/3460
 *
 * The operation is terminal.
 */
fun <T, K> Sequence<T>.associateByToSetMultimap(keySelector: (T) -> K) : ImmutableSetMultimap<K, T> {
    val ret = ImmutableSetMultimap.builder<K, T>()
    this.forEach { ret.put(keySelector(it), it) }
    return ret.build()
}
