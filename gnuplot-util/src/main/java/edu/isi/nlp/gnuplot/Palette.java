package edu.isi.nlp.gnuplot;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.List;

public final class Palette {
  private ImmutableList<Color> colorList;

  private Palette(final List<Color> colors) {
    this.colorList = ImmutableList.copyOf(colors);
  }

  public static Palette from(final List<Color> colors) {
    return new Palette(colors);
  }

  public Iterable<Color> infinitePaletteLoop() {
    return Iterables.cycle(colorList);
  }

  /**
   * A seven-color palette safe for deuteranopia.
   *
   * <p>Taken from B. Wong, "Points of view: Color blindness" Nature Methods 8:411 (2011) via
   * http://mkweb.bcgsc.ca/colorblind/
   */
  public static Palette colorBlindSafe7() {
    return COLORBLIND_SAFE_7;
  }

  private static final Palette COLORBLIND_SAFE_7 =
      Palette.from(
          ImmutableList.of(
              // black
              Color.fromRGB255(0, 0, 0),
              // orange
              Color.fromRGB255(230, 159, 0),
              // sky blue
              Color.fromRGB255(86, 180, 233),
              // blueish green
              Color.fromRGB255(0, 158, 115),
              // yellow
              Color.fromRGB255(240, 228, 66),
              // blue
              Color.fromRGB255(0, 114, 178),
              // vermillion
              Color.fromRGB255(213, 94, 0),
              // reddish purple
              Color.fromRGB255(204, 121, 167)));
}
