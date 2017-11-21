package edu.isi.nlp;

import edu.isi.nlp.strings.offsets.CharOffset;
import edu.isi.nlp.strings.offsets.OffsetRange;
import edu.isi.nlp.strings.offsets.UTF16Offset;

import com.google.common.base.Optional;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UnicodeFriendlyStringTest {

  // non-BMP emoji
  private static final String CHEESE_WEDGE = "\uD83E\uDDC0";
  private static final int CHEESE_WEDGE_INT = 129472;
  private static final String FACE_WITH_TEARS_OF_JOY = "\uD83D\uDE02";
  private static final int FACE_WITH_TEARS_OF_JOY_INT = 128514;

  private final String EMPTY_CODEUNITS = "";

  private final UnicodeFriendlyString EMPTY = StringUtils.unicodeFriendly(EMPTY_CODEUNITS);
  private final String HELLO_WORLD_CODEUNITS = "Hello world!";
  private final UnicodeFriendlyString HELLO_WORLD = StringUtils.unicodeFriendly(
      HELLO_WORLD_CODEUNITS);
  private final String HELLO_CHEESE_WEDGE_CODEUNITS = "Hello " + CHEESE_WEDGE + "!";
  private final UnicodeFriendlyString HELLO_CHEESE_WEDGE =
      StringUtils.unicodeFriendly(HELLO_CHEESE_WEDGE_CODEUNITS);
  private final String CHEESE_WEDGE_AND_TEARS_CODEUNITS = CHEESE_WEDGE + FACE_WITH_TEARS_OF_JOY;
  private final UnicodeFriendlyString CHEESE_WEDGE_AND_TEARS =
      StringUtils.unicodeFriendly(CHEESE_WEDGE_AND_TEARS_CODEUNITS);
  private final String HELLO_CHEESE_WEDGE_TEARS_CODEUNITS =
      "Hello " + CHEESE_WEDGE + FACE_WITH_TEARS_OF_JOY + "!";
  private final UnicodeFriendlyString HELLO_CHEESE_WEDGE_TEARS =
      StringUtils.unicodeFriendly(HELLO_CHEESE_WEDGE_TEARS_CODEUNITS);

  @Test
  public void testHasNonBmpCharacter() {
    assertFalse(EMPTY.hasNonBmpCharacter());
    assertFalse(HELLO_WORLD.hasNonBmpCharacter());
    assertTrue(StringUtils.unicodeFriendly(CHEESE_WEDGE).hasNonBmpCharacter());
    assertTrue(StringUtils.unicodeFriendly(CHEESE_WEDGE + FACE_WITH_TEARS_OF_JOY).hasNonBmpCharacter());
    assertTrue(StringUtils.unicodeFriendly(FACE_WITH_TEARS_OF_JOY).hasNonBmpCharacter());
    assertTrue(HELLO_CHEESE_WEDGE.hasNonBmpCharacter());
    assertTrue(HELLO_CHEESE_WEDGE_TEARS.hasNonBmpCharacter());
  }

  @Test
  public void testHasNonBmpCharacterRange() {
    assertFalse(HELLO_WORLD.hasNonBmpCharacter(OffsetRange.charOffsetRange(0, 4)));
    // hello
    assertFalse(HELLO_CHEESE_WEDGE.hasNonBmpCharacter(OffsetRange.charOffsetRange(0, 4)));
    // !
    assertFalse(HELLO_CHEESE_WEDGE.hasNonBmpCharacter(OffsetRange.charOffsetRange(7, 7)));
    // CHEESE_WEDGE! - non-BMP at beginning
    assertTrue(HELLO_CHEESE_WEDGE.hasNonBmpCharacter(OffsetRange.charOffsetRange(6, 7)));
    // o CHEESE_WEDGE - non-BMP at end
    assertTrue(HELLO_CHEESE_WEDGE.hasNonBmpCharacter(OffsetRange.charOffsetRange(4, 6)));
    // o CHEESE_WEDGE! - non-BMP in middle
    assertTrue(HELLO_CHEESE_WEDGE.hasNonBmpCharacter(OffsetRange.charOffsetRange(4, 7)));

    // for a region with multiple adjacent non-BMP characters
    assertTrue(CHEESE_WEDGE_AND_TEARS.hasNonBmpCharacter());
    assertTrue(CHEESE_WEDGE_AND_TEARS.hasNonBmpCharacter(OffsetRange.charOffsetRange(0, 0)));
    assertTrue(CHEESE_WEDGE_AND_TEARS.hasNonBmpCharacter(OffsetRange.charOffsetRange(1, 1)));
    // covers transitions between regions with non-bmp, regions with multiple bmp, and regions with non-bmp.
    // hello
    assertFalse(HELLO_CHEESE_WEDGE_TEARS.hasNonBmpCharacter(OffsetRange.charOffsetRange(0, 4)));
    // !
    assertFalse(HELLO_CHEESE_WEDGE_TEARS.hasNonBmpCharacter(OffsetRange.charOffsetRange(8, 8)));
    // CHEESE_WEDGE + FACE_WITH_TEARS_OF_JOY !- non-BMP at beginning
    assertTrue(HELLO_CHEESE_WEDGE_TEARS.hasNonBmpCharacter(OffsetRange.charOffsetRange(6, 8)));
    // CHEESE_WEDGE + FACE_WITH_TEARS_OF_JOY - non-BMP at end
    assertTrue(HELLO_CHEESE_WEDGE_TEARS.hasNonBmpCharacter(OffsetRange.charOffsetRange(4, 7)));
    // CHEESE_WEDGE + FACE_WITH_TEARS_OF_JOY ! - non-BMP in middle
    assertTrue(HELLO_CHEESE_WEDGE_TEARS.hasNonBmpCharacter(OffsetRange.charOffsetRange(4, 8)));
  }

  @Test
  public void testUtf16CodePoints() {
    assertEquals(EMPTY_CODEUNITS, EMPTY.utf16CodeUnits());
    assertEquals(HELLO_WORLD_CODEUNITS, HELLO_WORLD.utf16CodeUnits());
    assertEquals(CHEESE_WEDGE_AND_TEARS_CODEUNITS, CHEESE_WEDGE_AND_TEARS.utf16CodeUnits());
    assertEquals(HELLO_CHEESE_WEDGE_CODEUNITS, HELLO_CHEESE_WEDGE.utf16CodeUnits());
    assertEquals(HELLO_CHEESE_WEDGE_TEARS_CODEUNITS, HELLO_CHEESE_WEDGE_TEARS.utf16CodeUnits());
  }

  @Test
  public void testLengthInUtf16CodeUnits() {
    assertEquals(EMPTY_CODEUNITS.length(), EMPTY.lengthInUtf16CodeUnits());
    assertEquals(HELLO_WORLD_CODEUNITS.length(), HELLO_WORLD.lengthInUtf16CodeUnits());
    assertEquals(CHEESE_WEDGE_AND_TEARS_CODEUNITS.length(),
        CHEESE_WEDGE_AND_TEARS.lengthInUtf16CodeUnits());
    assertEquals(HELLO_CHEESE_WEDGE_CODEUNITS.length(),
        HELLO_CHEESE_WEDGE.lengthInUtf16CodeUnits());
    assertEquals(HELLO_CHEESE_WEDGE_TEARS_CODEUNITS.length(),
        HELLO_CHEESE_WEDGE_TEARS.lengthInUtf16CodeUnits());
  }

  @Test
  public void testLengthInCodePoints() {
    assertEquals(0, EMPTY.lengthInCodePoints());
    assertEquals(12, HELLO_WORLD.lengthInCodePoints());
    assertEquals(2, CHEESE_WEDGE_AND_TEARS.lengthInCodePoints());
    assertEquals(8, HELLO_CHEESE_WEDGE.lengthInCodePoints());
    assertEquals(9, HELLO_CHEESE_WEDGE_TEARS.lengthInCodePoints());
  }

  @Test
  public void testSubstringByCodePoints() {
    // substring tests with start offset only
    Assert.assertEquals(StringUtils.unicodeFriendly("world!"), HELLO_WORLD.substringByCodePoints(
        CharOffset.asCharOffset(6)));
    // start substring past non-BMP
    Assert.assertEquals(StringUtils.unicodeFriendly("!"), HELLO_CHEESE_WEDGE.substringByCodePoints(
        CharOffset.asCharOffset(7)));
    Assert.assertEquals(StringUtils.unicodeFriendly("!"),
        HELLO_CHEESE_WEDGE_TEARS.substringByCodePoints(CharOffset.asCharOffset(8)));
    // start substring before non-BMP
    Assert.assertEquals(StringUtils.unicodeFriendly(CHEESE_WEDGE + "!"), HELLO_CHEESE_WEDGE.substringByCodePoints(
        CharOffset.asCharOffset(6)));
    Assert.assertEquals(StringUtils.unicodeFriendly(CHEESE_WEDGE_AND_TEARS_CODEUNITS + "!"),
        HELLO_CHEESE_WEDGE_TEARS.substringByCodePoints(CharOffset.asCharOffset(6)));
    // start substring at non-BMP
    Assert.assertEquals(StringUtils.unicodeFriendly("o " + CHEESE_WEDGE + "!"),
        HELLO_CHEESE_WEDGE.substringByCodePoints(CharOffset.asCharOffset(4)));
    Assert.assertEquals(StringUtils.unicodeFriendly("o " + CHEESE_WEDGE_AND_TEARS_CODEUNITS + "!"),
        HELLO_CHEESE_WEDGE_TEARS.substringByCodePoints(CharOffset.asCharOffset(4)));


    // substring test with start and end offsets
    Assert.assertEquals(StringUtils.unicodeFriendly("worl"), HELLO_WORLD.substringByCodePoints(
        CharOffset.asCharOffset(6), CharOffset.asCharOffset(10)));
    // start substring past non-BMP
    Assert.assertEquals(StringUtils.unicodeFriendly("!"), HELLO_CHEESE_WEDGE.substringByCodePoints(
        CharOffset.asCharOffset(7), CharOffset.asCharOffset(8)));
    Assert.assertEquals(StringUtils.unicodeFriendly("!"), HELLO_CHEESE_WEDGE_TEARS.substringByCodePoints(
        CharOffset.asCharOffset(8), CharOffset.asCharOffset(9)));
    // start substring before non-BMP
    Assert.assertEquals(StringUtils.unicodeFriendly(CHEESE_WEDGE), HELLO_CHEESE_WEDGE.substringByCodePoints(
        CharOffset.asCharOffset(6), CharOffset.asCharOffset(7)));
    assertEquals(CHEESE_WEDGE_AND_TEARS, HELLO_CHEESE_WEDGE_TEARS.substringByCodePoints(
        CharOffset.asCharOffset(6), CharOffset.asCharOffset(8)));
    // start substring at non-BMP
    Assert.assertEquals(StringUtils.unicodeFriendly("o " + CHEESE_WEDGE),
        HELLO_CHEESE_WEDGE.substringByCodePoints(CharOffset.asCharOffset(4), CharOffset.asCharOffset(7)));
    Assert.assertEquals(StringUtils.unicodeFriendly("o " + CHEESE_WEDGE_AND_TEARS_CODEUNITS),
        HELLO_CHEESE_WEDGE_TEARS.substringByCodePoints(
            CharOffset.asCharOffset(4), CharOffset.asCharOffset(8)));

    // substring test with character offset ranges
    Assert.assertEquals(StringUtils.unicodeFriendly("worl"), HELLO_WORLD.substringByCodePoints(
        OffsetRange.charOffsetRange(6, 9)));
    // start substring past non-BMP
    Assert.assertEquals(StringUtils.unicodeFriendly("!"), HELLO_CHEESE_WEDGE.substringByCodePoints(
        OffsetRange.charOffsetRange(7, 7)));
    Assert.assertEquals(StringUtils.unicodeFriendly("!"), HELLO_CHEESE_WEDGE_TEARS.substringByCodePoints(
        OffsetRange.charOffsetRange(8, 8)));
    // start substring before non-BMP
    Assert.assertEquals(StringUtils.unicodeFriendly(CHEESE_WEDGE), HELLO_CHEESE_WEDGE.substringByCodePoints(
        OffsetRange.charOffsetRange(6, 6)));
    assertEquals(CHEESE_WEDGE_AND_TEARS, HELLO_CHEESE_WEDGE_TEARS.substringByCodePoints(
        OffsetRange.charOffsetRange(6, 7)));
    // start substring at non-BMP
    Assert.assertEquals(StringUtils.unicodeFriendly("o " + CHEESE_WEDGE),
        HELLO_CHEESE_WEDGE.substringByCodePoints(OffsetRange.charOffsetRange(4, 6)));
    Assert.assertEquals(StringUtils.unicodeFriendly("o " + CHEESE_WEDGE_AND_TEARS_CODEUNITS),
        HELLO_CHEESE_WEDGE_TEARS.substringByCodePoints(OffsetRange.charOffsetRange(4, 7)));
  }

  @Test
  public void testIsEmpty() {
    assertTrue(EMPTY.isEmpty());
    assertFalse(HELLO_WORLD.isEmpty());
    assertFalse(CHEESE_WEDGE_AND_TEARS.isEmpty());
    assertFalse(HELLO_CHEESE_WEDGE.isEmpty());
    assertFalse(HELLO_CHEESE_WEDGE_TEARS.isEmpty());
  }

  @Test
  public void testTrim() {
    // doesn't need trimming
    assertEquals(EMPTY, EMPTY.trim());
    assertEquals(HELLO_WORLD, HELLO_WORLD.trim());
    assertEquals(CHEESE_WEDGE_AND_TEARS, CHEESE_WEDGE_AND_TEARS.trim());
    assertEquals(HELLO_CHEESE_WEDGE, HELLO_CHEESE_WEDGE.trim());
    assertEquals(HELLO_CHEESE_WEDGE_TEARS, HELLO_CHEESE_WEDGE_TEARS.trim());

    // does need trimming, no non-BMP
    Assert.assertEquals(StringUtils.unicodeFriendly("world!"),
        HELLO_WORLD.substringByCodePoints(CharOffset.asCharOffset(5)).trim());
    // does need trimming, has non-BMP
    Assert.assertEquals(StringUtils.unicodeFriendly(CHEESE_WEDGE),
        HELLO_CHEESE_WEDGE.substringByCodePoints(CharOffset.asCharOffset(5), CharOffset.asCharOffset(7)).trim());
    // does need trimming, has non-BMP
    assertEquals(CHEESE_WEDGE_AND_TEARS,
        HELLO_CHEESE_WEDGE_TEARS.substringByCodePoints(
            CharOffset.asCharOffset(5), CharOffset.asCharOffset(8)).trim());
  }

  @Test
  public void testBmpLessSubstringOfBmpful() {
    assertFalse(HELLO_CHEESE_WEDGE.substringByCodePoints(CharOffset.asCharOffset(0),
        CharOffset.asCharOffset(3)).hasNonBmpCharacter());
    assertFalse(HELLO_CHEESE_WEDGE_TEARS.substringByCodePoints(CharOffset.asCharOffset(0),
        CharOffset.asCharOffset(3)).hasNonBmpCharacter());
  }

  private void testExpectedIndexIsCorrect(final Optional<CharOffset> received, final int expected) {
    assertTrue(received.isPresent());
    //noinspection OptionalGetWithoutIsPresent
    assertTrue(received.get().asInt() == expected);
  }

  @Test
  public void testCodePointIndexOfAndCodePointAt() {
    // test for proper behavior with a missing example
    final UnicodeFriendlyString notContained = StringUtils.unicodeFriendly("notContained");
    // works when nothing is present
    assertFalse(EMPTY.codePointIndexOf(notContained).isPresent());
    // works with only a single type of UnicodeFriendlyString
    assertFalse(HELLO_WORLD.codePointIndexOf(notContained).isPresent());
    assertFalse(CHEESE_WEDGE_AND_TEARS.codePointIndexOf(notContained).isPresent());
    // test across transitions
    assertFalse(HELLO_CHEESE_WEDGE.codePointIndexOf(notContained).isPresent());
    assertFalse(HELLO_CHEESE_WEDGE_TEARS.codePointIndexOf(notContained).isPresent());

    final UnicodeFriendlyString multipleTearsOfJoy =
        StringUtils.unicodeFriendly(FACE_WITH_TEARS_OF_JOY + FACE_WITH_TEARS_OF_JOY);
    // test for present and correct index
    testExpectedIndexIsCorrect(HELLO_WORLD.codePointIndexOf(HELLO_WORLD), 0);

    // multiple present
    testExpectedIndexIsCorrect(
        multipleTearsOfJoy.codePointIndexOf(StringUtils.unicodeFriendly(FACE_WITH_TEARS_OF_JOY)), 0);
    testExpectedIndexIsCorrect(
        CHEESE_WEDGE_AND_TEARS.codePointIndexOf(StringUtils.unicodeFriendly(CHEESE_WEDGE)), 0);
    testExpectedIndexIsCorrect(
        CHEESE_WEDGE_AND_TEARS.codePointIndexOf(StringUtils.unicodeFriendly(FACE_WITH_TEARS_OF_JOY)), 1);
    testExpectedIndexIsCorrect(HELLO_CHEESE_WEDGE.codePointIndexOf(StringUtils.unicodeFriendly(CHEESE_WEDGE)),
        6);

    testExpectedIndexIsCorrect(
        HELLO_CHEESE_WEDGE_TEARS.codePointIndexOf(StringUtils.unicodeFriendly(FACE_WITH_TEARS_OF_JOY)), 7);

    // test a string longer than one character
    testExpectedIndexIsCorrect(HELLO_CHEESE_WEDGE.codePointIndexOf(StringUtils.unicodeFriendly("ello")), 1);
    testExpectedIndexIsCorrect(HELLO_CHEESE_WEDGE_TEARS.codePointIndexOf(CHEESE_WEDGE_AND_TEARS),
        6);

    // the above, with BMP transitions
    // test a string longer than one character starting in BMP, ending in non-BMP
    testExpectedIndexIsCorrect(
        HELLO_CHEESE_WEDGE_TEARS.codePointIndexOf(StringUtils.unicodeFriendly("o " + CHEESE_WEDGE)), 4);
    testExpectedIndexIsCorrect(HELLO_CHEESE_WEDGE_TEARS
        .codePointIndexOf(StringUtils.unicodeFriendly("o " + CHEESE_WEDGE_AND_TEARS_CODEUNITS)), 4);
    testExpectedIndexIsCorrect(
        HELLO_CHEESE_WEDGE.codePointIndexOf(StringUtils.unicodeFriendly("o " + CHEESE_WEDGE)), 4);
    // test a string longer than one character starting in non-BMP, ending in BMP
    testExpectedIndexIsCorrect(
        HELLO_CHEESE_WEDGE.codePointIndexOf(StringUtils.unicodeFriendly(CHEESE_WEDGE + "!")), 6);
    testExpectedIndexIsCorrect(HELLO_CHEESE_WEDGE_TEARS
        .codePointIndexOf(StringUtils.unicodeFriendly(CHEESE_WEDGE_AND_TEARS_CODEUNITS + "!")), 6);
    // test a string BMP -> nonBMP -> BMP
    testExpectedIndexIsCorrect(HELLO_CHEESE_WEDGE_TEARS
        .codePointIndexOf(StringUtils.unicodeFriendly(" " + CHEESE_WEDGE_AND_TEARS_CODEUNITS + "!")), 5);
    testExpectedIndexIsCorrect(
        HELLO_CHEESE_WEDGE.codePointIndexOf(StringUtils.unicodeFriendly(" " + CHEESE_WEDGE + "!")), 5);
    // test a string nonBMP -> BMP -> nonBMP
    final UnicodeFriendlyString nonBMPTransitions =
        StringUtils.unicodeFriendly(multipleTearsOfJoy.utf16CodeUnits() + "__" + CHEESE_WEDGE);
    testExpectedIndexIsCorrect(nonBMPTransitions
        .codePointIndexOf(StringUtils.unicodeFriendly(FACE_WITH_TEARS_OF_JOY + "__" + CHEESE_WEDGE)), 1);
    // test entirely in BMP
    testExpectedIndexIsCorrect(multipleTearsOfJoy.codePointIndexOf(multipleTearsOfJoy), 0);

    // test for multiple instances
    testExpectedIndexIsCorrect(multipleTearsOfJoy
        .codePointIndexOf(StringUtils.unicodeFriendly(FACE_WITH_TEARS_OF_JOY), CharOffset.asCharOffset(1)), 1);

    // test for input index/offset starting before target string
    testExpectedIndexIsCorrect(
        HELLO_WORLD.codePointIndexOf(StringUtils.unicodeFriendly("world"), CharOffset.asCharOffset(2)), 6);
    testExpectedIndexIsCorrect(HELLO_CHEESE_WEDGE
        .codePointIndexOf(StringUtils.unicodeFriendly(CHEESE_WEDGE), CharOffset.asCharOffset(3)), 6);
    testExpectedIndexIsCorrect(HELLO_CHEESE_WEDGE_TEARS
        .codePointIndexOf(StringUtils.unicodeFriendly(FACE_WITH_TEARS_OF_JOY), CharOffset.asCharOffset(3)), 7);
    testExpectedIndexIsCorrect(HELLO_CHEESE_WEDGE_TEARS
        .codePointIndexOf(CHEESE_WEDGE_AND_TEARS, CharOffset.asCharOffset(4)), 6);
    testExpectedIndexIsCorrect(
        HELLO_CHEESE_WEDGE_TEARS.codePointIndexOf(StringUtils.unicodeFriendly("!"), CharOffset.asCharOffset(4)),
        8);
    // also test splitting up a non-bmp pair
    testExpectedIndexIsCorrect(
        HELLO_CHEESE_WEDGE_TEARS.codePointIndexOf(StringUtils.unicodeFriendly("!"), CharOffset.asCharOffset(7)),
        8);

    // test for input index/offset starting at target strung
    testExpectedIndexIsCorrect(
        HELLO_WORLD.codePointIndexOf(StringUtils.unicodeFriendly("world"), CharOffset.asCharOffset(6)), 6);
    testExpectedIndexIsCorrect(HELLO_CHEESE_WEDGE
        .codePointIndexOf(StringUtils.unicodeFriendly(CHEESE_WEDGE), CharOffset.asCharOffset(6)), 6);
    testExpectedIndexIsCorrect(HELLO_CHEESE_WEDGE_TEARS
        .codePointIndexOf(StringUtils.unicodeFriendly(FACE_WITH_TEARS_OF_JOY), CharOffset.asCharOffset(7)), 7);
    testExpectedIndexIsCorrect(HELLO_CHEESE_WEDGE_TEARS
        .codePointIndexOf(CHEESE_WEDGE_AND_TEARS, CharOffset.asCharOffset(6)), 6);
    testExpectedIndexIsCorrect(
        HELLO_CHEESE_WEDGE_TEARS.codePointIndexOf(StringUtils.unicodeFriendly("!"), CharOffset.asCharOffset(8)),
        8);

    // test for input index/offset starting after target string (find nothing)
    assertFalse(HELLO_WORLD.codePointIndexOf(StringUtils.unicodeFriendly("world"), CharOffset.asCharOffset(7))
        .isPresent());
    assertFalse(HELLO_CHEESE_WEDGE
        .codePointIndexOf(StringUtils.unicodeFriendly(CHEESE_WEDGE), CharOffset.asCharOffset(7)).isPresent());
    assertFalse(HELLO_CHEESE_WEDGE_TEARS
        .codePointIndexOf(StringUtils.unicodeFriendly(FACE_WITH_TEARS_OF_JOY), CharOffset.asCharOffset(8))
        .isPresent());
    assertFalse(HELLO_CHEESE_WEDGE_TEARS
        .codePointIndexOf(CHEESE_WEDGE_AND_TEARS, CharOffset.asCharOffset(7)).isPresent());

    // repeat the above examples with the empty string
    testExpectedIndexIsCorrect(EMPTY.codePointIndexOf(EMPTY), 0);
    testExpectedIndexIsCorrect(HELLO_WORLD.codePointIndexOf(EMPTY), 0);
    testExpectedIndexIsCorrect(HELLO_CHEESE_WEDGE.codePointIndexOf(EMPTY), 0);
    testExpectedIndexIsCorrect(HELLO_CHEESE_WEDGE_TEARS.codePointIndexOf(EMPTY), 0);
    testExpectedIndexIsCorrect(CHEESE_WEDGE_AND_TEARS.codePointIndexOf(EMPTY), 0);
    // and non-initial versions
    testExpectedIndexIsCorrect(HELLO_WORLD.codePointIndexOf(EMPTY, CharOffset.asCharOffset(2)), 2);
    testExpectedIndexIsCorrect(HELLO_CHEESE_WEDGE.codePointIndexOf(EMPTY, CharOffset.asCharOffset(3)), 3);
    testExpectedIndexIsCorrect(HELLO_CHEESE_WEDGE.codePointIndexOf(EMPTY, CharOffset.asCharOffset(6)), 6);
    testExpectedIndexIsCorrect(HELLO_CHEESE_WEDGE_TEARS.codePointIndexOf(EMPTY, CharOffset.asCharOffset(6)), 6);
    testExpectedIndexIsCorrect(CHEESE_WEDGE_AND_TEARS.codePointIndexOf(EMPTY, CharOffset.asCharOffset(1)), 1);
  }

  @Test
  public void testCodePointAt() {
    Assert.assertEquals(FACE_WITH_TEARS_OF_JOY_INT,
        StringUtils.unicodeFriendly(FACE_WITH_TEARS_OF_JOY).codepointAtCodepointIndex(
            CharOffset.asCharOffset(0)));
    assertEquals('H', HELLO_WORLD.codepointAtCodepointIndex(CharOffset.asCharOffset(0)));
    assertEquals('o', HELLO_WORLD.codepointAtCodepointIndex(CharOffset.asCharOffset(4)));
    Assert.assertEquals(CHEESE_WEDGE_INT,
        StringUtils.unicodeFriendly(CHEESE_WEDGE).codepointAtCodepointIndex(CharOffset.asCharOffset(0)));
    assertEquals(FACE_WITH_TEARS_OF_JOY_INT,
        CHEESE_WEDGE_AND_TEARS.codepointAtCodepointIndex(CharOffset.asCharOffset(1)));
    assertEquals(CHEESE_WEDGE_INT, HELLO_CHEESE_WEDGE.codepointAtCodepointIndex(
        CharOffset.asCharOffset(6)));
    assertEquals(FACE_WITH_TEARS_OF_JOY_INT,
        HELLO_CHEESE_WEDGE_TEARS.codepointAtCodepointIndex(CharOffset.asCharOffset(7)));
  }

  @Test
  public void testCodepointIndexOfCodeUnit() {
    // check that looking up a code point using either code unit of a multiunit character works
    assertEquals(0, HELLO_CHEESE_WEDGE.codepointIndex(UTF16Offset.of(0)).asInt());
    assertEquals(1, HELLO_CHEESE_WEDGE.codepointIndex(UTF16Offset.of(1)).asInt());
    assertEquals(2, HELLO_CHEESE_WEDGE.codepointIndex(UTF16Offset.of(2)).asInt());
    assertEquals(3, HELLO_CHEESE_WEDGE.codepointIndex(UTF16Offset.of(3)).asInt());
    assertEquals(4, HELLO_CHEESE_WEDGE.codepointIndex(UTF16Offset.of(4)).asInt());
    assertEquals(5, HELLO_CHEESE_WEDGE.codepointIndex(UTF16Offset.of(5)).asInt());
    assertEquals(6, HELLO_CHEESE_WEDGE.codepointIndex(UTF16Offset.of(6)).asInt());
    assertEquals(6, HELLO_CHEESE_WEDGE.codepointIndex(UTF16Offset.of(7)).asInt());
    assertEquals(7, HELLO_CHEESE_WEDGE.codepointIndex(UTF16Offset.of(8)).asInt());

    // check codepoint lookup in strings without BMP characters works
    assertEquals(4, HELLO_WORLD.codepointIndex(UTF16Offset.of(4)).asInt());
  }

  private static class SumOfCodePoints
      implements UnicodeFriendlyString.CodePointProcessor<Integer> {

    private int sum = 0;

    @Override
    public void processCodepoint(final UnicodeFriendlyString s, final CharOffset codePointOffset,
        final int codePoint) {
      sum += codePoint;
    }

    @Override
    public Integer getResult() {
      return sum;
    }
  }

  @Test
  public void testCodePointProcessor() {
    final UnicodeFriendlyString allBMP = StringUtils.unicodeFriendly("abc");
    final UnicodeFriendlyString withNonBMP = StringUtils.unicodeFriendly("abc" + FACE_WITH_TEARS_OF_JOY);

    final UnicodeFriendlyString.CodePointProcessor<Integer> sumOfCodePoints1 =
        new SumOfCodePoints();
    allBMP.processCodePoints(sumOfCodePoints1);
    assertEquals(97 + 98 + 99, sumOfCodePoints1.getResult().intValue());

    final UnicodeFriendlyString.CodePointProcessor<Integer> sumOfCodePoints2 =
        new SumOfCodePoints();
    withNonBMP.processCodePoints(sumOfCodePoints2);
    assertEquals(97 + 98 + 99 + 128514, sumOfCodePoints2.getResult().intValue());

    // this tests a crash which was observed in the transliterator
    // there was a bug where for this string three codepoints were processed rather than the
    // correct two
    final UnicodeFriendlyString oldTurkic = StringUtils.unicodeFriendly("\uD803\uDC34\uD803\uDC23");
    final UnicodeFriendlyString.CodePointProcessor<Integer> processor =
        new UnicodeFriendlyString.CodePointProcessor<Integer>() {
          int codepointCount = 0;

          @Override
          public void processCodepoint(final UnicodeFriendlyString s,
              final CharOffset codePointOffset,
              final int codePoint) {
            ++codepointCount;
          }

          @Override
          public Integer getResult() {
            return codepointCount;
          }
        };
    oldTurkic.processCodePoints(processor);
    assertEquals(2, processor.getResult().intValue());
  }
}
