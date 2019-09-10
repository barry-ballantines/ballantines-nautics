package ballantines.nautics.routing.polar.internal;

import ballantines.nautics.routing.polar.DefaultPolar;
import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Speed;

/**
 * A Wind Speed Interval based on linear interpolation of the boat speed.
 * 
 * The boat speed is calculated by linear interpolation based on the speeds calculated from a
 * lower and an upper SingleWindSpeedPolar for a given twa angle.
 * 
 * @author barry
 */
public class InterpolatingWindSpeedInterval implements WindSpeedInterval {
  
  SingleWindSpeedPolar lowerTWAPolar;
  SingleWindSpeedPolar upperTWAPolar;
  Quantity<Speed> delta;

  public InterpolatingWindSpeedInterval(SingleWindSpeedPolar lower, SingleWindSpeedPolar upper) {
    this.lowerTWAPolar = lower;
    this.upperTWAPolar = upper;
    this.delta = upper.getTrueWindSpeed().subtract(this.lowerTWAPolar.getTrueWindSpeed());
  }

  @Override
  public boolean matches(Quantity<Speed> trueWindSpeed) {
    double twa = trueWindSpeed.to(DefaultPolar.WIND_SPEED_UNIT).getValue().doubleValue();
    return twa >= lowerTWAPolar.getTrueWindSpeed().to(DefaultPolar.WIND_SPEED_UNIT).getValue().doubleValue() 
        && twa <= upperTWAPolar.getTrueWindSpeed().to(DefaultPolar.WIND_SPEED_UNIT).getValue().doubleValue();
  }

  @Override
  public Quantity<Speed> getVelocity(Quantity<Speed> trueWindSpeed, Quantity<Angle> trueWindAngle) {
    Quantity<Speed> speedDeltaFromStart = trueWindSpeed.subtract(lowerTWAPolar.getTrueWindSpeed());
    double factor = speedDeltaFromStart.divide(delta).getValue().doubleValue();
    Quantity<Speed> startSpeed = lowerTWAPolar.getVelocity(trueWindAngle);
    Quantity<Speed> endSpeed = upperTWAPolar.getVelocity(trueWindAngle);
    Quantity<Speed> deltaBoatSpeed = endSpeed.subtract(startSpeed);
    Quantity<Speed> interpolatedBoatSpeed = startSpeed.add(deltaBoatSpeed.multiply(factor));
    return interpolatedBoatSpeed;
  }

  @Override
  public Quantity<Speed> getLowerTWS() {
    return lowerTWAPolar.getTrueWindSpeed();
  }
  
  @Override
  public Quantity<Speed> getUpperTWS() {
    return upperTWAPolar.getTrueWindSpeed();
  }
  
  
  
}
