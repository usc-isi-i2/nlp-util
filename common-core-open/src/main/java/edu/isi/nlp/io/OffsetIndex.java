package edu.isi.nlp.io;

import edu.isi.nlp.strings.offsets.ByteOffset;
import edu.isi.nlp.strings.offsets.OffsetRange;
import edu.isi.nlp.symbols.Symbol;

import com.google.common.base.Optional;

import java.util.Set;

/**
 * Maps keys to offset ranges.  This is useful when, for example, many documents are concatenated
 * together and you wish to pull one out of the middle.
 */
public interface OffsetIndex {

  Optional<OffsetRange<ByteOffset>> byteOffsetsOf(Symbol key);

  Set<Symbol> keySet();
}
