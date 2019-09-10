package ballantines.nautics.routing.polar.internal;

import ballantines.nautics.routing.polar.DefaultPolar;
import ballantines.nautics.units.PolarVector;
import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Speed;

/**
 * An AngularSector based on linear interpolation.
 * 
 * @author barry
 */
public class InterpolatingAngularSector implements AngularSector {
  
  PolarVector<Speed> lowerBoatVelocity;
  PolarVector<Speed> upperBoatVelocity;
  private Quantity<Angle> deltaAngle;
  private Quantity<Speed> deltaSpeed;

  public InterpolatingAngularSector(PolarVector<Speed> lowerBoatVelocity, PolarVector<Speed> upperBoatVelocity) {
    this.lowerBoatVelocity = lowerBoatVelocity.to(DefaultPolar.BOAT_SPEED_UNIT, DefaultPolar.ANGLE_UNIT);
    this.upperBoatVelocity = upperBoatVelocity.to(DefaultPolar.BOAT_SPEED_UNIT, DefaultPolar.ANGLE_UNIT);
    deltaAngle = upperBoatVelocity.getAngle().subtract(lowerBoatVelocity.getAngle());
    deltaSpeed = upperBoatVelocity.getRadial().subtract(lowerBoatVelocity.getRadial());
  }

  @Override
  public boolean matches(Quantity<Angle> angle) {
    double angleValue = angle.to(DefaultPolar.ANGLE_UNIT).getValue().doubleValue();
    return angleValue >= lowerBoatVelocity.getAngle().getValue().doubleValue() 
        && angleValue <= upperBoatVelocity.getAngle().getValue().doubleValue();
  }

  @Override
  public Quantity<Speed> getVelocity(Quantity<Angle> angle) {
    Quantity<Angle> angleDistanceFromStart = angle.subtract(lowerBoatVelocity.getAngle());
    double factor = angleDistanceFromStart.divide(deltaAngle).getValue().doubleValue();
    Quantity<Speed> result = lowerBoatVelocity.getRadial().add(deltaSpeed.multiply(factor));
    return result;
  }
  
}
