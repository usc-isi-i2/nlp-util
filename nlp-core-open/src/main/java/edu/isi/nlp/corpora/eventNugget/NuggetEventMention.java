package edu.isi.nlp.corpora.eventNugget;

import edu.isi.nlp.IsiNlpImmutable;
import edu.isi.nlp.corpora.ere.ERESpan;
import edu.isi.nlp.symbols.Symbol;
import org.immutables.func.Functional;
import org.immutables.value.Value;

/**
 * @author Yee Seng Chan
 */
@Value.Immutable(prehash = true)
@Functional
@IsiNlpImmutable
public abstract class NuggetEventMention {

  @Value.Parameter
  public abstract String id();

  @Value.Parameter
  public abstract Symbol type();

  @Value.Parameter
  public abstract Symbol subtype();

  @Value.Parameter
  public abstract Symbol realis();

  @Value.Parameter
  public abstract ERESpan trigger();

  public static class Builder extends ImmutableNuggetEventMention.Builder {

  }
}
