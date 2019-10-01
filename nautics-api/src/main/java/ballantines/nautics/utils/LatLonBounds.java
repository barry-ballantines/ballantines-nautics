package ballantines.nautics.utils;

import ballantines.nautics.units.AngleUtil;
import ballantines.nautics.units.LatLon;
import ballantines.nautics.units.NauticalUnits;

import javax.measure.Quantity;
import javax.measure.quantity.Angle;

import static ballantines.nautics.units.NauticalUnits.*;

public class LatLonBounds {

  private double northLatitudeBound;
  private double southLatitudeBound;
  private double westLongitudeBound;
  private double eastLongitudeBound;

  public static LatLonBounds fromNorthWestToSouthEast(LatLon northWestCorner, LatLon southEastCorner) {
    return new LatLonBounds(lat(southEastCorner), lat(northWestCorner), lon(northWestCorner), lon(southEastCorner));
  }

  public static LatLonBounds fromSouthWestToNorthEast(LatLon southWestCorner, LatLon northEastCorner) {
    return new LatLonBounds(lat(southWestCorner), lat(northEastCorner), lon(southWestCorner), lon(northEastCorner));
  }

  private static double lat(LatLon pos) {
    return pos.getLatitude().to(ARC_DEGREE).getValue().doubleValue();
  }
  private static double lon(LatLon pos) {
    return pos.getLongitude().to(ARC_DEGREE).getValue().doubleValue();
  }

  public LatLonBounds(double south, double north, double west, double east) {
    this.northLatitudeBound = north;
    this.southLatitudeBound = south;
    this.westLongitudeBound = west;
    this.eastLongitudeBound = AngleUtil.normalizeToLowerBound(east, west);
  }

  public boolean contains(LatLon pos) {
    double latitude = pos.getLatitude().to(ARC_DEGREE).getValue().doubleValue();
    double longitude = AngleUtil.normalizeToLowerBound(pos.getLongitude().to(ARC_DEGREE).getValue().doubleValue(), westLongitudeBound);
    return westLongitudeBound <= longitude
            && eastLongitudeBound >= longitude
            && northLatitudeBound >= latitude
            && southLatitudeBound <= latitude;
  }

  public Quantity<Angle> getNorthLatitudeBound() {
    return NauticalUnits.degrees(northLatitudeBound);
  }

  public Quantity<Angle> getSouthLatitudeBound() {
    return NauticalUnits.degrees(southLatitudeBound);
  }

  public Quantity<Angle> getWestLongitudeBound() {
    return NauticalUnits.degrees(westLongitudeBound);
  }

  public Quantity<Angle> getEastLongitudeBound() {
    return NauticalUnits.degrees(eastLongitudeBound);
  }

  public String toString() {
    return "Bounds(Lat:[" + getSouthLatitudeBound() + " - " + getNorthLatitudeBound()
            + "], Lon:[" + getWestLongitudeBound() + " - " + getEastLongitudeBound() + "])";
  }
}
