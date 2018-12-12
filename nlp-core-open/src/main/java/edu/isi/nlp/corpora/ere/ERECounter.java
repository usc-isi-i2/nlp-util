package edu.isi.nlp.corpora.ere;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import edu.isi.nlp.StringUtils;
import edu.isi.nlp.files.FileUtils;
import edu.isi.nlp.symbols.Symbol;
import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Counts the number of occurrences of various structures in LDC ERE data. */
public final class ERECounter {
  private static final Logger log = LoggerFactory.getLogger(ERECounter.class);

  private ERECounter() {
    throw new UnsupportedOperationException();
  }

  public static void main(String[] argv) {
    // we wrap the main method in this way to
    // ensure a non-zero return value on failure
    try {
      trueMain(argv);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  private static void trueMain(String[] argv) throws IOException {
    final File ereMap = new File(argv[0]);

    final ImmutableMap<Symbol, File> docIdToFileMap = FileUtils.loadSymbolToFileMap(ereMap);
    final ERELoader ereLoader = ERELoader.builder().build();

    final Multiset<String> eventMentionCountsByType = HashMultiset.create();

    for (final File ereFile : docIdToFileMap.values()) {
      for (final EREEvent docEvent : ereLoader.loadFrom(ereFile).getEvents()) {
        for (final EREEventMention eventMention : docEvent.getEventMentions()) {
          eventMentionCountsByType.add(eventMention.getType() + "." + eventMention.getSubtype());
        }
      }
    }

    log.info(
        "ERE event mention counts by type:\n{}",
        StringUtils.unixNewlineJoiner()
            .join(Multisets.copyHighestCountFirst(eventMentionCountsByType).entrySet()));
  }
}
