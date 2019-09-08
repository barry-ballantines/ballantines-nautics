/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ballantines.nautics.routing.polar;

import java.util.function.Function;
import javax.measure.Quantity;
import javax.measure.quantity.Speed;
import javax.measure.quantity.Angle;
import tec.units.ri.quantity.Quantities;

import ballantines.nautics.units.PolarVector;
import ballantines.nautics.routing.polar.DefaultPolar;
import ballantines.nautics.routing.polar.DefaultPolar.SingleWindSpeedPolar;
import ballantines.nautics.routing.polar.DefaultPolar.AngularSector;
import ballantines.nautics.routing.polar.DefaultPolar.InterpolatingAngularSector;

import org.junit.Test;

import static org.junit.Assert.*;
import static ballantines.nautics.units.NauticalUnits.*;

/**
 *
 * @author mbuse
 */
public class DefaultPolarTest {
 
  @Test
  public void testInterpolatingAngularSector_matches() {
    System.out.println("test InterpolatingAngularSector.matches()");
    
    AngularSector sector = createInterpolatingAngularSector(30., 8.0, 50., 10.0);
   
    assertTrue(sector.matches(toAngle(30.)));
    assertTrue(sector.matches(toAngle(40.)));
    assertTrue(sector.matches(toAngle(50.)));
    assertFalse(sector.matches(toAngle(20.)));
    assertFalse(sector.matches(toAngle(60.)));
  }
  
  @Test
  public void testInterpolatingAngularSector_getVelocity() {
    System.out.println("test InterpolatingAngularSector.getVelocity()");
    
    AngularSector sector = createInterpolatingAngularSector(30., 8.0, 50., 10.0);
    
    assertEquals(8.0, sector.getVelocity(toAngle(30.)).getValue().doubleValue(), 0.0);
    assertEquals(8.5, sector.getVelocity(toAngle(35.)).getValue().doubleValue(), 0.0);
    assertEquals(9.0, sector.getVelocity(toAngle(40.)).getValue().doubleValue(), 0.0);
    assertEquals(9.5, sector.getVelocity(toAngle(45.)).getValue().doubleValue(), 0.0);
    assertEquals(10.0, sector.getVelocity(toAngle(50.)).getValue().doubleValue(), 0.0);
  }
  
  
  @Test
  public void testSingleWindSpeedPolar() {
    System.out.println("test SingleWindSpeedPolar");
    DefaultPolar.SingleWindSpeedPolar polar = new SingleWindSpeedPolar(toSpeed(10.));
    polar.add(toVelocity(0,0));
    polar.add(toVelocity(30, 6.0));
    polar.add(toVelocity(90, 8.0));
    polar.add(toVelocity(120, 10.0));
    polar.add(toVelocity(160, 12.0));
    polar.add(toVelocity(180, 10.0));
    
    
    Function<Double, Double> results = (degree) -> polar.getVelocity(toAngle(degree)).to(KNOT).getValue().doubleValue();
    
    assertEquals(0.0, results.apply(0.), 0.0);
    assertEquals(3.0, results.apply(15.), 0.0);
    assertEquals(6.0, results.apply(30.), 0.0);
    assertEquals(7.0, results.apply(60.), 0.0);
    assertEquals(8.0, results.apply(90.), 0.0);
    assertEquals(9.0, results.apply(105.), 0.0);
    assertEquals(10.0, results.apply(120.), 0.0);
    assertEquals(11.0, results.apply(140.), 0.0);
    assertEquals(12.0, results.apply(160.), 0.0);
    assertEquals(11.0, results.apply(170.), 0.0);
    assertEquals(10.0, results.apply(180.), 0.0);
  }
  
  private DefaultPolar.AngularSector createInterpolatingAngularSector(double startAngle, double startSpeed, double endAngle, double endSpeed) {
    PolarVector<Speed> start = PolarVector.create(startSpeed, KNOT, startAngle, ARC_DEGREE);
    PolarVector<Speed> end = PolarVector.create(endSpeed, KNOT, endAngle, ARC_DEGREE);
    return new DefaultPolar.InterpolatingAngularSector(start, end);
  }
  
  
  private Quantity<Speed> toSpeed(double knots) {
    return Quantities.getQuantity(knots, KNOT);
  }
  private Quantity<Angle> toAngle(double degrees) {
    return Quantities.getQuantity(degrees, ARC_DEGREE);
  }
  
  private PolarVector<Speed> toVelocity(double degrees, double knots) {
    return PolarVector.create(knots, KNOT, degrees, ARC_DEGREE);
  }
  
  
}
