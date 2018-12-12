package edu.isi.nlp.io;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import edu.isi.nlp.files.FileUtils;
import edu.isi.nlp.files.KeyValueSource;
import edu.isi.nlp.symbols.Symbol;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Represents a corpus of concatenated text files which have been indexed via {@link
 * IndexFlatGigaword} or some similar procedure. Typically this is fine for backing user interfaces
 * (e.g. when it is too slow to copy all 12M files of Gigaword to the DMZ) but too slow for bulk
 * use.
 *
 * <p>This should be merged into the newer {@link KeyValueSource} code.
 *
 * @author Ryan Gabbard
 */
public final class OffsetIndexedCorpus implements OriginalTextSource {

  private final DocIDToFileMapping corpusTextMapping;
  private final DocIDToFileMapping corpusIndexMapping;
  private final LoadingCache<File, OffsetIndex> offsetIndexCache;

  private OffsetIndexedCorpus(
      final DocIDToFileMapping corpusTextMapping,
      final DocIDToFileMapping corpusIndexMapping,
      final LoadingCache<File, OffsetIndex> offsetIndexCache) {
    this.corpusTextMapping = checkNotNull(corpusTextMapping);
    this.corpusIndexMapping = checkNotNull(corpusIndexMapping);
    this.offsetIndexCache = checkNotNull(offsetIndexCache);
  }

  public static OriginalTextSource fromTextAndOffsetFiles(
      DocIDToFileMapping corpusTextMapping, final DocIDToFileMapping corpusIndexMapping) {
    final LoadingCache<File, OffsetIndex> indexCache =
        CacheBuilder.newBuilder()
            .maximumSize(3)
            .build(
                new CacheLoader<File, OffsetIndex>() {
                  @Override
                  public OffsetIndex load(final File f) throws Exception {
                    return OffsetIndices.readBinary(FileUtils.asCompressedByteSource(f));
                  }
                });
    return new OffsetIndexedCorpus(corpusTextMapping, corpusIndexMapping, indexCache);
  }

  @Override
  public Optional<String> getOriginalText(final Symbol docID) throws IOException {
    final Optional<File> file = corpusTextMapping.fileForDocID(docID);
    if (file.isPresent()) {
      try {
        final Optional<File> indexFile = corpusIndexMapping.fileForDocID(docID);
        if (indexFile.isPresent()) {
          final IndexedByteSource source =
              IndexedByteSource.from(
                  Files.asByteSource(file.get()), offsetIndexCache.get(indexFile.get()));
          final Optional<CharSource> channelCharSource =
              source.channelAsCharSource(docID, Charsets.UTF_8);

          if (channelCharSource.isPresent()) {
            return Optional.of(channelCharSource.get().read());
          } else {
            return Optional.absent();
          }
        } else {
          throw new IOException("No index found for corpus chunk " + file);
        }
      } catch (ExecutionException e) {
        throw new IOException(e);
      }
    } else {
      return Optional.absent();
    }
  }
}
