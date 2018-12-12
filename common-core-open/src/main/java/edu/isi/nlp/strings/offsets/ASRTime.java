package edu.isi.nlp.strings.offsets;

public final class ASRTime extends AbstractOffset<ASRTime> {

  private ASRTime(int val) {
    super(val);
  }

  public static ASRTime of(int val) {
    return new ASRTime(val);
  }

  @Override
  public ASRTime shiftedCopy(final int shiftAmount) {
    return ASRTime.of(asInt() + shiftAmount);
  }
}
