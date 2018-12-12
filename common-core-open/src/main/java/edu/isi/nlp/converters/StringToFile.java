package edu.isi.nlp.converters;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;

public class StringToFile implements StringConverter<File> {

  public StringToFile() {}

  public Class<File> getValueClass() {
    return File.class;
  }

  @Override
  public File decode(final String s) {
    return new File(checkNotNull(s));
  }
}
