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
    double x = end.getLongitude().subtract(start.getLongitude()).to(NauticalUnits.ARC_DEGREE).getValue().doubleValue();
    double y = end.getLatitude().subtract(start.getLatitude()).to(NauticalUnits.ARC_DEGREE).getValue().doubleValue();
    return new Segment(x, y);
  }
}
