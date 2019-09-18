package ballantines.nautics.routing.filter;

import ballantines.nautics.Leg;
import ballantines.nautics.units.LatLon;
import ballantines.nautics.utils.LatLonBounds;


public class LatitudeBoxFilter implements LegFilter {

  private LatLonBounds bounds;

  public LatitudeBoxFilter(LatLon northWestCorner, LatLon southEastCorner) {
    this.bounds = new LatLonBounds(northWestCorner, southEastCorner);
  }

  @Override
  public boolean accept(Leg leg) {
    return bounds.contains(leg.endpoint);
  }
}
