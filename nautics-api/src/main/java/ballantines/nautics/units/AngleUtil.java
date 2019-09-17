/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ballantines.nautics.units;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Angle;
import tec.units.ri.quantity.Quantities;
import tec.units.ri.unit.Units;

/**
 *
 * @author mbuse
 */
public class AngleUtil {
  
  public static final Quantity<Angle> DEGREE_180 = Quantities.getQuantity(180., NauticalUnits.ARC_DEGREE);
  public static final Quantity<Angle> DEGREE_360 = Quantities.getQuantity(360., NauticalUnits.ARC_DEGREE);
  public static final Quantity<Angle> DEGREE_000 = Quantities.getQuantity(0., NauticalUnits.ARC_DEGREE);
  
  /**
   * Normalizes a degree value to a range of ]-360., 360[
   * @param angleInDegree
   * @return an equivalent angle in range of ]-360, 360[ 
   */
  public static double normalizeDegrees(double angleInDegree) {
    return angleInDegree % 360.;
  }
  
  /**
   * Normalizes a degree value to an equivalent degree of range [lowerBound, lowerBound+360°[.
   * 
   * @param degree
   * @param lowerBound
   * @return an angle of range [lowerBound, lowerBound+360°[.
   */
  public static double normalizeToLowerBound(double degree, double lowerBound) {
    degree = normalizeDegrees(degree);
    if (degree < lowerBound) {
      return degree + 360.;
    }
    else if (degree >= (lowerBound + 360.)) {
      return degree - 360.;
    }
    return degree;
  }
  
  /**
   * Normalizes a degree value to an equivalent degree of range ]upperBound-360°, upperBound].
   * 
   * @param degree
   * @param upperBound
   * @return an angle of range ]upperValue-360°, upperValue].
   */
  public static double normalizeToUpperBound(double degree, double upperBound) {
    degree = normalizeDegrees(degree);
    if (degree <= upperBound - 360.) {
      return degree + 360.;
    }
    else if (degree > upperBound) {
      return degree - 360.;
    }
    return degree;
  }
  
  /**
   * Compares two angles (given in degree)
   * 
   * @param phi   an angle in degree (°)
   * @param theta an angle in degree (°)
   * @return delta=theta-phi, negative values indicate left turn, positive values indicate right turn.
   */
  public static double delta(double phi, double theta) {
    return normalizeToUpperBound(theta-phi, 180.);
  }
  
  /**
   * Normalizes a degree value to an equivalent degree of range [lowerBound, lowerBound+360°[.
   * 
   * @param degree
   * @param lowerBound
   * @return an angle of range [lowerBound, lowerBound+360°[.
   */
  public static Quantity<Angle> normalizeToLowerBound(Quantity<Angle> angle, Quantity<Angle> lowerBound) {
    double angleDegrees = angle.to(NauticalUnits.ARC_DEGREE).getValue().doubleValue();
    double lowerBoundDegrees = lowerBound.to(NauticalUnits.ARC_DEGREE).getValue().doubleValue();
    double resultDegrees = normalizeToLowerBound(angleDegrees, lowerBoundDegrees);
    if (resultDegrees == angleDegrees) { 
      return angle; // UNCHANGED
    }
    else {
      return Quantities.getQuantity(resultDegrees, NauticalUnits.ARC_DEGREE).to(angle.getUnit());
    }
  }
  
  /**
   * Normalizes a degree value to an equivalent degree of range ]upperBound-360°, upperBound].
   * 
   * @param degree
   * @param upperBound
   * @return an angle of range ]upperValue-360°, upperValue].
   */
  public static Quantity<Angle> normalizeToUpperBound(Quantity<Angle> angle, Quantity<Angle> upperBound) {
    double angleDegrees = angle.to(NauticalUnits.ARC_DEGREE).getValue().doubleValue();
    double lowerBoundDegrees = upperBound.to(NauticalUnits.ARC_DEGREE).getValue().doubleValue();
    double resultDegrees = normalizeToUpperBound(angleDegrees, lowerBoundDegrees);
    if (resultDegrees == angleDegrees) { 
      return angle; // UNCHANGED
    }
    else {
      return Quantities.getQuantity(resultDegrees, NauticalUnits.ARC_DEGREE).to(angle.getUnit());
    }
  }
  
  /**
   * Compares two angles (given in degree)
   * 
   * @param phi   the reference angle
   * @param theta the test ange
   * @return the angle from theta to phi (=theta-phi). normalized to ]-180°, 180°]. 
   *         negative values indicate left turn, positive values indicate right turn.
   */
  public static Quantity<Angle>delta(Quantity<Angle> phi, Quantity<Angle> theta) {
    return normalizeToUpperBound(theta.subtract(phi), DEGREE_180);
  }

  // === Trigonomic functions: sin, cos, tan ===

  public static double cos(Quantity<Angle> phi) {
    return Math.cos(phi.to(Units.RADIAN).getValue().doubleValue());
  }

  public static double sin(Quantity<Angle> phi) {
    return Math.sin(phi.to(Units.RADIAN).getValue().doubleValue());
  }

  public static double tan(Quantity<Angle> phi) {
    return Math.tan(phi.to(Units.RADIAN).getValue().doubleValue());
  }

  public static Quantity<Angle> atan2(double numerator, double denominator) {
    double sigma12 = Math.atan2(numerator, denominator);
    return Quantities.getQuantity(sigma12, Units.RADIAN);
  }
}
