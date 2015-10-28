package com.bbn.bue.common.evaluation;

import com.bbn.bue.common.OptionalUtils;
import com.bbn.bue.common.collections.MapUtils;
import com.bbn.bue.common.math.PercentileComputer;
import com.bbn.bue.common.symbols.Symbol;

import com.google.common.annotations.Beta;
import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.common.primitives.Doubles;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A bootstrap sampling strategy for precision, recall, F-score, and accuracy.  This also supports
 * breaking down the output based on the results of some provided function.
 *
 * An item is consider a true positive if it has an alignment in the key.  Each unaligned test item
 * is considered to be a false positive and each unaligned key item is considered to be a false
 * negative.
 *
 * If a breakdown function is specified, separate results will be produced for each output of that
 * function where the alignment is filtered to include only items with the same value from the
 * function (e.g. to break down results by event type).
 */
@Beta
public final class BinaryFScoreBootstrapStrategy<T>
    implements
    BootstrapInspector.BootstrapStrategy<Alignment<? extends T, ? extends T>, Map<String, SummaryConfusionMatrix>> {

  private final File outputDir;
  private final String name;
  private final Function<? super T, String> breakdownScheme;

  private BinaryFScoreBootstrapStrategy(String name, Function<? super T, String> breakdownFunction,
      final File outputDir) {
    this.name = checkNotNull(name);
    this.outputDir = checkNotNull(outputDir);
    this.breakdownScheme = checkNotNull(breakdownFunction);
  }

  public static <T> BinaryFScoreBootstrapStrategy<T> createBrokenDownBy(
      final String name, final Function<? super T, String> breakdownFunction, File outputDir) {
    return new BinaryFScoreBootstrapStrategy<T>(name, breakdownFunction, outputDir);
  }

  public static <T> BinaryFScoreBootstrapStrategy<T> create(
      final String name, File outputDir) {
    return new BinaryFScoreBootstrapStrategy<T>(name, Functions.constant("Present"), outputDir);
  }

  private static final Symbol PRESENT = Symbol.from("Present");
  private static final Symbol ABSENT = Symbol.from("Absent");

  @Override
  public BootstrapInspector.ObservationSummarizer<Alignment<? extends T, ? extends T>, Map<String, SummaryConfusionMatrix>> createObservationSummarizer() {
    return new BootstrapInspector.ObservationSummarizer<Alignment<? extends T, ? extends T>, Map<String, SummaryConfusionMatrix>>() {
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
    summaryConfusionMatrixB
        .accumulatePredictedGold(PRESENT, PRESENT, alignment.rightAligned().size());
    summaryConfusionMatrixB
        .accumulatePredictedGold(PRESENT, ABSENT, alignment.leftUnaligned().size());
    summaryConfusionMatrixB
        .accumulatePredictedGold(ABSENT, PRESENT, alignment.rightUnaligned().size());
    return summaryConfusionMatrixB.build();
  }

  @Override
  public Collection<BootstrapInspector.SummaryAggregator<Map<String, SummaryConfusionMatrix>>> createSummaryAggregators() {
    return ImmutableList.of(prfAggregator());
  }

  private static final ImmutableList<Double> PERCENTILES_TO_PRINT =
      ImmutableList.of(0.01, 0.05, 0.25, 0.5, 0.75, 0.95, 0.99);

  public BootstrapInspector.SummaryAggregator<Map<String, SummaryConfusionMatrix>> prfAggregator() {
    return new BootstrapInspector.SummaryAggregator<Map<String, SummaryConfusionMatrix>>() {
      final ImmutableListMultimap.Builder<String, Double> f1sB = ImmutableListMultimap.builder();
      final ImmutableListMultimap.Builder<String, Double> precisionsB =
          ImmutableListMultimap.builder();
      final ImmutableListMultimap.Builder<String, Double> recallsB =
          ImmutableListMultimap.builder();
      final ImmutableListMultimap.Builder<String, Double> accuraciesB =
          ImmutableListMultimap.builder();

      final PercentileComputer percentileComputer = PercentileComputer.nistPercentileComputer();

      @Override
      public void observeSample(
          final Collection<Map<String, SummaryConfusionMatrix>> observationSummaries) {
        final Map<String, SummaryConfusionMatrices.Builder> keysToAggregateMatrices =
            Maps.newHashMap();
        for (final String key : MapUtils.allKeys(observationSummaries)) {
          keysToAggregateMatrices.put(key, SummaryConfusionMatrices.builder());
        }

        for (final Map<String, SummaryConfusionMatrix> observationSummary : observationSummaries) {
          for (final Map.Entry<String, SummaryConfusionMatrix> entry : observationSummary
              .entrySet()) {
            keysToAggregateMatrices.get(entry.getKey()).accumulate(entry.getValue());
          }
        }

        for (final Map.Entry<String, SummaryConfusionMatrices.Builder> aggregate : keysToAggregateMatrices
            .entrySet()) {
          final SummaryConfusionMatrix matrix = aggregate.getValue().build();
          accuraciesB.put(aggregate.getKey(), SummaryConfusionMatrices.accuracy(matrix));
          final FMeasureCounts fMeasureInfo =
              SummaryConfusionMatrices.FMeasureVsAllOthers(matrix, PRESENT);
          f1sB.put(aggregate.getKey(), (double) fMeasureInfo.F1());
          precisionsB.put(aggregate.getKey(), (double) fMeasureInfo.precision());
          recallsB.put(aggregate.getKey(), (double) fMeasureInfo.recall());
        }
      }

      @Override
      public void finish() throws IOException {
        final StringBuilder chart = new StringBuilder();
        final ImmutableListMultimap<String, Double> f1s = f1sB.build();
        final ImmutableListMultimap<String, Double> precisions = precisionsB.build();
        final ImmutableListMultimap<String, Double> recalls = recallsB.build();
        final ImmutableListMultimap<String, Double> accuracies = accuraciesB.build();

        chart.append(name).append("\n\n");

        // all four multimaps have the same keyset
        for (final String key : f1s.keySet()) {
          dumpPercentilesForMetric(key,
              ImmutableMap.of(
                  "F1", percentileComputer
                  .calculatePercentilesAdoptingData(Doubles.toArray(f1s.get(key))),
                  "Prec",
                  percentileComputer
                      .calculatePercentilesAdoptingData(Doubles.toArray(precisions.get(key))),
                  "Rec",
                  percentileComputer
                      .calculatePercentilesAdoptingData(Doubles.toArray(recalls.get(key))),
                  "Acc",
                  percentileComputer
                      .calculatePercentilesAdoptingData(Doubles.toArray(accuracies.get(key)))),
              chart);
          chart.append("\n\n");
        }

        outputDir.mkdir();
        Files.asCharSink(new File(outputDir, name + ".bootstrapped.txt"),
            Charsets.UTF_8).write(chart.toString());
      }

      private void dumpPercentilesForMetric(String chartTitle,
          ImmutableMap<String, PercentileComputer.Percentiles> percentilesByRow,
          StringBuilder output) {
        output.append(chartTitle).append("\n\n");
        output.append(renderLine("Name", PERCENTILES_TO_PRINT));
        output.append(Strings.repeat("*", 70)).append("\n");
        for (final Map.Entry<String, PercentileComputer.Percentiles> percentileEntry : percentilesByRow
            .entrySet()) {
          output.append(renderLine(percentileEntry.getKey(),
              Lists.transform(percentileEntry.getValue().percentiles(PERCENTILES_TO_PRINT),
                  OptionalUtils.deoptionalizeFunction(Double.NaN))));
        }
        output.append("\n\n\n");
      }

      private String renderLine(String name, List<Double> values) {
        final StringBuilder ret = new StringBuilder();

        ret.append(String.format("%20s", name));
        for (double val : values) {
          ret.append(String.format("%15.2f", 100.0 * val));
        }
        ret.append("\n");
        return ret.toString();
      }
    };
  }

}