package ballantines.nautics.routing.filter;

import ballantines.nautics.routing.Leg;
import ballantines.nautics.units.LatLon;
import ballantines.nautics.utils.LatLonBounds;

import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import java.util.List;

public class BoundCrossingBorderLegFilter extends CrossingBorderLegFilter {

  private LatLon nw, ne, sw, se = null;
  private LatLonBounds bounds = null;

  @Override
  public boolean accept(Leg leg) {
    return ignoreLeg(leg) ||  super.accept(leg);
  }

  protected boolean ignoreLeg(Leg leg) {
    return !(bounds.contains(leg.endpoint)
            || bounds.contains(leg.parent.endpoint)
            || isIntercepting(leg.parent.endpoint, leg.endpoint, nw, se)
            || isIntercepting(leg.parent.endpoint, leg.endpoint, ne, sw));
  }

  @Override
  public void setBorder(List<LatLon> border) {
    super.setBorder(border);

    Quantity<Angle> north = null;
    Quantity<Angle> south = null;
    Quantity<Angle> east  = null;
    Quantity<Angle> west  = null;

    for (LatLon pos : border) {
      if (north==null || pos.isNorthOf(north)) north = pos.getLatitude();
      if (south==null || pos.isSouthOf(south)) south = pos.getLatitude();
      if (east==null || pos.isEastOf(east)) east = pos.getLongitude();
      if (west==null || pos.isWestOf(west)) west = pos.getLongitude();
    }

    this.ne = new LatLon(north, east);
    this.nw = new LatLon(north, west);
    this.se = new LatLon(south, east);
    this.sw = new LatLon(south, east);

    this.bounds = LatLonBounds.fromNorthWestToSouthEast(nw, se);
  }
}
