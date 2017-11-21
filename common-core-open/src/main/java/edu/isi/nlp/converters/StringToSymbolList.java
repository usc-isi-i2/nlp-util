package edu.isi.nlp.converters;

import edu.isi.nlp.symbols.Symbol;
import edu.isi.nlp.symbols.SymbolUtils;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class StringToSymbolList implements StringConverter<List<Symbol>> {

  public StringToSymbolList(String delimiter) {
    this.subConverter =
        new StringToStringList(checkNotNull(delimiter));
  }

  @Override
  public List<Symbol> decode(String s) {
    checkNotNull(s);
    return SymbolUtils.listFrom(subConverter.decode(s));
  }

  private final StringConverter<? extends List<String>> subConverter;
}
