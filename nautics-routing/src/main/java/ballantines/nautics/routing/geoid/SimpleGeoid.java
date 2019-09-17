package ballantines.nautics.routing.geoid;

import ballantines.nautics.units.AngleUtil;
import ballantines.nautics.units.LatLon;
import ballantines.nautics.units.NauticalUnits;
import ballantines.nautics.units.PolarVector;
import tec.units.ri.quantity.Quantities;
import tec.units.ri.unit.Units;

import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Length;

import static ballantines.nautics.units.AngleUtil.cos;
import static ballantines.nautics.units.AngleUtil.sin;
import static ballantines.nautics.units.AngleUtil.atan2;
import static ballantines.nautics.units.NauticalUnits.*;

/**
 * A simple spherical geoid
 *
 * The geoid uses nautical miles for its internal calculations so it is independent of the Earth radius.
 * A nautical mile is defined as the length of an angle of 1' at the equator.
 *
 * @link https://en.wikipedia.org/wiki/Great-circle_navigation
 */
public class SimpleGeoid implements Geoid {

  @Override
  public PolarVector<Length> calculateOrthodromicDistanceAndBearing(LatLon start, LatLon end) {
    // bearing
    Quantity<Angle> lat1 = start.getLatitude();
    Quantity<Angle> lat2 = end.getLatitude();
    Quantity<Angle> lon12 = AngleUtil.delta(start.getLongitude(), end.getLongitude());
    double numerator = cos(lat2) * sin(lon12);
    double denominator = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(lon12);
    Quantity<Angle> bearing = atan2(numerator, denominator).to(ARC_DEGREE);

    // distance
    numerator = Math.sqrt(denominator * denominator + numerator * numerator);
    denominator = sin(lat1) * sin(lat2) + cos(lat1) * cos(lat2) * cos(lon12);
    Quantity<Angle> distAngle = atan2(numerator, denominator);

    Quantity<Length> distance = nauticalMiles(distAngle.to(ARC_MINUTE).getValue().doubleValue());

    return new PolarVector(distance, bearing);
  }

  @Override
  public LatLon calculateDestination(LatLon start, Quantity<Angle> bearing, Quantity<Length> distance) {
    Quantity<Angle> lat1 = start.getLatitude();
    Quantity<Angle> lon1 = start.getLongitude();

    // sigma12 = distance as angle
    Quantity<Angle> sigma12 = degrees(0., distance.to(NAUTICAL_MILE).getValue().doubleValue());

    // lon12 = difference destination.lon - start.lon
    double numerator = sin(sigma12) * sin(bearing);
    double denominator = cos(lat1) * cos(sigma12) - sin(lat1) * sin(sigma12) * cos(bearing);
    Quantity<Angle> lon12 = atan2(numerator, denominator).to(ARC_DEGREE);
    Quantity<Angle> lon2 = AngleUtil.normalizeToUpperBound(lon1.add(lon12), AngleUtil.DEGREE_180);

    // lat2 = latitude of endpoint
    denominator = Math.sqrt(denominator * denominator + numerator * numerator);
    numerator = sin(lat1) * cos(sigma12) + cos(lat1) * sin(sigma12) * cos(bearing);
    Quantity<Angle> lat2 = atan2(numerator, denominator).to(ARC_DEGREE);
    return new LatLon(lat2, lon2);
  }
}
