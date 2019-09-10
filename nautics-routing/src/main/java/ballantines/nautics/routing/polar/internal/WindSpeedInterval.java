package ballantines.nautics.routing.polar.internal;

import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Speed;

/**
 * Interface for calculating the boat speed for a well defined true wind speed interval.
 * @author barry
 */
public interface WindSpeedInterval {

  /** the given wind speed is in the given interval **/
  boolean matches(Quantity<Speed> trueWindSpeed);

  /** the boat speed for a given twa and tws **/
  Quantity<Speed> getVelocity(Quantity<Speed> trueWindSpeed, Quantity<Angle> trueWindAngle);
  
  Quantity<Speed> getLowerTWS();
  
  Quantity<Speed> getUpperTWS();
  
}
