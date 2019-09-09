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
 * @author barry
 * @param <T> The type of the radial coordinate
 */
public class PolarVector<T extends Quantity<T>> {
  
  public static <T extends Quantity<T>> PolarVector<T> create(double value, Unit<T> valueUnit, double angle, Unit<Angle> angleUnit) {
    return new PolarVector(Quantities.getQuantity(value, valueUnit), Quantities.getQuantity(angle, angleUnit));
  }
  
  private Quantity<Angle> angle;
  private Quantity<T> radial;
  
  public PolarVector(Quantity<T> value, Quantity<Angle> angle) {
    assert value.getValue().doubleValue() >= 0.;
    this.angle = angle;
    this.radial = value;
  }
  
  public PolarVector<T> to(Unit<T> radialUnit, Unit<Angle> angleUnit) {
    return new PolarVector<T>(radial.to(radialUnit), angle.to(angleUnit));
  }
  public PolarVector<T> toRadialUnit(Unit<T> radialUnit) {
    if (radial.getUnit().equals(radialUnit)) return this;
    return new PolarVector<T>(radial.to(radialUnit), angle);
  }
  public PolarVector<T> toAngleUnit(Unit<Angle> angleUnit) {
    if (angle.getUnit().equals(angleUnit)) return this;
    return new PolarVector<T>(radial, angle.to(angleUnit));
  }

  public Quantity<Angle> getAngle() {
    return angle;
  }
  
  public double getAngle(Unit<Angle> unit) {
    return angle.to(unit).getValue().doubleValue();
  }

  public Quantity<T> getRadial() {
    return radial;
  }
  
  public double getRadial(Unit<T> unit) {
    return radial.to(unit).getValue().doubleValue();
  }
 
  public PolarVector<T> add(PolarVector<T> other) {
    double r1 = this.radial.getValue().doubleValue();
    double r2 = other.getRadial(this.radial.getUnit());
    double phi1 = this.getAngle(Units.RADIAN);
    double phi2 = other.getAngle(Units.RADIAN);
    
    double r = sqrt(r1*r1 + r2*r2 + 2*r1*r2*cos(phi1 - phi2));
    double phi = phi1 + atan2(r2*sin(phi2-phi1), r1 + r2*cos(phi2-phi1));
    
    Quantity<Angle> angle = Quantities.getQuantity(phi, Units.RADIAN).to(this.angle.getUnit());
    Quantity<T> value = Quantities.getQuantity(r, this.radial.getUnit());
    
    return createSameTypeAsThis(value, angle); 
  }
  
  public PolarVector<T> subtract(PolarVector<T> other) {
    return this.add(other.reverse());
  }
  
  /**
   * Rotates the vector by the given turn angle. 
   * 
   * The radial component is unchanged. The new polar vector is calculated by the formular:
   * 
   * <code>(r, phi).rotateClockwise(theta) = (r, phi + theta)</code>
   * 
   * If the turn angle is positive (0° &lt; turn &lt; 180°), the turn is clockwise.
   * If the turn angle is negative (0° &gt; turn &gt; -180°), the turn is counter-clockwise.
   * 
   * 
   * @param turn  the turn angle
   * @return a new polar vector with the same units as this vector.
   */
  public PolarVector<T> rotateClockwise(Quantity<Angle> turn) {
    double phi = this.angle.getValue().doubleValue();
    phi = phi + turn.to(this.angle.getUnit()).getValue().doubleValue();
    return new PolarVector<T>(this.radial, Quantities.getQuantity(phi, this.angle.getUnit()));
  }
  
  /**
   * Rotates the vector by the given turn angle. 
   * 
   * The radial component is unchanged. The new polar vector is calculated by the formular:
   * 
   * <code>(r, phi).rotateCounterClockwise(theta) = (r, phi - theta)</code>
   * 
   * If the turn angle is positive (0° &lt; turn &lt; 180°), the turn is clockwise.
   * If the turn angle is negative (0° &gt; turn &gt; -180°), the turn is counter-clockwise.
   * 
   * 
   * @param turn  the turn angle
   * @return a new polar vector with the same units as this vector.
   */
  public PolarVector<T> rotateCounterClockwise(Quantity<Angle> turn) {
    double phi = this.angle.getValue().doubleValue();
    phi = phi - turn.to(this.angle.getUnit()).getValue().doubleValue();
    return new PolarVector<T>(this.radial, Quantities.getQuantity(phi, this.angle.getUnit()));
  }
  
  public <U extends Quantity<U>> PolarVector<U> multiply(Quantity<?> factor) {
    Quantity<U> product = (Quantity<U>) this.radial.multiply(factor);
    
    return new PolarVector<U>(product, this.angle);
  }
  
  public PolarVector<T> reverse() {
    double phi = getAngle(NauticalUnits.ARC_DEGREE);
    phi = (phi + 180.) % 360.;
    return new PolarVector<T>(radial, Quantities.getQuantity(phi, NauticalUnits.ARC_DEGREE).to(angle.getUnit()));
  }
  
  protected PolarVector<T> createSameTypeAsThis(Quantity<T> value, Quantity<Angle> angle) {
    return new PolarVector<T>(value, angle);
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + Objects.hashCode(this.angle);
    hash = 67 * hash + Objects.hashCode(this.radial);
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
    if (!Objects.equals(this.radial, other.radial)) {
      return false;
    }
    return true;
  }
  
  public String toString() {
    return "(" + radial + "; " + angle + ")";
  }
  
}
