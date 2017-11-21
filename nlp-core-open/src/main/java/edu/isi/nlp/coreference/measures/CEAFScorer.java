package edu.isi.nlp.coreference.measures;

import edu.isi.nlp.evaluation.FMeasureInfo;

import com.google.common.annotations.Beta;

@Beta
public interface CEAFScorer {

  public FMeasureInfo score(final Iterable<? extends Iterable<?>> predicted, final Iterable<? extends Iterable<?>> gold);
}
