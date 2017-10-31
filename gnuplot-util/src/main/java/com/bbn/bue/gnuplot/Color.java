package com.bbn.bue.gnuplot;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Range;

public final class Color {

  private final String colorString;

  private Color(String colorString) {
    this.colorString = checkNotNull(colorString);
    checkArgument(colorString.length() == 7 && colorString.charAt(0) == '#');
    for (int i = 1; i < 7; ++i) {
      final char c = colorString.charAt(i);
      checkArgument(Character.isDigit(c) || (c >= 'a' && c <= 'f')
          || (c >= 'A' && c <= 'F'));
    }
  }

  public static Color fromHexString(String colorString) {
    return new Color(colorString);
  }

  public static Color fromRGB255(int red, int green, int blue) {
    return fromHexString("#" + rgbComponentToHex(red, "red")
        + rgbComponentToHex(green, "green")
        + rgbComponentToHex(blue, "blue"));
  }

  public String asColorString() {
    return colorString;
  }

  public String asQuotedColorString() {
    return "\"" + asColorString() + "\"";
  }

  private static final Range<Integer> TWO_FIFTY_FIVE = Range.closed(0, 255);

  private static String rgbComponentToHex(int colorComponent, String componentName) {
    checkArgument(TWO_FIFTY_FIVE.contains(colorComponent),
        "RGB color values must lie in [0,255] but got "
            + componentName + " component %s", colorComponent);

    if (colorComponent == 0) {
      // need to special case to ensure two digits
      return "00";
    } else {
      return Integer.toHexString(colorComponent).toUpperCase();
    }
  }
}
