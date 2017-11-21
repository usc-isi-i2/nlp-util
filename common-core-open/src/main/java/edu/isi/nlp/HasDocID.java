package edu.isi.nlp;

import edu.isi.nlp.symbols.Symbol;

import com.google.common.base.Function;

/**
 * Anything which has an associated document ID.
 */
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
