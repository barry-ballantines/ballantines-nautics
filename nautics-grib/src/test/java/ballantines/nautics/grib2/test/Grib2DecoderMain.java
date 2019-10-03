package ballantines.nautics.grib2.test;

import ballantines.nautics.grib2.util.ResourcesFileExportUtil;
import com.noaa.grib.Grib2Decoder;

import java.io.File;

public class Grib2DecoderMain {

  public static void main(String... args) throws Throwable {
    File tmpFile = ResourcesFileExportUtil.exportResourceToTmpFile("/grib2/example.grb2");
    System.out.println("Downloading test GRIB file to " + tmpFile);

    Grib2Decoder.main(tmpFile.getPath());

    if (tmpFile.delete()) {
      System.out.println("GRIB File " + tmpFile + " deleted.");
    }
    else {
      System.out.println("GRIB File " + tmpFile + " not deleted.");
    }
  }
}
