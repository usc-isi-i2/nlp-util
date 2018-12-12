package edu.isi.nlp.corpora.eventNugget;

import com.google.common.collect.ImmutableList;
import edu.isi.nlp.IsiNlpImmutable;
import org.immutables.func.Functional;
import org.immutables.value.Value;

/** @author Yee Seng Chan */
@Value.Immutable(prehash = true)
@Functional
@IsiNlpImmutable
public abstract class NuggetDocument {

  @Value.Parameter
  public abstract String kitId();

  @Value.Parameter
  public abstract String docId();

  @Value.Parameter
  public abstract SourceType sourceType();

  @Value.Parameter
  public abstract ImmutableList<NuggetHopper> hoppers();

  public enum SourceType {
    newswire,
    multi_post
  }

  public static class Builder extends ImmutableNuggetDocument.Builder {}
}
