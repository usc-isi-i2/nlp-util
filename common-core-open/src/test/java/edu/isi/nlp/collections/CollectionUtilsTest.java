package edu.isi.nlp.collections;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * Tests the CollectionUtils class.
 */
public final class CollectionUtilsTest {

  @Test
  public void testPartition() {
    Assert.assertEquals(
        ImmutableList.of(
            ImmutableList.of(1, 2)),
        CollectionUtils.partitionAlmostEvenly(ImmutableList.of(1, 2), 1));
    Assert.assertEquals(
        ImmutableList.of(
            ImmutableList.of(1),
            ImmutableList.of(2)),
        CollectionUtils.partitionAlmostEvenly(ImmutableList.of(1, 2), 2));
    Assert.assertEquals(
        ImmutableList.of(
            ImmutableList.of(1, 3),
            ImmutableList.of(2)),
        CollectionUtils.partitionAlmostEvenly(ImmutableList.of(1, 2, 3), 2));
    Assert.assertEquals(
        ImmutableList.of(
            ImmutableList.of(1, 2),
            ImmutableList.of(3, 4)),
        CollectionUtils.partitionAlmostEvenly(ImmutableList.of(1, 2, 3, 4), 2));
    Assert.assertEquals(
        ImmutableList.of(
            ImmutableList.of(1, 4),
            ImmutableList.of(2),
            ImmutableList.of(3)),
        CollectionUtils.partitionAlmostEvenly(ImmutableList.of(1, 2, 3, 4), 3));
    Assert.assertEquals(
        ImmutableList.of(
            ImmutableList.of(1, 2, 3, 10),
            ImmutableList.of(4, 5, 6),
            ImmutableList.of(7, 8, 9)),
        CollectionUtils.partitionAlmostEvenly(ImmutableList.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), 3));
    // Test case where there is more than one extra preliminary partition
    Assert.assertEquals(
        ImmutableList.of(
            ImmutableList.of(1, 4),
            ImmutableList.of(2, 5),
            ImmutableList.of(3)),
        CollectionUtils.partitionAlmostEvenly(ImmutableList.of(1, 2, 3, 4, 5), 3));
  }

  @Test(expected = NullPointerException.class)
  public void testPartitionArgsCheck1() {
    CollectionUtils.partitionAlmostEvenly(null, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPartitionArgsCheck2() {
    CollectionUtils.partitionAlmostEvenly(ImmutableList.of(), 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPartitionArgsCheck3() {
    CollectionUtils.partitionAlmostEvenly(ImmutableList.of(1), 2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPartitionArgsCheck4() {
    CollectionUtils.partitionAlmostEvenly(ImmutableList.of(1), 0);
  }

  private final ImmutableSet<String> left = ImmutableSet.of("hello", "world", "!");
  private final ImmutableList<String> right = ImmutableList.of("this", "is", "a", "list");
  private final ImmutableList<String> reference = ImmutableList.of("hello", "world", "!",
      "this", "is", "a", "list");

  @Test
  public void testConcat() {
    final Collection<String> concatted = CollectionUtils.concat(left, right);
    assertEquals(7, concatted.size());
    for (int i = 0; i < reference.size(); ++i) {
      assertEquals(reference.get(i), Iterables.get(concatted, i));
    }
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testConcatOutOfBounds() {
    final Collection<String> concatted = CollectionUtils.concat(left, right);
    Iterables.get(concatted, reference.size());
  }
}
