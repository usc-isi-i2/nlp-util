package edu.isi.nlp.strings.offsets;

public final class ByteOffset extends AbstractOffset<ByteOffset> {

  private ByteOffset(int val) {
    super(val);
  }

  public static ByteOffset asByteOffset(final int val) {
    return new ByteOffset(val);
  }

  @Override
  public String toString() {
    return "b" + asInt();
  }

  @Override
  public ByteOffset shiftedCopy(final int shiftAmount) {
    return ByteOffset.asByteOffset(asInt() + shiftAmount);
  }
}
