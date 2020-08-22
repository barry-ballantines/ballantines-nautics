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
    double coslat2 = cos(lat2);
    double coslat1 = cos(lat1);
    double sinlon12 = sin(lon12);
    double sinlat1 = sin(lat1);
    double sinlat2 = sin(lat2);
    double coslon12 = cos(lon12);

    double numerator = coslat2 * sinlon12;
    double denominator = coslat1 * sinlat2 - sinlat1 * coslat2 * coslon12;
    Quantity<Angle> bearing = atan2(numerator, denominator).to(ARC_DEGREE);

    // distance
    numerator = Math.sqrt(denominator * denominator + numerator * numerator);
    denominator = sinlat1 * sinlat2 + coslat1 * coslat2 * coslon12;
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
    double sinlat1 = sin(lat1);
    double coslat1 = cos(lat1);
    double sinsigma12 = sin(sigma12);
    double cossigma12 = cos(sigma12);
    double sinbearing = sin(bearing);
    double cosbearing = cos(bearing);

    double numerator = sinsigma12 * sinbearing;
    double denominator = coslat1 * cossigma12 - sinlat1 * sinsigma12 * cosbearing;

    Quantity<Angle> lon12 = atan2(numerator, denominator).to(ARC_DEGREE);
    Quantity<Angle> lon2 = AngleUtil.normalizeToUpperBound(lon1.add(lon12), AngleUtil.DEGREE_180);

    // lat2 = latitude of endpoint
    denominator = Math.sqrt(denominator * denominator + numerator * numerator);
    numerator = sinlat1 * cossigma12 + coslat1 * sinsigma12 * cosbearing;

    Quantity<Angle> lat2 = atan2(numerator, denominator).to(ARC_DEGREE);
    return new LatLon(lat2, lon2);
  }
}
