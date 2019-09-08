/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ballantines.nautics.routing.polar;

import ballantines.nautics.units.NauticalUnits;
import ballantines.nautics.units.PolarVector;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Speed;

/**
 *
 * @author mbuse
 */
public class DefaultPolar implements Polar {
  
  private static Unit<Speed> SPEED_UNIT = NauticalUnits.KNOT;
  private static Unit<Angle> ANGLE_UNIT = NauticalUnits.ARC_DEGREE;

  @Override
  public PolarVector<Speed> getVelocity(Quantity<Speed> trueWindSpeed, Quantity<Angle> trueWindAngle) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
  
  
  /** a sector for calculating the boat speeds between 2 fixed angles. **/
  public static interface AngularSector {
    /** the given angle is within the given sector **/
    boolean matches(Quantity<Angle> angle);
    
    Quantity<Speed> getVelocity(Quantity<Angle> angle);
  }
  
  public static class SingleWindSpeedPolar {
    private List<AngularSector> angularSectors = new LinkedList<>();
    private Quantity<Speed> trueWindSpeed;
    
    private PolarVector<Speed> lastEntry=null;
    
    
    public SingleWindSpeedPolar(Quantity<Speed> trueWindSpeed) {
      this.trueWindSpeed = trueWindSpeed.to(SPEED_UNIT);
    }
    
    public Quantity<Speed> getVelocity(Quantity<Angle> angle) {
      Optional<AngularSector> optionalSector = angularSectors.stream().filter(sector -> sector.matches(angle)).findFirst();
      if (optionalSector.isPresent()) {
        return optionalSector.get().getVelocity(angle);
      }
      else {
        return null;
      }
    }
    
    public void add(PolarVector<Speed> newEntry) {
      if (lastEntry==null) {
        lastEntry = newEntry.to(SPEED_UNIT, ANGLE_UNIT);
        return;
      }
      newEntry = newEntry.to(SPEED_UNIT, ANGLE_UNIT);
      if (newEntry.getAngle().getValue().doubleValue() <= lastEntry.getAngle().getValue().doubleValue()) {
        throw new IllegalArgumentException("Invalid entry: values must be added with increasing angles!");
      }
      angularSectors.add(new InterpolatingAngularSector(lastEntry, newEntry));
      lastEntry = newEntry;
    }
  }
 
  
  public static class InterpolatingAngularSector implements AngularSector {
    
    private PolarVector<Speed> start;
    private PolarVector<Speed> end;
    
    private Quantity<Angle> deltaAngle;
    private Quantity<Speed> deltaSpeed;
    
    public InterpolatingAngularSector(PolarVector<Speed> start, PolarVector<Speed> end) {
      this.start = start.to(SPEED_UNIT, ANGLE_UNIT);
      this.end = end.to(SPEED_UNIT, ANGLE_UNIT);
      
      deltaAngle = end.getAngle().subtract(start.getAngle());
      deltaSpeed = end.getRadian().subtract(start.getRadian());
    }

    @Override
    public boolean matches(Quantity<Angle> angle) {
      double angleValue = angle.to(ANGLE_UNIT).getValue().doubleValue();
      return (angleValue>=start.getAngle().getValue().doubleValue()
           && angleValue<=end.getAngle().getValue().doubleValue());
    }

    @Override
    public Quantity<Speed> getVelocity(Quantity<Angle> angle) {
      Quantity<Angle> angleDistanceFromStart = angle.subtract(start.getAngle());
      double factor = angleDistanceFromStart.divide(deltaAngle).getValue().doubleValue();
      Quantity<Speed> result = start.getRadian().add(deltaSpeed.multiply(factor));
      return result;
    }
    
  }
}
