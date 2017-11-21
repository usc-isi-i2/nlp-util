package edu.isi.nlp.xml;

public class XMLException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public XMLException(String msg) {
    super(msg);
  }

  public XMLException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
