/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ballantines.nautics.units;

import javax.measure.Dimension;
import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import tec.units.ri.unit.Units;

import static java.lang.Math.*;
import java.util.Objects;
import javax.measure.Unit;
import tec.units.ri.quantity.Quantities;
/**
 *
 * @author mbuse
 * @param <T> The radian type
 */
public class PolarVector<T extends Quantity<T>> {
  
  public static <T extends Quantity<T>> PolarVector<T> create(double value, Unit<T> valueUnit, double angle, Unit<Angle> angleUnit) {
    return new PolarVector(Quantities.getQuantity(value, valueUnit), Quantities.getQuantity(angle, angleUnit));
  }
  
  private Quantity<Angle> angle;
  private Quantity<T> radian;
  
  public PolarVector(Quantity<T> value, Quantity<Angle> angle) {
    assert value.getValue().doubleValue() >= 0.;
    this.angle = angle;
    this.radian = value;
  }

  public Quantity<Angle> getAngle() {
    return angle;
  }
  
  public double getAngle(Unit<Angle> unit) {
    return angle.to(unit).getValue().doubleValue();
  }

  public Quantity<T> getRadian() {
    return radian;
  }
  
  public double getValue(Unit<T> unit) {
    return radian.to(unit).getValue().doubleValue();
  }
 
  public PolarVector<T> add(PolarVector<T> other) {
    double r1 = this.radian.getValue().doubleValue();
    double r2 = other.getValue(this.radian.getUnit());
    double phi1 = this.getAngle(Units.RADIAN);
    double phi2 = other.getAngle(Units.RADIAN);
    
    double r = sqrt(r1*r1 + r2*r2 + 2*r1*r2*cos(phi1 - phi2));
    double phi = phi1 + atan2(r2*sin(phi2-phi1), r1 + r2*cos(phi2-phi1));
    
    Quantity<Angle> angle = Quantities.getQuantity(phi, Units.RADIAN).to(this.angle.getUnit());
    Quantity<T> value = Quantities.getQuantity(r, this.radian.getUnit());
    
    return createSameTypeAsThis(value, angle); 
  }
  
  public PolarVector<T> subtract(PolarVector<T> other) {
    return this.add(other.reverse());
  }
  
  public <U extends Quantity<U>> PolarVector<U> multiply(Quantity<?> factor) {
    Quantity<U> product = (Quantity<U>) this.radian.multiply(factor);
    
    return new PolarVector<U>(product, this.angle);
  }
  
  public PolarVector<T> reverse() {
    double phi = getAngle(NauticalUnits.ARC_DEGREE);
    phi = (phi + 180.) % 360.;
    return new PolarVector<T>(radian, Quantities.getQuantity(phi, NauticalUnits.ARC_DEGREE).to(angle.getUnit()));
  }
  
  protected PolarVector<T> createSameTypeAsThis(Quantity<T> value, Quantity<Angle> angle) {
    return new PolarVector<T>(value, angle);
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + Objects.hashCode(this.angle);
    hash = 67 * hash + Objects.hashCode(this.radian);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final PolarVector<?> other = (PolarVector<?>) obj;
    if (!Objects.equals(this.angle, other.angle)) {
      return false;
    }
    if (!Objects.equals(this.radian, other.radian)) {
      return false;
    }
    return true;
  }
  
  public String toString() {
    return "(" + radian + "; " + angle + ")";
  }
  
}
