/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ballantines.nautics.units;

import javax.measure.quantity.Speed;
import javax.measure.quantity.Length;
import org.junit.Test;
import static org.junit.Assert.*;
import static ballantines.nautics.units.NauticalUnits.*;
import static java.lang.System.out;
import javax.measure.Quantity;
import javax.measure.quantity.Time;
import tec.units.ri.quantity.Quantities;
import tec.units.ri.unit.Units;

/**
 *
 * @author mbuse
 */
public class PolarVectorTest {
  
  protected void assertPolarVectors(PolarVector exp, PolarVector res, double precision) {
    assertEquals(exp.getRadial().getValue().doubleValue(), 
                 exp.getRadial().getValue().doubleValue(), precision);
    assertEquals(exp.getAngle().getValue().doubleValue(),
                 res.getAngle().getValue().doubleValue(), precision);
    
  }
  
  /**
   * Test of add method, of class PolarVector.
   */
  @Test
  public void testAdd() {
    out.println("PolarVector.add()");
    
    PolarVector<Speed> velocityRelativeToWater = PolarVector.create(8., KNOT, 35., ARC_DEGREE);
    PolarVector<Speed> velocityStream = PolarVector.create(1.2, KNOT, 270., ARC_DEGREE);
    
    PolarVector<Speed> velocityOverGround = velocityRelativeToWater.add(velocityStream);
    
    PolarVector<Speed> expectedResult = PolarVector.create(7.4, KNOT, 27.4, ARC_DEGREE);
    
    out.println("   Velocity in Water    : " + velocityRelativeToWater);
    out.println(" + Velocity of Stream   : " + velocityStream);
    out.println(" = Velocity over Ground : " + velocityOverGround);
    
    assertPolarVectors(expectedResult, velocityOverGround, 0.1);
    
  }

  @Test
  public void testReverse() {
    out.println("PolarVector.reverse()");
    PolarVector<Length> polar = PolarVector.create(10., NAUTICAL_MILE, 10., ARC_DEGREE);
    PolarVector<Length> inverted = polar.reverse();
    PolarVector<Length> expected = PolarVector.create(10., NAUTICAL_MILE, 190., ARC_DEGREE);
    
    assertPolarVectors(expected, inverted, 0.0);
  }

  /**
   * Test of substract method, of class PolarVector.
   */
  @Test
  public void testSubstract() {
    out.println("PolarVector.substract()");
    PolarVector<Speed> velocityRelativeToWater = PolarVector.create(8., KNOT, 35., ARC_DEGREE);
    PolarVector<Speed> velocityStream = PolarVector.create(1.2, KNOT, 270., ARC_DEGREE);
    PolarVector<Speed> velocityOverGround = PolarVector.create(7.4, KNOT, 27.4, ARC_DEGREE);
  
    assertPolarVectors(velocityRelativeToWater, velocityOverGround.subtract(velocityStream), 0.1);
    
  }
  
  /**
   * Test of multiply method, of class PolarVector.
   */
  @Test
  public void testMultiply() {
    out.println("PolarVector.multiply()");
    // velocity = 8 kn, 90°
    PolarVector<Speed> velocity = PolarVector.create(8, KNOT, 90, ARC_DEGREE);
    
    // time = 90 min
    Quantity<Time> duration = Quantities.getQuantity(90., Units.MINUTE);
    
    // dead reckoning = 12 nm, 90°
    PolarVector<Length> deadReckoning = velocity.multiply(duration);
    
    out.println("   velocity : " + velocity);
    out.println(" * duration : " + duration);
    out.println(" = D.R.     : " + deadReckoning);
    
    assertEquals(velocity.getAngle(), deadReckoning.getAngle());
    assertTrue(deadReckoning.getRadial().getUnit().isCompatible(NAUTICAL_MILE));
    assertEquals(12., deadReckoning.getRadial().to(NAUTICAL_MILE).getValue().doubleValue(), 0.0);
  }

  @Test
  public void testCreateFromCartesian() {
    out.println("PolarVector.createFromCartesianCoordinates()");

    PolarVector<Speed> velocity = PolarVector.createFromCartesianCoordinates(knots(0.), knots(5.));
    assertEquals(0., velocity.getAngle(ARC_DEGREE), 0.001);

    velocity = PolarVector.createFromCartesianCoordinates(knots(5.), knots(5.));
    assertEquals(45., velocity.getAngle(ARC_DEGREE), 0.001);

    velocity = PolarVector.createFromCartesianCoordinates(knots(5.), knots(0.));
    assertEquals(90., velocity.getAngle(ARC_DEGREE), 0.001);

    velocity = PolarVector.createFromCartesianCoordinates(knots(5.), knots(-5.));
    assertEquals(135., velocity.getAngle(ARC_DEGREE), 0.001);

    velocity = PolarVector.createFromCartesianCoordinates(knots(0.), knots(-5.));
    assertEquals(180., velocity.getAngle(ARC_DEGREE), 0.001);

    velocity = PolarVector.createFromCartesianCoordinates(knots(-5.), knots(5.));
    assertEquals(-45., velocity.getAngle(ARC_DEGREE), 0.001);

    velocity = PolarVector.createFromCartesianCoordinates(knots(-5.), knots(0.));
    assertEquals(-90., velocity.getAngle(ARC_DEGREE), 0.001);

    velocity = PolarVector.createFromCartesianCoordinates(knots(-5.), knots(-5.));
    assertEquals(-135., velocity.getAngle(ARC_DEGREE), 0.001);

  }
  
}
