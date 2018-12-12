package edu.isi.nlp.io;

import com.google.common.base.Optional;
import edu.isi.nlp.files.KeyValueSource;
import edu.isi.nlp.symbols.Symbol;
import java.io.IOException;

/**
 * Interface for something which can provide the original text for a document.
 *
 * <p>This should be merged into the newer {@link KeyValueSource} code.
 *
 * @author Ryan Gabbard
 */
public interface OriginalTextSource {

  /**
   * Returns the original document text for the specified document ID, if available. If it is not,
   * returns {@link com.google.common.base.Optional#absent()}.
   */
  public Optional<String> getOriginalText(Symbol docID) throws IOException;
}
