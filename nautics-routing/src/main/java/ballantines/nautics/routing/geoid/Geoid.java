package ballantines.nautics.routing.geoid;

import ballantines.nautics.units.LatLon;
import ballantines.nautics.units.PolarVector;

import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Length;

/**
 * This interface represents the Geoid and allows Orthodromic Great-Circle Calculations on a Geoid.
 */
public interface Geoid {
    /**
     * Calculates the initial bearing and distance of the orthodrome from the given start to the given end position on
     * this geoid
     *
     * @param start The starting point of the orthodrome
     * @param end   The endpoint of the orthodrome
     * @return a polar vector, representing the orthodrome
     */
  PolarVector<Length> calculateOrthodromicDistanceAndBearing(LatLon start, LatLon end);

    /**
     * Calculates the destination of an orthodrome, starting at the start position with a given initial bearing and
     * distance.
     *
     * @param start The start point
     * @param bearing The initial bearing (in a range of ]-180°, 180°]
     * @param distance the distance.
     * @return the calculated destination.
     */
  LatLon calculateDestination(LatLon start, Quantity<Angle> bearing, Quantity<Length> distance);

}
