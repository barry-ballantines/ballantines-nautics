package ballantines.nautics.units;

import javax.measure.quantity.Length;
import javax.measure.Unit;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Speed;
import tec.units.ri.format.SimpleUnitFormat;

import static tec.units.ri.unit.Units.*;

/**
 * Nautical units for nautical calculations
 * @author barry
 */
public class NauticalUnits {
  public static final Unit<Length> NAUTICAL_MILE = METRE.multiply(1852).asType(Length.class);
  public static final Unit<Speed> KNOT = NAUTICAL_MILE.divide(HOUR).asType(Speed.class);
  public static final Unit<Angle> ARC_DEGREE = RADIAN.multiply(Math.PI).divide(180).asType(Angle.class);
  public static final Unit<Angle> ARC_MINUTE = ARC_DEGREE.divide(60).asType(Angle.class);
  public static final Unit<Angle> ARC_SECOND = ARC_MINUTE.divide(60).asType(Angle.class);
  
  static {
    SimpleUnitFormat unitFormat = SimpleUnitFormat.getInstance();
    
    unitFormat.label(NAUTICAL_MILE, "nm");
    unitFormat.alias(NAUTICAL_MILE, "sm");
    unitFormat.alias(NAUTICAL_MILE, "nmi");
    
    unitFormat.label(KNOT, "kn");
    unitFormat.alias(KNOT, "kt");
    
    unitFormat.label(ARC_DEGREE, "°");
    unitFormat.label(ARC_MINUTE, "'");
    unitFormat.label(ARC_SECOND, "\"");
    
  }
  
}
