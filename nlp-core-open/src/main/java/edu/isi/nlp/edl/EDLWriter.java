package edu.isi.nlp.edl;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.io.CharSink;
import edu.isi.nlp.IsiNlpImmutable;
import java.io.IOException;
import java.util.List;
import org.immutables.value.Value;

/**
 * Writes files in the submission format for the TAC KBP Entity Detection and Linking eval.
 */
@IsiNlpImmutable
@Value.Immutable
public abstract class EDLWriter {

  /**
   * A default KB ID to assign to {@link EDLMention}s with absent KB IDs.
   */
  public abstract Optional<String> defaultKbId();

  /**
   * Writes out the specified {@link EDLMention}s. If a {@link #defaultKbId} was not specified,
   * an exception will be thrown if any EDL mentions have an absent KB ID.
   */
  public void writeEDLMentions(Iterable<EDLMention> edlMentions, CharSink sink) throws IOException {
    final List<String> lines = Lists.newArrayList();

    for (final EDLMention edlMention : edlMentions) {
      lines.add(toLine(edlMention));
    }

    sink.writeLines(lines, "\n");
  }

  public static class Builder extends ImmutableEDLWriter.Builder {}

  private String toLine(final EDLMention edlMention) {
    final Optional<String> kbId = edlMention.kbId().or(defaultKbId());
    checkArgument(kbId.isPresent(),
        "EDL mention %s does not have a KB ID and no default was specified", kbId);
    return edlMention.runId() + "\t" + edlMention.mentionId() + "\t" + edlMention.headString()
        + "\t" + edlMention.documentID() + ":" + edlMention.headOffsets().startInclusive().asInt()
        + "-" + edlMention.headOffsets().endInclusive().asInt() + "\t" + kbId.get() + "\t"
        + edlMention.entityType() + "\t" + edlMention.mentionType() + "\t"
        + edlMention.confidence();
  }
}
