package edu.isi.nlp.strings.formatting;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import edu.isi.nlp.strings.offsets.AnnotatedOffsetRange;
import edu.isi.nlp.strings.offsets.CharOffset;
import edu.isi.nlp.strings.offsets.OffsetRange;
import edu.isi.nlp.symbols.Symbol;
import java.util.Map;

public final class XMLStyleTextAnnotations {

  private XMLStyleTextAnnotations() {
    throw new UnsupportedOperationException();
  }

  private static final Symbol DIV = Symbol.from("div");
  private static final Symbol SPAN = Symbol.from("span");

  public static AnnotatedOffsetRange<CharOffset> createDiv(
      final String clazz, final OffsetRange<CharOffset> span) {
    return AnnotatedOffsetRange.create(DIV, span, ImmutableMap.<String, String>of("class", clazz));
  }

  public static AnnotatedOffsetRange<CharOffset> createSpan(
      final String clazz, final OffsetRange<CharOffset> span) {
    return AnnotatedOffsetRange.create(SPAN, span, ImmutableMap.<String, String>of("class", clazz));
  }

  public static AnnotatedOffsetRange<CharOffset> createSpanWithID(
      final String clazz, final String id, final OffsetRange<CharOffset> span) {
    return AnnotatedOffsetRange.create(
        SPAN, span, ImmutableMap.<String, String>of("class", clazz, "id", id));
  }

  public static AnnotatedOffsetRange<CharOffset> createDivWithID(
      final String clazz, final String id, final OffsetRange<CharOffset> span) {
    checkNotNull(id);
    checkArgument(!id.isEmpty());
    return AnnotatedOffsetRange.create(
        DIV, span, ImmutableMap.<String, String>of("class", clazz, "id", id));
  }

  private static final MapJoiner attributeJoiner = Joiner.on(" ").withKeyValueSeparator("=");

  public static String toStartTag(final AnnotatedOffsetRange<CharOffset> offsetSpan) {
    final StringBuilder ret = new StringBuilder();
    final Map<String, String> wrappedAttributes =
        Maps.transformValues(offsetSpan.attributes(), x -> "\"" + x + "\"");

    ret.append("<").append(offsetSpan.type().toString());
    if (!offsetSpan.attributes().isEmpty()) {
      ret.append(" ");
      ret.append(attributeJoiner.join(wrappedAttributes));
    }
    ret.append(">");
    return ret.toString();
  }

  public static String toEndTag(final AnnotatedOffsetRange<CharOffset> offsetSpan) {
    return String.format("</%s>", offsetSpan.type().toString());
  }

  public static Function<AnnotatedOffsetRange<CharOffset>, String> ToStartTag() {
    return new Function<AnnotatedOffsetRange<CharOffset>, String>() {
      @Override
      public String apply(final AnnotatedOffsetRange<CharOffset> ann) {
        return toStartTag(ann);
      }
    };
  }

  public static Function<AnnotatedOffsetRange<CharOffset>, String> ToEndTag() {
    return new Function<AnnotatedOffsetRange<CharOffset>, String>() {
      @Override
      public String apply(final AnnotatedOffsetRange<CharOffset> ann) {
        return toEndTag(ann);
      }
    };
  }
}
