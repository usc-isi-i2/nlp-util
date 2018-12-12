package edu.isi.nlp.evaluation;

import com.google.common.base.Function;
import edu.isi.nlp.symbols.Symbol;
import java.util.Set;

/**
 * Represents a confusion matrix which only tracks counts. Asking for {@code #cell(x,y)} will give
 * you the number of times an object was assigned the label {@code x} by the "left system" but
 * {@code y} by the "right system". By convention, when comparing against a gold standard, the gold
 * standard is the "right system".
 *
 * <p>To build one of these, see {@link SummaryConfusionMatrices#builder()}.
 *
 * <p>If you also want to track exactly what objects were confused instead of just the counts, you
 * want a {@link ProvenancedConfusionMatrix}.
 */
public interface SummaryConfusionMatrix {

  double cell(final Symbol row, final Symbol col);
  /** The left-hand labels of the confusion matrix. */
  Set<Symbol> leftLabels();
  /** The right hand labels of the confusion matrix. */
  Set<Symbol> rightLabels();

  double sumOfallCells();

  double rowSum(Symbol row);

  double columnSum(Symbol column);

  SummaryConfusionMatrix filteredCopy(CellFilter filter);

  SummaryConfusionMatrix copyWithTransformedLabels(Function<Symbol, Symbol> f);

  void accumulateTo(SummaryConfusionMatrices.Builder builder);

  interface CellFilter {
    boolean keepCell(Symbol row, Symbol column);
  }
}
