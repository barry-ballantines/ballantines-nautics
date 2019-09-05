/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ballantines.nautics.units;

import org.junit.Test;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Speed;
import tec.units.ri.quantity.Quantities;
import tec.units.ri.format.QuantityFormat;

import static org.junit.Assert.*;
import static ballantines.nautics.units.NauticalUnits.*;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Time;
import static tec.units.ri.unit.Units.*;
import static tec.units.ri.unit.MetricPrefix.*;

/**
 *
 * @author barry
 */
public class NauticalUnitsTest {

  @Test
  public void test_NAUTICAL_MILE_to_METRE() {
    Quantity<Length> lengthInMiles = Quantities.getQuantity(5.0, NAUTICAL_MILE);
    Quantity<Length> lengthInMetre = lengthInMiles.to(METRE);
    System.out.println(lengthInMiles + " = " + lengthInMetre);
    assertEquals(1852 * 5, lengthInMetre.getValue().doubleValue(), 0.0);
  }
  
  @Test
  public void test_METRE_to_NAUTICAL_MILE() {
    Quantity<Length> lengthInMetre = Quantities.getQuantity(2 * 1852., METRE);
    Quantity<Length> lengthInMiles = lengthInMetre.to(NAUTICAL_MILE);
    System.out.println(lengthInMetre + " = " + lengthInMiles);
    assertEquals(2.0, lengthInMiles.getValue().doubleValue(), 0.0);
  }
  
  @Test
  public void test_NAUTICAL_MILE_calculation() {
    Quantity<Length> nm = Quantities.getQuantity(1., NAUTICAL_MILE);
    Quantity<Length> m = Quantities.getQuantity(148., METRE);
    Quantity<Length> sum = nm.add(m);
    
    System.out.println(nm + " + " + m + " = " + sum.to(KILO(METRE)));
    assertEquals(2., sum.to(KILO(METRE)).getValue().doubleValue(), 0.0);
  }
  
  @Test
  public void test_NAUTICAL_MILE_parsing() {
    QuantityFormat format = QuantityFormat.getInstance();
    
    Quantity<Length> a = format.parse("1 nm");
    Quantity<Length> b = format.parse("1 nmi");
    Quantity<Length> c = format.parse("1 sm");
    
    assertEquals(NAUTICAL_MILE, a.getUnit());
    assertEquals(NAUTICAL_MILE, b.getUnit());
    assertEquals(NAUTICAL_MILE, c.getUnit());
    assertEquals(3., a.add(b).add(c).getValue().doubleValue(), 0.0);
    
  }
  
  @Test
  public void test_KNOT_to_METRE_PER_SECOND() {
    Quantity<Speed> speedKn = Quantities.getQuantity(1.0, KNOT);
    Quantity<Speed> speedMS = speedKn.to(METRE_PER_SECOND);
    
    System.out.println( speedKn + " = " + speedMS);
    assertEquals(1852./3600., speedMS.getValue().doubleValue(), 0.0);
    
  }
  
  @Test
  public void test_KNOT_to_KILOMETRE_PER_HOUR() {
    Quantity<Speed> speedKn = Quantities.getQuantity(1.0, KNOT);
    Quantity<Speed> speedKmH = speedKn.to(KILO(METRE).divide(HOUR).asType(Speed.class));
    
    System.out.println( speedKn + " = " + speedKmH);
    assertEquals(1.852, speedKmH.getValue().doubleValue(), 0.0);
    
  }
  
  @Test
  public void test_METRE_PER_SECOND_to_KNOT() {
    Quantity<Speed> speedMS = Quantities.getQuantity(1., METRE_PER_SECOND);
    Quantity<Speed> speedKn = speedMS.to(KNOT);
    
    System.out.println( speedMS + " = " + speedKn);
    assertEquals(3600./1852., speedKn.getValue().doubleValue(), 0.0);
  }
  
  @Test
  public void test_KNOT_parsing() {
    QuantityFormat format = QuantityFormat.getInstance();
   
    
    Quantity<Speed> speedKn = format.parse("1 kn");
    Quantity<Speed> speedKt = format.parse("1 kt");
    //Quantity<Speed> speedxx = format.parse("1 nm/h");
    
    System.out.println(speedKn + " = " + speedKt);
    
    assertEquals(KNOT, speedKn.getUnit());
    assertEquals(KNOT, speedKt.getUnit());
  }
  
  @Test
  public void test_KNOT_boatspeed_calc() {
    QuantityFormat format = QuantityFormat.getInstance();
    
    Quantity<Speed> boatSpeed = format.parse("8 kn"); //Quantities.getQuantity(8., KNOT);
    Quantity<Length> distance = format.parse("10 nm");
    Quantity<Time> duration = distance.divide(boatSpeed).asType(Time.class).to(MINUTE);
    
    System.out.println("Boat Speed: " + boatSpeed);
    System.out.println("Distance  : " + distance);
    System.out.println("Duration  : " + duration);
    
    assertEquals(75., duration.getValue().doubleValue(), 0.0);
  }
  
  @Test
  public void test_ARC_DEGREE_to_RADIAN() {
    Quantity<Angle> quart = Quantities.getQuantity(90., ARC_DEGREE);
    Quantity<Angle> radian = quart.to(RADIAN);
    
    System.out.println(quart + " = " + radian);
    
    assertEquals(Math.PI/2, radian.getValue().doubleValue(), 0.0000001);
    
  }
  
  @Test
  public void test_ARC_DEGREE_to_ARC_MINUTE() {
    Quantity<Angle> degree = Quantities.getQuantity(1., ARC_DEGREE);
    Quantity<Angle> minute = degree.to(ARC_MINUTE);
    
    System.out.println(degree + " = " + minute);
    
    assertEquals(60., minute.getValue().doubleValue(), 0.0000001);
    
  }
  
  @Test
  public void test_ARC_MINUTE_to_ARC_SECOND() {
    Quantity<Angle> minute = Quantities.getQuantity(1., ARC_MINUTE);
    Quantity<Angle> second = minute.to(ARC_SECOND);
    
    System.out.println(minute + " = " + second);
    
    assertEquals(60., second.getValue().doubleValue(), 0.0000001);
    
  }
  
  @Test
  public void test_ARC_parsing() {
    QuantityFormat format = QuantityFormat.getInstance();
    
    Quantity<Angle> degree = format.parse("53 °");
    Quantity<Angle> minute = format.parse("33 '");
    Quantity<Angle> second = format.parse("55 \"");
    
    Quantity<Angle> position = degree.add(minute).add(second).to(ARC_DEGREE);
    
    System.out.println("Hamburg: " + degree + " " + minute + " " + second + " = " + position);
    
    assertEquals(53.5652778, position.getValue().doubleValue(), 0.0000001 );
    
    // 53.5652778
  }
}
