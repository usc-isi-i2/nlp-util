package edu.isi.nlp.parameters;

import static edu.isi.nlp.parameters.Parameters.builder;
import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import edu.isi.nlp.parameters.exceptions.ParameterConversionException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests some of {@link Parameters}. As these tests were developed long after the fact, they do not
 * have full coverage.
 */
public final class ParametersTest {
  @Rule public final ExpectedException exception = ExpectedException.none();

  @Test
  public void testFromMap() {
    final ImmutableMap<String, String> map = ImmutableMap.of("a", "1", "b", "", "c.d", "2");
    // Basic parameter
    Assert.assertEquals("1", Parameters.fromMap(map).getString("a"));
    // Empty parameter
    Assert.assertEquals("", Parameters.fromMap(map).getString("b"));
    // Namespace specified by list
    Assert.assertEquals("", Parameters.fromMap(map, ImmutableList.of("")).namespace());
    Assert.assertEquals(
        ImmutableList.of(), Parameters.fromMap(map, ImmutableList.<String>of()).namespaceAsList());
    Assert.assertEquals("foo", Parameters.fromMap(map, ImmutableList.of("foo")).namespace());
    Assert.assertEquals(
        ImmutableList.of("foo"),
        Parameters.fromMap(map, ImmutableList.of("foo")).namespaceAsList());
    Assert.assertEquals(
        "foo.bar", Parameters.fromMap(map, ImmutableList.of("foo", "bar")).namespace());
    Assert.assertEquals(
        ImmutableList.of("foo", "bar"),
        Parameters.fromMap(map, ImmutableList.of("foo", "bar")).namespaceAsList());
  }

  @Test
  public void testSplitNamespace() {
    assertEquals(ImmutableList.of(), Parameters.splitNamespace(""));
    assertEquals(ImmutableList.of("foo"), Parameters.splitNamespace("foo"));
    assertEquals(ImmutableList.of("foo", "bar"), Parameters.splitNamespace("foo.bar"));
  }

  @Test
  public void testJoinNamespace() {
    assertEquals("", Parameters.joinNamespace(ImmutableList.of("")));
    assertEquals("foo", Parameters.joinNamespace(ImmutableList.of("foo")));
    assertEquals("foo.bar", Parameters.joinNamespace(ImmutableList.of("foo", "bar")));
    assertEquals("foo.bar.baz", Parameters.joinNamespace(ImmutableList.of("foo.bar", "baz")));

    assertEquals("", Parameters.joinNamespace(""));
    assertEquals("foo", Parameters.joinNamespace("foo"));
    assertEquals("foo.bar", Parameters.joinNamespace("foo", "bar"));
    assertEquals("foo.bar.baz", Parameters.joinNamespace("foo.bar", "baz"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testJoinNamespaceEndsWithPeriod() {
    Parameters.joinNamespace(ImmutableList.of("foo.bar.", "baz"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testJoinNamespaceStarsWithPeriod() {
    Parameters.joinNamespace(ImmutableList.of("foo.bar", ".baz"));
  }

  @Test
  public void testBuilder() {
    final ImmutableMap<String, String> map = ImmutableMap.of("a", "1", "b", "2");
    // Empty namespace
    Assert.assertEquals("1", Parameters.builder().putAll(map).build().getString("a"));
    Assert.assertEquals("1", Parameters.fromMap(map).modifiedCopyBuilder().build().getString("a"));
    // With namespace
    Assert.assertEquals(
        "1", Parameters.builder(ImmutableList.of("foo")).putAll(map).build().getString("a"));
    Assert.assertEquals(
        "1",
        Parameters.fromMap(map, ImmutableList.of("foo"))
            .modifiedCopyBuilder()
            .build()
            .getString("a"));
    // Check namespace
    Assert.assertEquals("", Parameters.builder().putAll(map).build().namespace());
    Assert.assertEquals("", Parameters.fromMap(map).modifiedCopyBuilder().build().namespace());
    Assert.assertEquals(
        "foo", Parameters.builder(ImmutableList.of("foo")).putAll(map).build().namespace());
    Assert.assertEquals(
        "foo",
        Parameters.fromMap(map, ImmutableList.of("foo")).modifiedCopyBuilder().build().namespace());
    Assert.assertEquals(
        "foo.bar",
        Parameters.builder(ImmutableList.of("foo", "bar")).putAll(map).build().namespace());
    Assert.assertEquals(
        "foo.bar",
        Parameters.fromMap(map, ImmutableList.of("foo", "bar"))
            .modifiedCopyBuilder()
            .build()
            .namespace());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBuilderEmptyKey() {
    final Parameters.Builder builder = Parameters.builder();
    builder.set("", "bar");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBuilderWhitespaceKey() {
    final Parameters.Builder builder = Parameters.builder();
    builder.set("fo o", "bar");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBuilderOnlyWhitespaceKey() {
    final Parameters.Builder builder = Parameters.builder();
    builder.set(" ", "bar");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBuilderEmptyValue() {
    final Parameters.Builder builder = Parameters.builder();
    builder.set("foo", "");
  }

  @Test
  public void testBuilderWhitespaceValue() {
    final Parameters.Builder builder = Parameters.builder();
    builder.set("foo", " ba r ");
    // Leading and trailing whitespace should be trimmed
    assertEquals("ba r", builder.build().getString("foo"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBuilderOnlyWhitespaceValue() {
    final Parameters.Builder builder = Parameters.builder();
    builder.set("foo", " ");
  }

  @Test
  public void testStringList() {
    Assert.assertEquals(
        ImmutableList.of(), Parameters.fromMap(ImmutableMap.of("list", "")).getStringList("list"));
    Assert.assertEquals(
        ImmutableList.of("a"),
        Parameters.fromMap(ImmutableMap.of("list", "a")).getStringList("list"));
    Assert.assertEquals(
        ImmutableList.of("a", "b"),
        Parameters.fromMap(ImmutableMap.of("list", "a,b")).getStringList("list"));
  }

  @Test
  public void testIntegerList() {
    Assert.assertEquals(
        ImmutableList.of(1),
        Parameters.fromMap(ImmutableMap.of("list", "1")).getIntegerList("list"));
    Assert.assertEquals(
        ImmutableList.of(1, 2),
        Parameters.fromMap(ImmutableMap.of("list", "1,2")).getIntegerList("list"));
    exception.expect(ParameterConversionException.class);
    Parameters.fromMap(ImmutableMap.of("list", "a,b")).getIntegerList("list");
  }

  @Test
  public void testBooleanList() {
    Assert.assertEquals(
        ImmutableList.of(true),
        Parameters.fromMap(ImmutableMap.of("list", "true")).getBooleanList("list"));
    Assert.assertEquals(
        ImmutableList.of(true, false),
        Parameters.fromMap(ImmutableMap.of("list", "true,false")).getBooleanList("list"));
    exception.expect(ParameterConversionException.class);
    Parameters.fromMap(ImmutableMap.of("list", "a,b")).getBooleanList("list");
  }
}
