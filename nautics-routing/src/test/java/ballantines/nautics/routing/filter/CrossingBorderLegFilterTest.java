package ballantines.nautics.routing.filter;

import ballantines.nautics.routing.Leg;
import ballantines.nautics.units.LatLon;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class CrossingBorderLegFilterTest {

  @Test
  public void test_crossing() {
    LatLon a1 = new LatLon(10., 10.);
    LatLon a2 = new LatLon(20. ,20.);
    LatLon b1 = new LatLon(10., 20.);
    LatLon b2 = new LatLon(20., 10.);

    assertCrossing("a crosses b", true, a1, a2, b1, b2);
    assertCrossing("b crosses a", true, b1, b2, a1, a2);

    assertCrossing("-a crosses b", true, a2, a1, b1, b2);
    assertCrossing("-b crosses a", true, b2, b1, a1, a2);

    assertCrossing("a crosses -b", true, a1, a2, b2, b1);
    assertCrossing("b crosses -a", true, b1, b2, a2, a1);

    assertCrossing("-a crosses -b", true, a2, a1, b2, b1);
    assertCrossing("-b crosses -a", true, b2, b1, a2, a1);

  }

  @Test
  public void test_not_crossing() {
    LatLon a1 = new LatLon(10., 10.);
    LatLon a2 = new LatLon(20. ,20.);
    LatLon b1 = new LatLon(10., 00.);
    LatLon b2 = new LatLon(00., 10.);

    assertCrossing("a does not cross b",   false, a1, a2, b1, b2);
    assertCrossing("b does not cross a",   false, b1, b2, a1, a2);

    assertCrossing("-a does not cross b",  false, a2, a1, b1, b2);
    assertCrossing("-b does not cross a",  false, b2, b1, a1, a2);

    assertCrossing("a does not cross -b",  false, a1, a2, b2, b1);
    assertCrossing("b does not cross -a",  false, b1, b2, a2, a1);

    assertCrossing("-a does not cross -b", false, a2, a1, b2, b1);
    assertCrossing("-b does not cross -a", false, b2, b1, a2, a1);

  }

  @Test
  public void test_crossing_pacific() {
    LatLon a1 = new LatLon( 00., +170.);
    LatLon a2 = new LatLon(+10. ,+180.);
    LatLon b1 = new LatLon(+10., +170.);
    LatLon b2 = new LatLon(-10., -170.);

    assertCrossing("a crosses b", true, a1, a2, b1, b2);
    assertCrossing("b crosses a", true, b1, b2, a1, a2);

    assertCrossing("-a crosses b", true, a2, a1, b1, b2);
    assertCrossing("-b crosses a", true, b2, b1, a1, a2);

    assertCrossing("a crosses -b", true, a1, a2, b2, b1);
    assertCrossing("b crosses -a", true, b1, b2, a2, a1);

    assertCrossing("-a crosses -b", true, a2, a1, b2, b1);
    assertCrossing("-b crosses -a", true, b2, b1, a2, a1);
  }

  private void assertCrossing(String message, boolean expected, LatLon a1, LatLon a2, LatLon b1, LatLon b2) {
    CrossingBorderLegFilter filter = new CrossingBorderLegFilter();
    filter.setBorder(Arrays.asList(a1, a2));
    Leg parent = new Leg();
    Leg leg = new Leg();
    parent.endpoint = b1;
    leg.parent = parent;
    leg.endpoint = b2;

    if(expected) {
      assertTrue(message, !filter.accept(leg));
    }
    else {
      assertFalse(message, !filter.accept(leg));
    }
  }
}
