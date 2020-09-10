package ballantines.nautics.routing.app.features;

import ballantines.nautics.routing.IsochronesRouting;
import ballantines.nautics.routing.app.Bounds;
import ballantines.nautics.routing.app.Config;
import ballantines.nautics.routing.app.Input;
import ballantines.nautics.routing.filter.LatLonBoxFilter;

import java.util.List;

public class ForbiddenAreasConfigurationFeature implements Feature {

  private List<Bounds> forbiddenAreas;

  @Override
  public void prepare(Config config, Input in) {
    forbiddenAreas = config.getForbiddenAreas();
  }

  @Override
  public void postPrepare(Input in) {
    if (!forbiddenAreas.isEmpty()) {
      System.out.println("Forbidden areas:");
      forbiddenAreas.stream().forEach(area -> in.println(" - " + area));
      in.println();
    }
  }

  @Override
  public void decorate(IsochronesRouting routing) {
    forbiddenAreas.stream().filter(b -> b.isEnabled()).forEach(b -> {
      LatLonBoxFilter filter = new LatLonBoxFilter(b.toLatLonBounds());
      routing.addLegFilter(filter.inverse());
    });
  }
}
