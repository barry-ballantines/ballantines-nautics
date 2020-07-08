package ballantines.nautics.routing.export;

import ballantines.nautics.routing.Leg;
import ballantines.nautics.units.AngleUtil;
import ballantines.nautics.units.LatLon;

import java.io.PrintWriter;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import static ballantines.nautics.units.NauticalUnits.*;

/**
 * Format:
 *
 * Position;Time;Bearing;Distance;Boatspeed;Wind direction;Wind speed
 * 34°30.123 N  032°23.345 W;2020-07-08T12:23:42;245°;24 nm;12.3 kn;323°;28 kn;
 */
public class SailawayRouteExport implements Export {

  private static final char CSV_SEP = ';';

  public static SailawayRouteExport from(Leg leg) {
    return new SailawayRouteExport(leg);
  }



  private SailawayRouteExport(Leg route) {
    this.route = route;
  }

  public void to(PrintWriter out) {
    List<Leg> legs  = this.route.getRoute();
    for (Leg l : legs)  {
      String wp = formatWaypoint(l);
      out.println(wp);
    }
    out.flush();
  }

  private String formatWaypoint(Leg l) {
    StringBuilder line = new StringBuilder(formatLatLon(l.endpoint) ).append(CSV_SEP);
    line.append(l.time == null ? "" : l.time.toInstant().atZone(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_INSTANT)).append(CSV_SEP);
    line.append(l.bearing == null ? "" : String.format(Locale.US, "%d°", l.bearing.to(ARC_DEGREE).getValue().intValue())).append(CSV_SEP);
    line.append(l.distance == null ? "" : String.format(Locale.US, "%.1f nm", l.distance.to(NAUTICAL_MILE).getValue().floatValue())).append(CSV_SEP);
    line.append(l.boatSpeed == null ? "" : String.format(Locale.US, "%.1f kn", l.boatSpeed.to(KNOT).getValue().floatValue())).append(CSV_SEP);
    line.append(l.wind == null ? "" : String.format(Locale.US, "%d°", Math.round(AngleUtil.normalizeToLowerBound(l.wind.getAngle().to(ARC_DEGREE).getValue().doubleValue(), 0.)))).append(CSV_SEP);
    line.append(l.wind == null ? "" : String.format(Locale.US, "%.1f kn", l.wind.getRadial().to(KNOT).getValue().floatValue())).append(CSV_SEP);
    return line.toString();
  }

  private String formatLatLon(LatLon pos) {
    Number lat = pos.getLatitude().to(ARC_DEGREE).getValue();
    Number lon = pos.getLongitude().to(ARC_DEGREE).getValue();

    return formatDM(lat.doubleValue(), 2, 3, true) + "  " +
            formatDM(lon.doubleValue(), 3, 3, false);
  }

  private String formatDM(double value, int leadingZeros, int precision, boolean latitude) {
    String hdg;
    if (value>=0) {
      hdg = latitude ? "N" : "E";
    }
    else {
      hdg = latitude ? "S" : "W";
    }
    double angle = Math.abs(value);
    int degree = (int) angle;
    double minutes = (angle - degree) * 60;
    return String.format(Locale.US,"%0"+leadingZeros+"d°%02."+precision+"f %s", degree, minutes, hdg);
  }
  // === Attributes ===

  private Leg route;
}
