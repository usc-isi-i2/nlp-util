package edu.isi.nlp.converters;

import static com.google.common.base.Preconditions.checkNotNull;

import edu.isi.nlp.symbols.Symbol;
import edu.isi.nlp.symbols.SymbolUtils;
import java.util.Set;

public class StringToSymbolSet implements StringConverter<Set<Symbol>> {

  public StringToSymbolSet(String delimiter) {
    this.subConverter = new StringToStringSet(checkNotNull(delimiter));
  }

  @Override
  public Set<Symbol> decode(String s) {
    checkNotNull(s);
    return SymbolUtils.setFrom(subConverter.decode(s));
  }

  private final StringConverter<Set<String>> subConverter;
}
