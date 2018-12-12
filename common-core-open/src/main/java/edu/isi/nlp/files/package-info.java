/**
 * Various utilities for working with file I/O.
 *
 * <p>The major bits of code here are:
 *
 * <ul>
 *   <li><b>{@link edu.isi.nlp.files.FileUtils}:</b> various utility methods for working with files.
 *   <li<b>{@link edu.isi.nlp.files.KeyValueSource}, {@link edu.isi.nlp.files.KeyValueSink} and
 *       related classes:</b> an implementation-agnostic way of working with mappings between keys
 *       and values. In practice we largely use these for mapping between document IDs and documents
 *       without worrying about whether the storage is a filesystem, database, or something else.
 *       These should be instantiated via {@link edu.isi.nlp.files.KeyValueSources} and {@link
 *       edu.isi.nlp.files.KeyValueSinks}
 * </ul>
 *
 * The are some additional minor utilities:
 *
 * <ul>
 *   <li><b>Tool to split a document corpus:</b> {@link edu.isi.nlp.files.SplitCorpus}
 *   <li><b>Class to load three column tab-separated files as a {@link
 *       edu.isi.nlp.collections.Multitable}</b>: {@link edu.isi.nlp.files.MultitableLoader}
 *   <li><b>Working with document lists:</b> {@link edu.isi.nlp.files.MergeFileLists}, {@link
 *       edu.isi.nlp.files.SubtractFileLists}
 *   <li><b>Working with document Id to file maps:</b> {@link
 *       edu.isi.nlp.files.DocIDToFileMapContains}, {@link edu.isi.nlp.files.SubtractFileMaps}
 * </ul>
 */
@ParametersAreNonnullByDefault
package edu.isi.nlp.files;

import javax.annotation.ParametersAreNonnullByDefault;
