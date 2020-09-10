package ballantines.nautics.routing.app.features;

import ballantines.nautics.routing.IsochronesRouting;
import ballantines.nautics.routing.app.Border;
import ballantines.nautics.routing.app.Config;
import ballantines.nautics.routing.app.Input;
import ballantines.nautics.routing.app.parsers.GPXParser;
import ballantines.nautics.routing.filter.CrossingBorderLegFilter;
import ballantines.nautics.units.LatLon;

import java.util.List;

public class ForbiddenBorderConfigurationFeature implements Feature {

  private List<Border> borders;

  @Override
  public void prepare(Config config, Input in) {
    borders = config.getBorders();
  }

  @Override
  public void postPrepare(Input in) {
    if (!borders.isEmpty()) {
      in.println("Borders:");
      borders.stream().filter(b -> b.isEnabled()).forEach(border -> {
        in.printf(" - name : %s %n", border.getName());
        if (border.getGpx() != null) {
          in.printf("   gpx :  %s %n", border.getGpx().toString());
        } else {
          in.printf("   locations: %n");
          border.getLocations().stream().forEach(loc -> in.printf("              %s %n", loc));
        }
      });
    }
  }

  @Override
  public void decorate(IsochronesRouting routing) {
    borders.stream().filter(b -> b.isEnabled()).forEach(b -> {
      CrossingBorderLegFilter filter = new CrossingBorderLegFilter();
      List<LatLon> latlons =  (b.getGpx()==null)
              ? b.getLatLons()
              : new GPXParser(b.getGpx()).parse();
      filter.setBorder(latlons);
      routing.addLegFilter(filter);
    });
  }
}
