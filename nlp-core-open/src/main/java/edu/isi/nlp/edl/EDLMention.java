package edu.isi.nlp.edl;

import static com.google.common.base.Preconditions.checkArgument;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import edu.isi.nlp.IsiNlpImmutable;
import edu.isi.nlp.evaluation.ScoringTypedOffsetRange;
import edu.isi.nlp.strings.offsets.CharOffset;
import edu.isi.nlp.strings.offsets.OffsetRange;
import edu.isi.nlp.symbols.Symbol;
import org.immutables.func.Functional;
import org.immutables.value.Value;

/**
 * Represents a mention for the Entity Detection and Linking scorer to the degree necessary for the
 * Lorelei NER evaluations. Does not yet address KB linking.
 */
@IsiNlpImmutable
@Value.Immutable
@Functional
@JsonSerialize
@JsonDeserialize
public abstract class EDLMention {
  public abstract Symbol runId();

  public abstract String mentionId();

  public abstract Symbol documentID();

  public abstract Optional<String> kbId();

  public abstract String headString();

  public abstract OffsetRange<CharOffset> headOffsets();

  public abstract Symbol mentionType();

  public abstract Symbol entityType();

  public abstract double confidence();

  @Value.Check
  void check() {
    checkArgument(!runId().asString().isEmpty(), "Run ID cannot be empty");
    checkArgument(!mentionId().isEmpty(), "Mention ID cannot be empty");
    checkArgument(!documentID().asString().isEmpty(), "Document ID cannot be empty");
    checkArgument(
        !kbId().isPresent() || !kbId().get().isEmpty(), "KB ID can be absent but cannot be empty");
    checkArgument(!headString().isEmpty(), "Head string cannot be empty");
    checkArgument(!mentionType().asString().isEmpty(), "Mention type cannot be empty");
    checkArgument(!entityType().asString().isEmpty(), "Entity type cannot be empty");
  }

  public static class Builder extends ImmutableEDLMention.Builder {

    /** Sets the KB id to be a NIL cluster with the specified id. */
    public Builder nilKbId(final String id) {
      return this.kbId("NIL" + id);
    }
  }

  /**
   * Guava {@link Function} to transform an {@code EDLMention}s to a {@link ScoringTypedOffsetRange}
   * with offsets corresponding to {@link #headOffsets()} and type corresponding to {@link
   * #entityType()}.
   */
  public static Function<EDLMention, ScoringTypedOffsetRange<CharOffset>>
      asTypedOffsetRangeFunction() {
    return AsTypedOffsetRangeFunction.INSTANCE;
  }

  private enum AsTypedOffsetRangeFunction
      implements Function<EDLMention, ScoringTypedOffsetRange<CharOffset>> {
    INSTANCE {
      @Override
      public ScoringTypedOffsetRange<CharOffset> apply(final EDLMention input) {
        return ScoringTypedOffsetRange.create(
            input.documentID(), input.entityType(), input.headOffsets());
      }
    }
  }
}
