package edu.isi.nlp.evaluation;

import com.google.common.annotations.Beta;
import edu.isi.nlp.symbols.Symbol;

/**
 * Anything that has an associated {@link Symbol} type that can be used for the purposes of scoring.
 */
@Beta
public interface HasScoringType {
  Symbol scoringType();
}
