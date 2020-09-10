package ballantines.nautics.routing.app.features;

import ballantines.nautics.routing.IsochronesRouting;
import ballantines.nautics.routing.app.Config;
import ballantines.nautics.routing.app.Input;
import ballantines.nautics.routing.polar.Polar;
import ballantines.nautics.routing.polar.PolarParser;

import java.io.File;
import java.io.IOException;

public class PolarConfigurationFeature implements Feature {

  private PolarParser polarParser = new PolarParser();

  private File polarFile;
  private Polar polar;

  @Override
  public void prepare(Config config, Input in) {

    try {
      polarFile = config.getPolarFile().orElseGet(in.readFile(
              "Polar diagram file    : ", true));
      polar = polarParser.parsePolar(polarFile);
    } catch (IOException e) {
      throw new RuntimeException("Cannot parse Polar file.", e);
    }
  }


  @Override
  public void postPrepare(Input in) {
    in.printf("Polar file   : %s%n", polarFile);
    in.println();
  }

  @Override
  public void decorate(IsochronesRouting routing) {
    routing.setPolar(polar);
  }
}
