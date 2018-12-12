package edu.isi.nlp.io;

import com.google.common.base.Optional;
import edu.isi.nlp.files.KeyValueSource;
import edu.isi.nlp.symbols.Symbol;
import java.io.File;

/**
 * Represents a mapping from document IDs to files. See {@link DocIDToFileMappings} for how to
 * create instances.
 *
 * <p>This should be merged into the newer {@link KeyValueSource} code.
 *
 * @author Ryan Gabbard
 */
public interface DocIDToFileMapping {

  /**
   * Returns the {@link java.io.File} corresponding to the specified document ID, if possible.
   * Otherwise, returns {@link com.google.common.base.Optional#absent()}. Beware that even if absent
   * is not returned, there is no guarantee the specified file exists.
   *
   * @param docID May not be null. If it is, the result is undefined.
   */
  Optional<File> fileForDocID(Symbol docID);
}
