package edu.isi.nlp.strings.offsets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A character offset. Canonically this is counted by Unicode codepoints.
 */
public final class CharOffset extends AbstractOffset<CharOffset> {

  private CharOffset(final int val) {
    super(val);
  }

  @JsonCreator
  public static CharOffset asCharOffset(@JsonProperty("value") final int val) {
    return new CharOffset(val);
  }

  @Override
  public String toString() {
    return "c" + Integer.toString(asInt());
  }

  @Override
  public CharOffset shiftedCopy(final int shiftAmount) {
    return CharOffset.asCharOffset(asInt() + shiftAmount);
  }
}


