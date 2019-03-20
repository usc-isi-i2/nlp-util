package edu.isi.nlp.corpora;

import static junit.framework.TestCase.assertEquals;

import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import com.google.common.io.Resources;
import org.junit.Test;

public class TestLtf {
  @Test
  public void testLtfLoading() {
    final LctlText loadedLctlText =
        (new LtfReader())
            .read(
                Resources.asCharSource(
                    this.getClass().getResource("test_ltf.xml"), Charsets.UTF_8));

    assertEquals(1, loadedLctlText.getDocuments().size());
    final LtfDocument ltfDocument = Iterables.getOnlyElement(loadedLctlText.getDocuments());
    assertEquals(2, ltfDocument.getSegments().size());
    assertEquals(
        "Hello, world.\nThis is an LTF file.\n",
        ltfDocument.getOriginalText().content().utf16CodeUnits());
    // TODO: test tokens, etc.
  }
}
