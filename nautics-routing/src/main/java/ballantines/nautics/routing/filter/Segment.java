package ballantines.nautics.routing.filter;

import ballantines.nautics.units.LatLon;
import ballantines.nautics.units.NauticalUnits;

public class Segment {
  private double x;
  private double y;

  public Segment(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double crossProduct(Segment that) {
    return this.y * that.x - this.x * that.y;
  }

  public static Segment of(LatLon start, LatLon end) {
    double deltaLon  = end.getLongitude().subtract(start.getLongitude()).to(NauticalUnits.ARC_DEGREE).getValue().doubleValue();
    double deltaLat = end.getLatitude().subtract(start.getLatitude()).to(NauticalUnits.ARC_DEGREE).getValue().doubleValue();

    deltaLon = correctLongitude(deltaLon);
    return new Segment(deltaLon, deltaLat);
  }

  /* correct longitudes if x > 180° or x < -180°. We are crossing the E 180° meridian! */
  private static double correctLongitude(double lon) {
    if (lon > 180.) {
      return lon - 360.;
    }
    if (lon < -180.) {
      return lon + 360.;
    }
    return lon;
  }
}
