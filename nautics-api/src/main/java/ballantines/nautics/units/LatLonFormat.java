package ballantines.nautics.units;

import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ballantines.nautics.units.NauticalUnits.degrees;

public class LatLonFormat {
  private static Pattern combinePatterns(Pattern p) {
    String patternAsString = p.pattern();
    return Pattern.compile("("+patternAsString + ")\\s+("+ patternAsString + ")");
  }

  public static Pattern H_DMS_PATTERN = combinePatterns(ArcDegreeFormat.H_DMS_PATTERN);
  public static Pattern H_DM_PATTERN = combinePatterns(ArcDegreeFormat.H_DM_PATTERN);
  public static Pattern H_D_PATTERN = combinePatterns(ArcDegreeFormat.H_D_PATTERN);

  public static LatLon parse(String string) {
    Matcher matcher = H_D_PATTERN.matcher(string);
    if (matcher.matches()) {
      Quantity<Angle> lat = ArcDegreeFormat.parseAngle(matcher.group(1));
      Quantity<Angle> lon = ArcDegreeFormat.parseAngle(matcher.group(4));
      return new LatLon(lat, lon);
    }

    matcher = H_DM_PATTERN.matcher(string);
    if (matcher.matches()) {
      Quantity<Angle> lat = ArcDegreeFormat.parseAngle(matcher.group(1));
      Quantity<Angle> lon = ArcDegreeFormat.parseAngle(matcher.group(5));
      return new LatLon(lat, lon);
    }

    matcher = H_DMS_PATTERN.matcher(string);
    if (matcher.matches()) {
      Quantity<Angle> lat = ArcDegreeFormat.parseAngle(matcher.group(1));
      Quantity<Angle> lon = ArcDegreeFormat.parseAngle(matcher.group(6));
      return new LatLon(lat, lon);
    }

    throw new IllegalArgumentException("Unknown format: " + string);
  }

}
