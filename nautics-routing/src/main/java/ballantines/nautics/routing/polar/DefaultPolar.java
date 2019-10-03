package ballantines.nautics.routing.polar;

import ballantines.nautics.routing.polar.internal.SingleWindSpeedPolar;
import ballantines.nautics.routing.polar.internal.InterpolatingWindSpeedInterval;
import ballantines.nautics.routing.polar.internal.WindSpeedInterval;
import ballantines.nautics.units.AngleUtil;
import ballantines.nautics.units.NauticalUnits;
import ballantines.nautics.units.PolarVector;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Speed;
import tec.units.ri.quantity.Quantities;

/**
 * Default implementation for a Polar diagram.
 * 
 * The polar data can be defined as described here:
 * 
 * <pre>
 *   DefaultPolar polar = new DefaultPolar();
 *   polar.newTWA(Quantities.getQuantity(10, KNOT))
 *        .add(PolarVector.create(0, KNOT, 0, ARC_DEGREE)
 *        .add(PolarVector.create(30, KNOT, 4, ARC_DEGREE)
 *        ...
 *        .add(PolarVector.create(10, KNOT, 180, ARC_DEGREE);
 *   polar.newTWA(Quantities.getQuantity(15, KNOT))
 *        .add(PolarVector.create(0, KNOT, 0, ARC_DEGREE)
 *        ...
 *        .add(PolarVector.create(12, KNOT, 180, ARC_DEGREE);
 *   polar.newTWA( nextTWA )
 *        .add(...) // and so on...
 * </pre>
 *
 * @author barry
 */
public class DefaultPolar implements Polar {
  
  public static Unit<Speed> WIND_SPEED_UNIT = NauticalUnits.KNOT;
  public static Unit<Speed> BOAT_SPEED_UNIT = NauticalUnits.KNOT;
  public static Unit<Angle> ANGLE_UNIT = NauticalUnits.ARC_DEGREE;
  
  private List<WindSpeedInterval> windSpeedIntervals = new LinkedList<>();
  
  private SingleWindSpeedPolar lastWindSpeedPolar = null;

  @Override
  public PolarVector<Speed> getVelocity(Quantity<Speed> trueWindSpeed, Quantity<Angle> trueWindAngle) {
    Quantity<Angle> normalizedTWA = normalizeTWA(trueWindAngle);
    Optional<WindSpeedInterval> optionalWSI 
            = windSpeedIntervals.stream().filter((wsi) -> wsi.matches(trueWindSpeed)).findFirst();
    if (optionalWSI.isPresent()) {
      Quantity<Speed> boatSpeed = optionalWSI.get().getVelocity(trueWindSpeed, normalizedTWA);
      return new PolarVector(boatSpeed, trueWindAngle);
    }
    else {
      // wind is stronger as expected. take the last value available from the polar
      WindSpeedInterval maxWindInterval = windSpeedIntervals.get(windSpeedIntervals.size() - 1);
      Quantity<Speed> upperTWS = maxWindInterval.getUpperTWS();
      Quantity<Speed> boatSpeed = maxWindInterval.getVelocity(upperTWS, normalizedTWA);
      return new PolarVector(boatSpeed, trueWindAngle);
    }
  }

  @Override
  public Quantity<Speed> getMaximumTrueWindSpeed() {
    return this.windSpeedIntervals.get(this.windSpeedIntervals.size()-1).getUpperTWS();
  }
  
  
  
  public SingleWindSpeedPolar newTWS(Quantity<Speed> tws) {
    tws = tws.to(WIND_SPEED_UNIT);
    
    if (this.lastWindSpeedPolar==null) {
      this.lastWindSpeedPolar = new SingleWindSpeedPolar(Quantities.getQuantity(0.0, WIND_SPEED_UNIT));
      if (tws.getValue().doubleValue() == 0.0) {
        // lets fill the TWA=0 record...
        return this.lastWindSpeedPolar;
      }
      else {
        // Add missing TWS=0 record...
        this.lastWindSpeedPolar.add(PolarVector.create(0.0, BOAT_SPEED_UNIT, 0.0, ANGLE_UNIT));
        this.lastWindSpeedPolar.add(PolarVector.create(0.0, BOAT_SPEED_UNIT, 180.0, ANGLE_UNIT));
      
      }
    }
    
    if (tws.getValue().doubleValue() <= this.lastWindSpeedPolar.getTrueWindSpeed().getValue().doubleValue()) {
      throw new IllegalArgumentException("True Wind Speed is not larger than previous TWA!");
    }
    
    SingleWindSpeedPolar nextWindSpeedPolar = new SingleWindSpeedPolar(tws);
    WindSpeedInterval interval = new InterpolatingWindSpeedInterval(lastWindSpeedPolar, nextWindSpeedPolar);
    
    this.windSpeedIntervals.add(interval);
    this.lastWindSpeedPolar = nextWindSpeedPolar;
    
    return nextWindSpeedPolar;
  }
  
  private Quantity<Angle> normalizeTWA(Quantity<Angle> twa) {
    double degrees = twa.to(ANGLE_UNIT).getValue().doubleValue();
    if (degrees>=0. && degrees<= 180.) {
      return twa;
    }
    double normalizedDegrees =Math.abs(AngleUtil.normalizeToUpperBound(degrees, 180.));
    return Quantities.getQuantity(normalizedDegrees, ANGLE_UNIT).asType(Angle.class);
    
  }
}
