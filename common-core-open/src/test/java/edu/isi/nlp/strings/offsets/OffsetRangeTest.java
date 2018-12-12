package edu.isi.nlp.strings.offsets;

import static edu.isi.nlp.strings.offsets.OffsetRange.charOffsetRange;
import static junit.framework.TestCase.assertEquals;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import java.util.List;
import org.junit.Test;

public class OffsetRangeTest {

  private static final List<OffsetRange<CharOffset>> input =
      ImmutableList.of(
          charOffsetRange(42, 54),
          charOffsetRange(42, 50),
          charOffsetRange(42, 60),
          charOffsetRange(30, 70),
          charOffsetRange(43, 44));

  @Test
  public void testEarlyEarly() {
    final List<OffsetRange<CharOffset>> ref =
        ImmutableList.of(
            charOffsetRange(30, 70),
            charOffsetRange(42, 50),
            charOffsetRange(42, 54),
            charOffsetRange(42, 60),
            charOffsetRange(43, 44));

    final Ordering<OffsetRange<CharOffset>> testOrdering =
        OffsetRange.byEarlierStartEarlierEndOrdering();
    assertEquals(ref, testOrdering.sortedCopy(input));
  }

  @Test
  public void testEarlyLate() {
    final List<OffsetRange<CharOffset>> ref =
        ImmutableList.of(
            charOffsetRange(30, 70),
            charOffsetRange(42, 60),
            charOffsetRange(42, 54),
            charOffsetRange(42, 50),
            charOffsetRange(43, 44));

    final Ordering<OffsetRange<CharOffset>> testOrdering =
        OffsetRange.byEarlierStartLaterEndOrdering();
    assertEquals(ref, testOrdering.sortedCopy(input));
  }
}
