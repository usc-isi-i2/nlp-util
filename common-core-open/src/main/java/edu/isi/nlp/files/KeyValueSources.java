package edu.isi.nlp.files;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteSource;
import edu.isi.nlp.symbols.Symbol;
import edu.isi.nlp.symbols.SymbolUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.annotation.Nonnull;

/**
 * Provides factory methods for key-value sources.
 *
 * @author Constantine Lignos, Ryan Gabbard
 */
public final class KeyValueSources {

  private KeyValueSources() {
    throw new UnsupportedOperationException();
  }

  /** Creates a new key-value source based on the contents of the specified symbol to file map. */
  @Nonnull
  public static ImmutableKeyValueSource<Symbol, ByteSource> fromFileMap(
      final Map<Symbol, File> fileMap) {
    return new FileMapKeyToByteSource(fileMap);
  }

  /**
   * Creates a new key-value source based on the contents of the specified embedded database.
   *
   * @param dbFile a database file created using {@link KeyValueSinks#forPalDB(File, boolean)}
   * @return a key-value source
   * @throws IOException if the database could not be opened for reading
   */
  @Nonnull
  public static ImmutableKeyValueSource<Symbol, ByteSource> fromPalDB(final File dbFile)
      throws IOException {
    return PalDBKeyValueSource.fromFile(dbFile);
  }

  /**
   * Creates a new source using a zip file where each value is located at an entry with the same
   * name as the key. The caller must ensure that the zip file is not closed or modified, otherwise
   * all behavior is undefined. All files in the zip file will be used; there is currently no way to
   * exclude specific files. To specify a function that defines the mapping between keys and the
   * entry used for their values, see {@link #fromZip(ZipFile, Function)}.
   *
   * @param zipFile the zip file to use as a source
   * @return a new key-value source backed by the specified zip file
   * @see #fromZip(ZipFile, Function)
   */
  @Nonnull
  public static ImmutableKeyValueSource<Symbol, ByteSource> fromZip(final ZipFile zipFile) {
    return fromZip(zipFile, SymbolUtils.symbolizeFunction());
  }

  /**
   * Creates a new source using a zip file and a function that maps each entry in the zip file to a
   * unique key. The caller must ensure that the zip file is not closed or modified, otherwise all
   * behavior is undefined. All files in the zip file will be used; there is currently no way to
   * exclude specific files. Use a default identity-like function that defines the mapping between
   * keys and the entry used for their values, see {@link #fromZip(ZipFile)}.
   *
   * @param zipFile the zip file to use as a source
   * @param idExtractor a function that returns a unique id for every file contained in the zip
   * @return a new key-value source backed by the specified zip file
   * @see #fromZip(ZipFile)
   */
  @Nonnull
  public static ImmutableKeyValueSource<Symbol, ByteSource> fromZip(
      final ZipFile zipFile, final Function<String, Symbol> idExtractor) {
    final ImmutableMap.Builder<Symbol, String> ret = ImmutableMap.builder();
    // Build a map of the key for each file to the filename
    final Enumeration<? extends ZipEntry> entries = zipFile.entries();
    while (entries.hasMoreElements()) {
      final ZipEntry entry = entries.nextElement();
      final String name = entry.getName();
      // Skip directories
      if (entry.isDirectory()) {
        continue;
      }
      final Symbol id = checkNotNull(idExtractor.apply(name));
      ret.put(id, name);
    }
    return new ZipKeyValueSource(zipFile, ret.build());
  }

  @Nonnull
  public static ImmutableKeyValueSource<Symbol, ByteSource> fromVistaUtilsZipKeyValueSource(
      final ZipFile zipFile) {
    // read "__keys" file
    ZipEntry keysEntry = zipFile.getEntry(("__keys"));
    String keysString = null;
    try {
      keysString = ZipFiles.entryAsString(zipFile, keysEntry, StandardCharsets.US_ASCII);
    } catch (IOException e) {
      e.printStackTrace();
    }
    String[] keysArray = keysString.split("\n");

    // explicitly construct the second argument to the ZipKeyValueSource constructor
    final ImmutableMap.Builder<Symbol, String> keyMapBuilder = ImmutableMap.builder();
    for (int i = 0; i < keysArray.length; i++) {
      String key = keysArray[i];
      keyMapBuilder.put(Symbol.from(key), key);
    }
    return new ZipKeyValueSource(zipFile, keyMapBuilder.build());
  }
}
