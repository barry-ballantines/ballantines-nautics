package ballantines.nautics.routing.polar.internal;

import ballantines.nautics.routing.polar.DefaultPolar;
import ballantines.nautics.units.PolarVector;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Speed;

/**
 * A Polar diagram for a single tws wind speed.
 * 
 * @author barry
 */
public class SingleWindSpeedPolar {
  
  private List<AngularSector> angularSectors = new LinkedList<>();
  private Quantity<Speed> trueWindSpeed;
  private PolarVector<Speed> lastEntry = null;

  public SingleWindSpeedPolar(Quantity<Speed> trueWindSpeed) {
    this.trueWindSpeed = trueWindSpeed.to(DefaultPolar.WIND_SPEED_UNIT);
  }

  public Quantity<Speed> getVelocity(Quantity<Angle> angle) {
    Optional<AngularSector> optionalSector = angularSectors.stream().filter((sector) -> sector.matches(angle)).findFirst();
    if (optionalSector.isPresent()) {
      return optionalSector.get().getVelocity(angle);
    } else {
      return null;
    }
  }

  public SingleWindSpeedPolar add(PolarVector<Speed> newBoatSpeed) {
    if (lastEntry == null) {
      lastEntry = newBoatSpeed.to(DefaultPolar.BOAT_SPEED_UNIT, DefaultPolar.ANGLE_UNIT);
      return this;
    }
    newBoatSpeed = newBoatSpeed.to(DefaultPolar.BOAT_SPEED_UNIT, DefaultPolar.ANGLE_UNIT);
    if (newBoatSpeed.getAngle().getValue().doubleValue() <= lastEntry.getAngle().getValue().doubleValue()) {
      throw new IllegalArgumentException("Invalid entry: values must be added with increasing angles!");
    }
    angularSectors.add(new InterpolatingAngularSector(lastEntry, newBoatSpeed));
    lastEntry = newBoatSpeed;
    return this;
  }

  public Quantity<Speed> getTrueWindSpeed() {
    return trueWindSpeed;
  }
  
}
