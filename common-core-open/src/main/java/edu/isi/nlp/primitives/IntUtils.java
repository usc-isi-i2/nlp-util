package edu.isi.nlp.primitives;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * Utility methods for working with primitive integers.
 *
 * @author Ryan Gabbard
 */
public final class IntUtils {

  private IntUtils() {
    throw new UnsupportedOperationException();
  }


  /**
   * Fisher-Yates suffer a primitive int array
   */
  public static void shuffle(final int[] arr, final Random rng) {
    // Fisher-Yates shuffle
    for (int i = arr.length; i > 1; i--) {
      // swap i-1 and a random spot
      final int a = i - 1;
      final int b = rng.nextInt(i);

      final int tmp = arr[b];
      arr[b] = arr[a];
      arr[a] = tmp;
    }
  }

  /**
   * Produces an array of integers from 0 (inclusive) to len (exclusive)
   */
  public static int[] arange(final int len) {
    checkArgument(len > 0);
    final int[] ret = new int[len];
    for (int i = 0; i < len; ++i) {
      ret[i] = i;
    }
    return ret;
  }


  public static void writeTo(final int[] arr, final DataOutputStream out) throws IOException {
    out.writeInt(arr.length);
    for (final int x : arr) {
      out.writeInt(x);
    }
  }

  public static int[] readIntegerArrayFrom(final DataInputStream in) throws IOException {
    final int size = in.readInt();
    final int[] ret = new int[size];
    for (int i = 0; i < size; ++i) {
      ret[i] = in.readInt();
    }
    return ret;
  }

  /**
   * Note: this does not warn about overflow beyond the range of a long.
   */
  public static Long sum(Iterable<Integer> values) {
    long ret = 0;
    for (final int x : values) {
      ret += x;
    }
    return ret;
  }


}
