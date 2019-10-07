package ballantines.nautics.units;

import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ballantines.nautics.units.NauticalUnits.degrees;

public class ArcDegreeFormat {

  private static String HEADING = "([NSEW-]?)\\s?";
  private static String INTEGER = "(\\d+)";
  private static String FLOAT = "(\\d+\\.?\\d*)";
  private static String DEGREE = "°?\\s?";
  private static String MINUTE = "\\'?\\s?";
  private static String SECOND = "\"?\\s?";

  //public static Pattern H_DMS_PATTERN = Pattern.compile("([NSEW-]?)\\s?(\\d+)°(\\d+)\'(\\d+)\"");
  public static Pattern H_DMS_PATTERN = Pattern.compile(HEADING+INTEGER+DEGREE+INTEGER+MINUTE+INTEGER+SECOND);
  //public static Pattern H_DM_PATTERN = Pattern.compile("([NSEW-]?)\\s?(\\d+)°\\s?(\\d+.\\d+)\'");
  public static Pattern H_DM_PATTERN = Pattern.compile(HEADING+INTEGER+DEGREE+ FLOAT +MINUTE);
  //public static Pattern H_D_PATTERN = Pattern.compile("([NSEW-]?)\\s?(\\d+.\\d+)\\s?°?");
  public static Pattern H_D_PATTERN = Pattern.compile(HEADING+ FLOAT +DEGREE);

  public static Quantity<Angle> parseAngle(String string) {
    Matcher matcher = H_D_PATTERN.matcher(string);
    if (matcher.matches()) {
      String heading = matcher.group(1);
      double degree = Double.parseDouble(matcher.group(2));
      if (!heading.isEmpty() && "SW-".contains(heading)) {
        degree = -degree;
      }
      return degrees(degree);
    }

    matcher = H_DM_PATTERN.matcher(string);
    if (matcher.matches()) {
      String heading = matcher.group(1);
      double degree = Integer.parseInt(matcher.group(2));
      double minute = Double.parseDouble(matcher.group(3));
      if (!heading.isEmpty() && "SW-".contains(heading)) {
        degree = -degree;
        minute = -minute;
      }
      return degrees(degree, minute);
    }

    matcher = H_DMS_PATTERN.matcher(string);
    if (matcher.matches()) {
      String heading = matcher.group(1);
      double degree = Integer.parseInt(matcher.group(2));
      double minute = Integer.parseInt(matcher.group(3));
      double second = Integer.parseInt(matcher.group(4));
      if (!heading.isEmpty() && "SW-".contains(heading)) {
        degree = -degree;
        minute = -minute;
        second = -second;
      }
      return degrees(degree, minute, second);
    }

    throw new IllegalArgumentException("Invalid format: " + string);
  }
}
