package ballantines.nautics.routing.polar;

import java.util.function.Function;
import javax.measure.Quantity;
import javax.measure.quantity.Speed;
import javax.measure.quantity.Angle;
import tec.units.ri.quantity.Quantities;

import ballantines.nautics.units.PolarVector;
import ballantines.nautics.routing.polar.internal.SingleWindSpeedPolar;
import ballantines.nautics.routing.polar.internal.AngularSector;
import ballantines.nautics.routing.polar.internal.InterpolatingAngularSector;

import org.junit.Test;

import static org.junit.Assert.*;
import static ballantines.nautics.units.NauticalUnits.*;
import java.util.function.BiFunction;

/**
 *
 * @author barry
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
    SingleWindSpeedPolar polar = new SingleWindSpeedPolar(toSpeed(10.));
    polar.add(toVelocity(0,0))
            .add(toVelocity(30, 6.0))
            .add(toVelocity(90, 8.0))
            .add(toVelocity(120, 10.0))
            .add(toVelocity(160, 12.0))
            .add(toVelocity(180, 10.0));
    
    
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
  
  @Test
  public void testDefaultPolar() {
    System.out.println("test DefaultPolar");
    DefaultPolar polar = new DefaultPolar();
    
    polar.newTWS(toSpeed(10.))
            .add(toVelocity(0,0))
            .add(toVelocity(30, 6.0))
            .add(toVelocity(90, 8.0))
            .add(toVelocity(120, 10.0))
            .add(toVelocity(160, 12.0))
            .add(toVelocity(180, 10.0));
    polar.newTWS(toSpeed(20.))
            .add(toVelocity(0,0))
            .add(toVelocity(30, 8.0))
            .add(toVelocity(90, 10.0))
            .add(toVelocity(120, 12.0))
            .add(toVelocity(160, 14.0))
            .add(toVelocity(180, 12.0));
    
    BiFunction<Double,Double,Double> test = (tws, twa) -> {
      return polar.getVelocity(toSpeed(tws), toAngle(twa)).getRadian(KNOT);
    };
    
    // tws=0 -> v=0
    assertEquals(0.0, test.apply(0.0, 15.0), 0.0); 
    
    // tws/twa defined... take speed from tws/twa cell
    assertEquals(6.0, test.apply(10.0, 30.0), 0.0); 
    assertEquals(8.0, test.apply(20.0, 30.0), 0.0);
    
    // interpolating twa
    assertEquals(7.0, test.apply(10.0, 60.0), 0.0);
    assertEquals(13.0, test.apply(20.0, 170.0), 0.0);
    
    // interpolating tws
    assertEquals(9.0, test.apply(15.0, 90.0), 0.0);
    
    // interpolating twa/tws
    assertEquals(8.0, test.apply(15.0, 60.0), 0.0);
    
    // check angles out of [0,180]
    assertEquals(8.0, test.apply(15.0, 300.0), 0.0);
    assertEquals(8.0, test.apply(15.0, -60.0), 0.0);
    
  }
  
  private AngularSector createInterpolatingAngularSector(double startAngle, double startSpeed, double endAngle, double endSpeed) {
    PolarVector<Speed> start = PolarVector.create(startSpeed, KNOT, startAngle, ARC_DEGREE);
    PolarVector<Speed> end = PolarVector.create(endSpeed, KNOT, endAngle, ARC_DEGREE);
    return new InterpolatingAngularSector(start, end);
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
