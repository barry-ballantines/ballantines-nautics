package ballantines.nautics.units;

import org.junit.Test;

import javax.measure.Quantity;
import javax.measure.quantity.Angle;

import static junit.framework.TestCase.assertEquals;

public class ArcDegreeFormatTest {

  @Test
  public void testParse_H_DDMMSS() {
    Quantity<Angle> angle = ArcDegreeFormat.parseAngle("N 50°30'00\"");
    assertEquals(50.5, angle.getValue().floatValue(), 0.0001);

    angle = ArcDegreeFormat.parseAngle("S 50°30'00\"");
    assertEquals(-50.5, angle.getValue().floatValue(), 0.0001);

    angle = ArcDegreeFormat.parseAngle("-50°30'00\"");
    assertEquals(-50.5, angle.getValue().floatValue(), 0.0001);
  }

  @Test
  public void testParse_H_DDMM() {
    Quantity<Angle> angle = ArcDegreeFormat.parseAngle("N 50°30.00'");
    assertEquals(50.5, angle.getValue().floatValue(), 0.0001);

    angle = ArcDegreeFormat.parseAngle("S 50°30.00'");
    assertEquals(-50.5, angle.getValue().floatValue(), 0.0001);

    angle = ArcDegreeFormat.parseAngle("-50°30.00'");
    assertEquals(-50.5, angle.getValue().floatValue(), 0.0001);
  }


  @Test
  public void testParse_DD() {
    Quantity<Angle> angle = ArcDegreeFormat.parseAngle("50.500°");
    assertEquals(50.5, angle.getValue().floatValue(), 0.0001);

    angle = ArcDegreeFormat.parseAngle("-50.500°");
    assertEquals(-50.5, angle.getValue().floatValue(), 0.0001);

    angle = ArcDegreeFormat.parseAngle("-50.500");
    assertEquals(-50.5, angle.getValue().floatValue(), 0.0001);
  }
}
