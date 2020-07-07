package ballantines.nautics.routing.export;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public interface Export {

  void to(PrintWriter out);

  default void to(File outputFile) {
    try (PrintWriter writer = new PrintWriter(outputFile, "UTF-8")) {
      to(writer);
      writer.flush();
    } catch(IOException ex) {
      System.err.println("Failed to write to file: " + outputFile);
      System.err.println(ex);
    }
  }
}
