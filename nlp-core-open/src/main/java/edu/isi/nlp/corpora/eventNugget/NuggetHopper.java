package edu.isi.nlp.corpora.eventNugget;

import com.google.common.collect.ImmutableList;
import edu.isi.nlp.IsiNlpImmutable;
import org.immutables.func.Functional;
import org.immutables.value.Value;

@Value.Immutable(prehash = true)
@Functional
@IsiNlpImmutable
public abstract class NuggetHopper {

  @Value.Parameter
  public abstract String id();

  @Value.Parameter
  public abstract ImmutableList<NuggetEventMention> eventMentions();

  public static class Builder extends ImmutableNuggetHopper.Builder {

  }
}
