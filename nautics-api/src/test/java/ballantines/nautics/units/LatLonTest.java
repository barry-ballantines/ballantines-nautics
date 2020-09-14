package ballantines.nautics.units;

import ballantines.nautics.utils.LatLonBounds;
import junit.framework.TestCase;
import org.junit.Test;

import static junit.framework.TestCase.*;

public class LatLonTest {

  @Test
  public void testCompareLongitudes() {
    for (double lon=0; lon < 360.; lon += 10.) {
      LatLon west = new LatLon(0., normalizeLongitude(lon));
      LatLon east = new LatLon(0., normalizeLongitude(lon + 40));

      assertTrue(west + " is west of " + east, west.isWestOf(east));
      assertTrue(east + " is east of " + west, east.isEastOf(west));
    }
  }

  @Test
  public void testCompareLatitudes() {
    for (double lat=-90.; lat < 70.; lat += 10.) {
      LatLon south = new LatLon(lat, 0.);
      LatLon north = new LatLon(lat + 20., 0.);

      assertTrue(south + " is south of " + north, south.isSouthOf(north));
      assertTrue(north + " is north of " + south, north.isNorthOf(south));

    }
  }

  @Test
  public void testLatLonBounds() {
    // critical pazific bound
    LatLonBounds bounds = LatLonBounds.fromNorthWestToSouthEast(
            new LatLon(20, 160),
            new LatLon( -20, -160));

    assertTrue(bounds.contains(new LatLon(0, 170)));
    assertTrue(bounds.contains(new LatLon(0, -170)));
    assertFalse(bounds.contains(new LatLon(0, 150)));
    assertFalse(bounds.contains(new LatLon(0, -150)));
  }
  private double normalizeLongitude(double lon) {
    return AngleUtil.normalizeToUpperBound(lon, 180.);
  }
}
