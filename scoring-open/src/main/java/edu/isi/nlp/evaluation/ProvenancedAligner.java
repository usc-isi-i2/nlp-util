package edu.isi.nlp.evaluation;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;

/**
 * Something which can produce a {@link ProvenancedAlignment}. See that class for more details.
 *
 * <p>Note that unlike a regular {@link Aligner}, the resulting alignment may not be over the same
 * type of objects as the input (which will become the provenances).
 */
@Beta
public interface ProvenancedAligner<LeftT, LeftProvT, RightT, RightProvT> {

  ProvenancedAlignment<LeftT, LeftProvT, RightT, RightProvT> align(
      Iterable<? extends LeftProvT> leftItems, Iterable<? extends RightProvT> rightItems);

  Function<
          EvalPair<
              ? extends Iterable<? extends LeftProvT>, ? extends Iterable<? extends RightProvT>>,
          ProvenancedAlignment<LeftT, LeftProvT, RightT, RightProvT>>
      asFunction();
}
