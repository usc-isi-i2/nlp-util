package edu.isi.nlp.gnuplot;

public final class GnuPlotUtils {

  private GnuPlotUtils() {
    throw new UnsupportedOperationException();
  }

  public static String gnuPlotString(String s) {
    return "\"" + s + "\"";
  }
}
