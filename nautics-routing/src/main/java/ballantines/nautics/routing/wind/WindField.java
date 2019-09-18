/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ballantines.nautics.routing.wind;

import ballantines.nautics.units.LatLon;
import ballantines.nautics.units.PolarVector;

import javax.measure.quantity.Speed;
import java.util.Date;

/**
 * Represents a wind field.
 *
 * This interface represents a very simple interface for weather routing. Implementations might use GRIB-files to get the
 * data.
 *
 * @author barry
 */
public interface WindField {

  /**
   * returns true, if this wind field supports the given position and time.
   *
   * @param position
   * @param time
   * @return true, if there is wind for the given position and time.
   */
  boolean supports(LatLon position, Date time);

  /**
   * Returns a wind vector for the given position and time
   *
   * Note: The angle of the wind vector is pointing towards the wind direction. A wind vector (0°, 5kn) represents a wind
   * of 5kn speed coming from north.
   *
   * @param position the position
   * @param time the date and time
   * @return the wind vector
   */
  PolarVector<Speed> getWind(LatLon position, Date time);

}
