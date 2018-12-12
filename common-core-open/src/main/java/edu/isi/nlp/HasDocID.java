package edu.isi.nlp;

import com.google.common.base.Function;
import edu.isi.nlp.symbols.Symbol;

/** Anything which has an associated document ID. */
public interface HasDocID {

  Symbol docID();

  enum DocIDFunction implements Function<HasDocID, Symbol> {
    INSTANCE;

    @Override
    public Symbol apply(final HasDocID input) {
      return input.docID();
    }
  }
}
