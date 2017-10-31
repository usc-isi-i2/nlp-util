package com.bbn.bue.common.evaluation;

import com.bbn.bue.common.math.PercentileComputer;
import com.bbn.bue.common.serialization.jackson.JacksonSerializer;
import com.bbn.bue.common.serialization.jackson.JacksonTestUtils;
import java.io.IOException;
import org.junit.Test;

public class TestBootstrapWriter {

  @Test
  public void testBootstrapResultSerialization() throws IOException {
    BootstrapWriter.SerializedBootstrapResults results = new
        BootstrapWriter.SerializedBootstrapResults.Builder()
        .putPercentilesMap("foo",
            PercentileComputer.nistPercentileComputer()
                .calculatePercentilesCopyingData(new double[]{1.0, 2.0, 3.0, 4.0}))
        .putPercentilesMap("bar",
            PercentileComputer.nistPercentileComputer()
                .calculatePercentilesCopyingData(new double[]{1.0, 2.0, 3.0, 4.0}))
        .putRawSamples("foo", 1.0)
        .putRawSamples("foo", 2.0).build();
    JacksonTestUtils.roundTripThroughSerializer(results, JacksonSerializer.forNormalJSON());
  }
}
