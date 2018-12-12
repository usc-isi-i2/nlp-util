/**
 * Utilities related to collections.
 *
 * <p>Major hunks of code:
 *
 * <ul>
 *   <li><b>{@link edu.isi.nlp.collections.Multitable} and friends:</b> Combines a Guava {@link
 *       edu.isi.nlp.collections.Multitable} and a {@link com.google.common.collect.Multimap} to
 *       allow mapping a pair of keys to multiple values.
 *   <li><b>Tools to make building {@link com.google.common.collect.ImmutableMap}s easier:</b>
 *       {@link com.google.common.collect.ImmutableMap} is great not only because it is immutable
 *       but also because it has a deterministic iteration order. But its builder is often overly
 *       strict for particular applications - for example, forbidding duplicate key-value pairs
 *       being added even if identical. We provide a number of more relaxed builders including
 *       {@link edu.isi.nlp.collections.MapUtils#immutableMapBuilderIgnoringDuplicates()}, {@link
 *       edu.isi.nlp.collections.MapUtils#immutableMapBuilderResolvingDuplicatesBy(java.util.Comparator)},
 *       and {@link edu.isi.nlp.collections.MapUtils#immutableMapBuilderAllowingSameEntryTwice()}.
 *       See {@link edu.isi.nlp.collections.LaxImmutableMapBuilder} for more details.
 *   <li>Utilities for most major collection types: {@link edu.isi.nlp.collections.CollectionUtils},
 *       {@link edu.isi.nlp.collections.IterableUtils}, {@link
 *       edu.isi.nlp.collections.IteratorUtils}, {@link edu.isi.nlp.collections.ListUtils}, {@link
 *       edu.isi.nlp.collections.MapUtils}, {@link edu.isi.nlp.collections.MultimapUtils}, {@link
 *       edu.isi.nlp.collections.MultisetUtils}, {@link edu.isi.nlp.collections.RangeUtils}, {@link
 *       edu.isi.nlp.collections.SetUtils}, and {@link edu.isi.nlp.collections.TableUtils}.
 * </ul>
 *
 * Minor hunks of code: generating samples for bootstrap confidence estimation ({@link
 * edu.isi.nlp.collections.BootstrapIterator}, shuffling things ({@link
 * edu.isi.nlp.collections.ShufflingIterable}, {@link edu.isi.nlp.collections.ShufflingCollection}),
 * and sets of potentially overlapping ranges ({@link edu.isi.nlp.collections.OverlappingRangeSet}).
 */
package edu.isi.nlp.collections;
