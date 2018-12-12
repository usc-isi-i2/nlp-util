package edu.isi.nlp.converters;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;

public class StringToOSFile implements StringConverter<File> {

  public StringToOSFile() {}

  public Class<File> getValueClass() {
    return File.class;
  }

  @Override
  public File decode(final String s) {
    String path = s;
    path = path.replace("\\\\", "/nfs/").replace("\\", "/");
    return new File(checkNotNull(path));
  }
}
