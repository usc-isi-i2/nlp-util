package edu.isi.nlp.io;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteSource;
import edu.isi.nlp.strings.offsets.OffsetRange;
import edu.isi.nlp.symbols.Symbol;
import java.io.IOException;
import junit.framework.TestCase;

public class OffsetIndicesTest extends TestCase {

  final MapOffsetIndex offsetIndex =
      MapOffsetIndex.fromMap(
          ImmutableMap.of(
              Symbol.from("foo"), OffsetRange.byteOffsetRange(0, 42),
              Symbol.from("bar"), OffsetRange.byteOffsetRange(100, 101)));

  public void testIO() throws IOException {
    final ByteArraySink sink = ByteArraySink.create();

    OffsetIndices.writeBinary(offsetIndex, sink);
    final OffsetIndex reloaded = OffsetIndices.readBinary(ByteSource.wrap(sink.toByteArray()));
    assertEquals(offsetIndex, reloaded);
  }
}
