package edu.isi.nlp.collections;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.Lists;
import java.util.List;
import org.junit.Test;

public class ListUtilsTest {

  @Test
  public void testListConcat() {
    final List<String> list1 = Lists.newArrayList("Hello", "world");
    final List<String> list2 = Lists.newArrayList("foo", "bar", "baz");
    final List<String> combined = ListUtils.concat(list1, list2);

    assertEquals(Lists.newArrayList("Hello", "world", "foo", "bar", "baz"), combined);
    list1.add("new element");
    assertEquals(
        Lists.newArrayList("Hello", "world", "new element", "foo", "bar", "baz"), combined);
  }
}
