package ballantines.nautics.routing.filter;

import ballantines.nautics.routing.Leg;
import ballantines.nautics.units.LatLon;

import java.util.List;

public class CrossingBorderLegFilter implements LegFilter {

  private List<LatLon> border;

  @Override
  public boolean accept(Leg leg) {
    LatLon start = null;
    LatLon end = null;
    for (LatLon point : border) {
      start = end;
      end = point;
      if (start==null) continue;
      if (isIntercepting(leg.parent.endpoint, leg.endpoint, start, end)) {
        return false;
      }
    }
    return true;
  }

  private boolean isIntercepting(LatLon a1, LatLon a2, LatLon b1, LatLon b2) {
    Segment a1a2 = Segment.of(a1, a2);
    Segment a2b1 = Segment.of(a2, b1);
    Segment a2b2 = Segment.of(a2, b2);

    boolean axb = (a1a2.crossProduct(a2b1) * a1a2.crossProduct(a2b2)) <= 0;

    if (axb) {
      Segment b1b2 = Segment.of(b1, b2);
      Segment b2a1 = Segment.of(b2, a1);
      Segment b2a2 = Segment.of(b2, a2);

      boolean bxa = (b1b2.crossProduct(b2a1) * b1b2.crossProduct(b2a2)) <= 0;

      return bxa;
    }
    return false;
  }

  public void setBorder(List<LatLon> border) {
    this.border = border;
  }
}
