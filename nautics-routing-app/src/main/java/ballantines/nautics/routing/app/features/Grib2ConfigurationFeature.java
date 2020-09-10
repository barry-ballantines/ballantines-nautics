package ballantines.nautics.routing.app.features;

import ballantines.nautics.routing.IsochronesRouting;
import ballantines.nautics.routing.app.Config;
import ballantines.nautics.routing.app.Input;
import ballantines.nautics.routing.filter.LatLonBoxFilter;
import ballantines.nautics.routing.wind.Grib2WindField;
import ballantines.nautics.utils.LatLonBounds;

import java.io.File;
import java.io.IOException;

public class Grib2ConfigurationFeature implements Feature {

  private Grib2WindField windfield;
  private File grib2File;
  private LatLonBounds boundaryBox;

  @Override
  public void prepare(Config config, Input in) {
    try {
      grib2File = config.getGrib2File().orElseGet(in.readFile(
            "GRIB2 weather file: ", true));
      windfield = new Grib2WindField(grib2File);
      boundaryBox = config.getBoundaryBox()!=null ? config.getBoundaryBox().toLatLonBounds() : windfield.getBounds();
      in.println();
    } catch (IOException e) {
      throw new RuntimeException("Failed to create Windfield from GRIB2 file.", e);
    }
  }

  @Override
  public void postPrepare(Input in) {
    in.printf("GRIB2 file   : %s%n", grib2File);
    in.printf("Boundary Box : %s%n", boundaryBox);
    in.println();
  }

  @Override
  public void decorate(IsochronesRouting routing) {
    routing.setWindfield(windfield);
    routing.addLegFilter(new LatLonBoxFilter(boundaryBox));

  }
}
