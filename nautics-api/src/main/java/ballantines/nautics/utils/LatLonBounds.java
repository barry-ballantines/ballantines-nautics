package ballantines.nautics.utils;

import ballantines.nautics.units.AngleUtil;
import ballantines.nautics.units.LatLon;

import static ballantines.nautics.units.NauticalUnits.*;

public class LatLonBounds {

  private double northLatitudeBound;
  private double southLatitudeBound;
  private double westLongitudeBound;
  private double eastLongitudeBound;


  public LatLonBounds(LatLon northWestCorner, LatLon southEastCorner) {
    this.northLatitudeBound = northWestCorner.getLatitude().to(ARC_DEGREE).getValue().doubleValue();
    this.southLatitudeBound = southEastCorner.getLatitude().to(ARC_DEGREE).getValue().doubleValue();

    this.westLongitudeBound = northWestCorner.getLongitude().to(ARC_DEGREE).getValue().doubleValue();
    this.eastLongitudeBound = AngleUtil.normalizeToLowerBound(
            southEastCorner.getLongitude().to(ARC_DEGREE).getValue().doubleValue(), westLongitudeBound);

  }

  public boolean contains(LatLon pos) {
    double latitude = pos.getLatitude().to(ARC_DEGREE).getValue().doubleValue();
    double longitude = AngleUtil.normalizeToLowerBound(pos.getLongitude().to(ARC_DEGREE).getValue().doubleValue(), westLongitudeBound);
    return westLongitudeBound <= longitude
            && eastLongitudeBound >= longitude
            && northLatitudeBound >= latitude
            && southLatitudeBound <= latitude;
  }
}
