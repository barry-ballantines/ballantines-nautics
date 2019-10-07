package ballantines.nautics.units;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class LatLonFormatTest {

  @Test
  public void test_parse_H_DMS() {
    String location = "N 50°30'00\" E 100°30'00\"";

    LatLon latlon = LatLonFormat.parse(location);
    assertEquals(50.5, latlon.getLatitude().getValue().floatValue(), 0.001);
    assertEquals(100.5, latlon.getLongitude().getValue().floatValue(), 0.001);
  }

  @Test
  public void test_parse_H_DM() {
    String location = "N 50°30.00' E 100°30.000'";

    LatLon latlon = LatLonFormat.parse(location);
    assertEquals(50.5, latlon.getLatitude().getValue().floatValue(), 0.001);
    assertEquals(100.5, latlon.getLongitude().getValue().floatValue(), 0.001);
  }

  @Test
  public void test_parse_H_D() {
    String location = "50.500° 100.500°";

    LatLon latlon = LatLonFormat.parse(location);
    assertEquals(50.5, latlon.getLatitude().getValue().floatValue(), 0.001);
    assertEquals(100.5, latlon.getLongitude().getValue().floatValue(), 0.001);
  }
}
