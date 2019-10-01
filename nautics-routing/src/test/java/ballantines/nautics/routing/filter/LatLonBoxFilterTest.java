package ballantines.nautics.routing.filter;

import ballantines.nautics.routing.Leg;
import ballantines.nautics.units.LatLon;
import ballantines.nautics.utils.LatLonBounds;
import org.junit.Test;

import static org.junit.Assert.*;

public class LatLonBoxFilterTest {

  @Test
  public void test_atlantic() {

    LatLon nw = new LatLon(50., -50); // 50° N  50° W
    LatLon se = new LatLon(-50., 50.); // 50° S  50° E

    LatLon inside = new LatLon(-30., 23.); // 30° S  23° E

    LatLon outsideNorth = new LatLon(52., 23.);
    LatLon outsideSouth = new LatLon(-51., -23.);
    LatLon outsideWest = new LatLon( 12., -54.);
    LatLon outsideEast = new LatLon( -12., 52.);

    LatLonBoxFilter filter = new LatLonBoxFilter(LatLonBounds.fromNorthWestToSouthEast(nw, se));

    assertTrue("inside", filter.accept(leg(inside)));
    assertFalse("outside N", filter.accept(leg(outsideNorth)));
    assertFalse("outside S", filter.accept(leg(outsideSouth)));
    assertFalse("outside W", filter.accept(leg(outsideWest)));
    assertFalse("outside E", filter.accept(leg(outsideEast)));

  }

  @Test
  public void test_pacific() {

    LatLon nw = new LatLon(50., 130.);  // 50° N   130° E
    LatLon se = new LatLon(-50., -130.); // 50° S   130° W

    LatLon insideWest = new LatLon(-30., 160.); // 30° S  160° E
    LatLon insideEast = new LatLon(-30., -160.); // 30° S  160° W

    LatLon outsideNorth = new LatLon(52., 160.);
    LatLon outsideSouth = new LatLon(-51., -160.);
    LatLon outsideWest = new LatLon( 12., 128.);
    LatLon outsideEast = new LatLon( -12., -128.);

    LatLonBoxFilter filter = new LatLonBoxFilter(LatLonBounds.fromNorthWestToSouthEast(nw, se));

    assertTrue("inside W", filter.accept(leg(insideWest)));
    assertTrue("inside E", filter.accept(leg(insideEast)));
    assertFalse("outside N", filter.accept(leg(outsideNorth)));
    assertFalse("outside S", filter.accept(leg(outsideSouth)));
    assertFalse("outside W", filter.accept(leg(outsideWest)));
    assertFalse("outside E", filter.accept(leg(outsideEast)));

  }

  public static Leg leg(LatLon endpoint) {
    Leg leg = new Leg();
    leg.endpoint = endpoint;
    return leg;
  }
}
