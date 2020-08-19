package ballantines.nautics.routing.filter;

import ballantines.nautics.routing.Leg;
import ballantines.nautics.utils.LatLonBounds;


public class LatLonBoxFilter implements LegFilter {

  private LatLonBounds bounds;

  public LatLonBoxFilter(LatLonBounds bounds) {
    this.bounds = bounds;
  }

  @Override
  public boolean accept(Leg leg) {
    return bounds.contains(leg.endpoint);
  }

  public LegFilter inverse() {
    return leg -> ! accept(leg);
  }
}
