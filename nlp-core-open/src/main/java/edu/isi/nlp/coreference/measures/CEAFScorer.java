package edu.isi.nlp.coreference.measures;

import com.google.common.annotations.Beta;
import edu.isi.nlp.evaluation.FMeasureInfo;

@Beta
public interface CEAFScorer {

  public FMeasureInfo score(
      final Iterable<? extends Iterable<?>> predicted, final Iterable<? extends Iterable<?>> gold);
}
