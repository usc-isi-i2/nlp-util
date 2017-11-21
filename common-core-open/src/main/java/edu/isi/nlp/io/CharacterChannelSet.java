package edu.isi.nlp.io;

import edu.isi.nlp.symbols.Symbol;

import com.google.common.base.Optional;
import com.google.common.io.CharSource;

import edu.isi.nlp.files.KeyValueSource;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Set;

/**
 * Something which contains multiple 'channels' of character data, indexed by {@link
 * Symbol}s.
 *
 * This should get merged/replaced with the newer {@link KeyValueSource}
 * code.
 */
public interface CharacterChannelSet {

  Set<Symbol> channelSet();

  Optional<CharSource> channelAsCharSource(Symbol key, Charset charset) throws IOException;
}
