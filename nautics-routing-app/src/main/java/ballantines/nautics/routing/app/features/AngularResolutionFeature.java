package ballantines.nautics.routing.app.features;

import ballantines.nautics.routing.IsochronesRouting;
import ballantines.nautics.routing.app.Config;
import ballantines.nautics.routing.app.Input;

public class AngularResolutionFeature implements Feature {

  private int candidatesResolution;
  private int isochronesResolution;

  @Override
  public void prepare(Config config, Input in) {
    candidatesResolution = config.getCandidatesAngularResolution();
    isochronesResolution = config.getIsochronesAngularResolution();
  }

  @Override
  public void postPrepare(Input in) {
    in.printf("candidates angular resolution   : %d° %n", candidatesResolution);
    in.printf("isochrones angular resolution   : %d° %n", isochronesResolution);
    in.println();
  }

  @Override
  public void decorate(IsochronesRouting routing) {
    routing.setCandidatesAngularResolution(candidatesResolution);
    routing.setIsochronesAngularResolution(isochronesResolution);
  }
}
