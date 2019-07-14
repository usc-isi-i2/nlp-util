package edu.isi.nlp;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import edu.isi.nlp.parameters.Parameters;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.manager.LocalRepositoryManager;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.slf4j.LoggerFactory;

// this doesn't belong here, but I needed a spot to stick an emergency evaluation hack
public final class ExportGraphDB {
  public static void main(String[] argv) throws IOException {
    Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    root.setLevel(Level.INFO);

    final Parameters params = Parameters.loadSerifStyle(new File(argv[0]));
    final File outputFile = params.getCreatableFile("outputFile");
    final LocalRepositoryManager localRepositoryManager =
        new LocalRepositoryManager(params.getExistingDirectory("graphDbBaseDir"));
    final Repository repository =
        localRepositoryManager.getRepository(params.getString("repositoryId"));

    try (RepositoryConnection connection = repository.getConnection()) {
      try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
        final RDFWriter writer = Rio.createWriter(RDFFormat.NTRIPLES, outputStream);
        connection.exportStatements(null, null, null, false, writer);
      }
    }
  }
}
