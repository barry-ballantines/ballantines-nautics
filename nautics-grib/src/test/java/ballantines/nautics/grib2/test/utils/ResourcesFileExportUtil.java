package ballantines.nautics.grib2.test.utils;

import java.io.*;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;

public class ResourcesFileExportUtil {

  private static int DEFAULT_BUFFER_SIZE = 8192;

  public static File exportResourceToTmpFile(String resourcePath) throws IOException {
    URI uri = URI.create(resourcePath);
    String filename = Paths.get(uri.getPath()).getFileName().toString();
    String tmp = System.getProperty("java.io.tmpdir");
    File outputFile = new File(tmp, filename);
    exportResourceToFile(resourcePath, outputFile);
    return outputFile;
  }

  public static void exportResourceToFile(String resourcePath, File outputFile) throws IOException {
    InputStream is = ResourcesFileExportUtil.class.getResourceAsStream(resourcePath);
    BufferedInputStream bis = new BufferedInputStream(is);

    FileOutputStream fos = new FileOutputStream(outputFile);
    try {
      ReadableByteChannel rbc = Channels.newChannel(is);
      fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    }
    finally {
      fos.flush();
      fos.close();
    }

  }
}
