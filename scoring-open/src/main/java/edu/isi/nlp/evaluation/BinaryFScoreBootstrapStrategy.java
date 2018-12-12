package edu.isi.nlp.evaluation;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.util.Collection;
import java.util.Map;

/** Prefer {@link BinaryConfusionMatrixBootstrapStrategy} with a {@link BrokenDownPRFAggregator}. */
@Beta
@Deprecated
public final class BinaryFScoreBootstrapStrategy<T>
    implements BootstrapInspector.BootstrapStrategy<
        Alignment<? extends T, ? extends T>, Map<String, SummaryConfusionMatrix>> {

  private final File outputDir;
  private final String name;
  private final Function<? super T, String> breakdownScheme;

  private BinaryFScoreBootstrapStrategy(
      String name, Function<? super T, String> breakdownFunction, final File outputDir) {
    this.name = checkNotNull(name);
    this.outputDir = checkNotNull(outputDir);
    this.breakdownScheme = checkNotNull(breakdownFunction);
  }

  public static <T> BinaryFScoreBootstrapStrategy<T> createBrokenDownBy(
      final String name, final Function<? super T, String> breakdownFunction, File outputDir) {
    return new BinaryFScoreBootstrapStrategy<T>(name, breakdownFunction, outputDir);
  }

  public static <T> BinaryFScoreBootstrapStrategy<T> create(final String name, File outputDir) {
    return new BinaryFScoreBootstrapStrategy<T>(name, Functions.constant("Present"), outputDir);
  }

  @Override
  public BootstrapInspector.ObservationSummarizer<
          Alignment<? extends T, ? extends T>, Map<String, SummaryConfusionMatrix>>
      createObservationSummarizer() {
    return new BootstrapInspector.ObservationSummarizer<
        Alignment<? extends T, ? extends T>, Map<String, SummaryConfusionMatrix>>() {
      @Override
      public Map<String, SummaryConfusionMatrix> summarizeObservation(
          final Alignment<? extends T, ? extends T> alignment) {
        final ImmutableMap<String, Alignment<T, T>> alignmentsByKeys =
            Alignments.splitAlignmentByKeyFunction(alignment, breakdownScheme);
        final ImmutableMap.Builder<String, SummaryConfusionMatrix> ret = ImmutableMap.builder();
        for (final Map.Entry<String, Alignment<T, T>> e : alignmentsByKeys.entrySet()) {
          ret.put(e.getKey(), confusionMatrixForAlignment(e.getValue()));
        }
        return ret.build();
      }
    };
  }

  private SummaryConfusionMatrix confusionMatrixForAlignment(
      final Alignment<? extends T, ? extends T> alignment) {
    final SummaryConfusionMatrices.Builder summaryConfusionMatrixB =
        SummaryConfusionMatrices.builder();
    summaryConfusionMatrixB.accumulatePredictedGold(
        EvaluationConstants.PRESENT, EvaluationConstants.PRESENT, alignment.rightAligned().size());
    summaryConfusionMatrixB.accumulatePredictedGold(
        EvaluationConstants.ABSENT, EvaluationConstants.PRESENT, alignment.leftUnaligned().size());
    summaryConfusionMatrixB.accumulatePredictedGold(
        EvaluationConstants.PRESENT, EvaluationConstants.ABSENT, alignment.rightUnaligned().size());
    return summaryConfusionMatrixB.build();
  }

  @Override
  public Collection<BootstrapInspector.SummaryAggregator<Map<String, SummaryConfusionMatrix>>>
      createSummaryAggregators() {
    return ImmutableList.of(prfAggregator());
  }

  public BootstrapInspector.SummaryAggregator<Map<String, SummaryConfusionMatrix>> prfAggregator() {
    return BrokenDownPRFAggregator.create(name, outputDir);
  }
}
