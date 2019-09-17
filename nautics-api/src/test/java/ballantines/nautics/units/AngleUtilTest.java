/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ballantines.nautics.units;

import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import org.junit.Test;
import tec.units.ri.quantity.Quantities;

import static org.junit.Assert.*;
import static ballantines.nautics.units.NauticalUnits.degrees;

/**
 *
 * @author mbuse
 */
public class AngleUtilTest {
  
  public AngleUtilTest() {
  }

  /**
   * Test of normalize method, of class AngleUtil.
   */
  @Test
  public void testNormalizeDegrees() {
    System.out.println("normalizeDegrees");
    
    assertEquals(123.2, AngleUtil.normalizeDegrees(123.2), 0.0);
    assertEquals(-123.2, AngleUtil.normalizeDegrees(-123.2), 0.0);
    
    assertEquals(12.3, AngleUtil.normalizeDegrees(372.3), 0.000001);
    assertEquals(-12.3, AngleUtil.normalizeDegrees(-372.3), 0.000001);
    
    
  }

  /**
   * Test of normalizeToLowerBound method, of class AngleUtil.
   */
  @Test
  public void testNormalizeToLowerBound() {
    System.out.println("normalizeToLowerBound");
    assertNormalizeToLowerBound( 180.,  180., AngleUtil.DEGREE_000);
    assertNormalizeToLowerBound( 270.,  -90., AngleUtil.DEGREE_000);
    assertNormalizeToLowerBound(   0.,  360., AngleUtil.DEGREE_000);
    assertNormalizeToLowerBound(  90.,  450., AngleUtil.DEGREE_000);
    assertNormalizeToLowerBound(  90.,  90. + 2 * 360., AngleUtil.DEGREE_000);
  }
  
  private void assertNormalizeToLowerBound(double expected, double degrees, Quantity<Angle> lowerBound) {
    Quantity<Angle> angle = degrees(degrees);
    Quantity<Angle> result = AngleUtil.normalizeToLowerBound(angle, lowerBound);
    assertEquals(expected, result.getValue().doubleValue(), 0.000001);
  }
  
  @Test
  public void testNormalizeToUpperBound() {
    System.out.println("normalizeToUpperBound");
    assertNormalizeToUpperBound( 180. ,  180. , AngleUtil.DEGREE_180);
    assertNormalizeToUpperBound(-179.9,  180.1, AngleUtil.DEGREE_180);  
    assertNormalizeToUpperBound( 180. , -180. , AngleUtil.DEGREE_180);
    assertNormalizeToUpperBound( -90. ,  -90. , AngleUtil.DEGREE_180);
    assertNormalizeToUpperBound(   0. ,  360. , AngleUtil.DEGREE_180);
    assertNormalizeToUpperBound(  90. ,  450. , AngleUtil.DEGREE_180); 
  }
  
  private void assertNormalizeToUpperBound(double expected, double degrees, Quantity<Angle> upperBound) {
    Quantity<Angle> angle = degrees(degrees);
    Quantity<Angle> result = AngleUtil.normalizeToUpperBound(angle, upperBound);
    assertEquals(expected, result.getValue().doubleValue(), 0.000001);
  }
  
}
