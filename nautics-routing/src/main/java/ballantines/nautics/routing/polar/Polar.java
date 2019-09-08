package ballantines.nautics.routing.polar;

import ballantines.nautics.units.PolarVector;
import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Speed;

/**
 * A polar diagram for a boat
 * @author barry
 */
public interface Polar {
  
  /**
   * Get the boat velocity for the given true wind speed and true wind angle.
   * 
   * @param trueWindSpeed the true wind speed
   * @param trueWindAngle the angle between the boat heading direction and the direction of the true wind.
   * @return 
   */
  PolarVector<Speed> getVelocity(Quantity<Speed> trueWindSpeed, Quantity<Angle> trueWindAngle);
  
}
