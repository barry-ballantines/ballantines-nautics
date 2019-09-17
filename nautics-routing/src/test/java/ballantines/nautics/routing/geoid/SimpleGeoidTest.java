package ballantines.nautics.routing.geoid;

import ballantines.nautics.units.LatLon;
import ballantines.nautics.units.PolarVector;
import org.junit.Test;

import javax.measure.quantity.Length;

import static ballantines.nautics.units.NauticalUnits.*;
import static org.junit.Assert.*;

public class SimpleGeoidTest {

  @Test
  public void test_calculateOrthodromicDistanceAndBearing() {
    System.out.println("test_calculateOrthodromicDistanceAndBearing()");
    LatLon valparaiso = new LatLon(degrees(-33.), degrees(-71.6));
    LatLon shanghai = new LatLon(degrees(31.4), degrees(121.8));

    Geoid geoid = new SimpleGeoid();

    PolarVector<Length> course = geoid.calculateOrthodromicDistanceAndBearing(valparaiso, shanghai).to(NAUTICAL_MILE, ARC_DEGREE);
    System.out.println(" - course Valparaiso -> Shanghai = " + course);
    assertEquals(-94.41, course.getAngle().getValue().doubleValue(), 0.01);
    assertEquals(10113.4, course.getRadial().getValue().doubleValue(), 0.1);

    course = geoid.calculateOrthodromicDistanceAndBearing(shanghai, valparaiso).to(NAUTICAL_MILE, ARC_DEGREE);
    System.out.println(" - course Shanghai -> Valparaiso = " + course);
    assertEquals(101.58, course.getAngle().getValue().doubleValue(), 0.01);
    assertEquals(10113.4, course.getRadial().getValue().doubleValue(), 0.1);

    course = geoid.calculateOrthodromicDistanceAndBearing(shanghai, shanghai);
    System.out.println(" - course Shanghai -> Shanghai = " + course);
    assertEquals(0., course.getRadial().getValue().doubleValue(), 0.0001);

    course = geoid.calculateOrthodromicDistanceAndBearing(valparaiso, valparaiso);
    System.out.println(" - course Valparaiso -> Valparaiso = " + course);
    assertEquals(0., course.getRadial().getValue().doubleValue(), 0.0001);
  }

  @Test
  public void test_calculateDestination() {
    System.out.println("test_calculateDestination()");
    LatLon valparaiso = new LatLon(degrees(-33.), degrees(-71.6));
    LatLon shanghai = new LatLon(degrees(31.4), degrees(121.8));

    Geoid geoid = new SimpleGeoid();

    LatLon dest = geoid.calculateDestination(valparaiso, degrees(-94.4), nauticalMiles(10113.4));
    System.out.println(" - Start: Valparaiso = " + valparaiso.getLatitude() + " - " + valparaiso.getLongitude());
    System.out.println(" - End  : Shanghai   = " + dest.getLatitude() + " - " + dest.getLongitude());
    assertCloseBy(shanghai, dest);

    dest = geoid.calculateDestination(shanghai, degrees(101.6), nauticalMiles(10113.4));

    System.out.println(" - Start: Shanghai = " + shanghai.getLatitude() + " - " + shanghai.getLongitude());
    System.out.println(" - End  : Valparaiso = " + dest.getLatitude() + " - " + dest.getLongitude());
    assertCloseBy(valparaiso, dest);
  }

  private void assertCloseBy(LatLon expected, LatLon actual) {
    assertEquals(expected.getLatitude().getValue().doubleValue(), actual.getLatitude().getValue().doubleValue(), 0.1);
    assertEquals(expected.getLongitude().getValue().doubleValue(), actual.getLongitude().getValue().doubleValue(), 0.1);
  }

}
