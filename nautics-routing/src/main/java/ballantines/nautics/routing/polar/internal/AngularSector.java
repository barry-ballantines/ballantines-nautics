package ballantines.nautics.routing.polar.internal;

import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Speed;

/** a sector for calculating the boat speeds between 2 fixed angles. 
 *
 * @author barry
 **/
public interface AngularSector {

  /** the given angle is within the given sector **/
  boolean matches(Quantity<Angle> angle);

  Quantity<Speed> getVelocity(Quantity<Angle> angle);
  
}
