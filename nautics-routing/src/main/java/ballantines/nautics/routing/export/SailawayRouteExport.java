package ballantines.nautics.routing.export;

import ballantines.nautics.routing.Leg;
import ballantines.nautics.units.LatLon;

import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

import static ballantines.nautics.units.NauticalUnits.ARC_DEGREE;

public class SailawayRouteExport implements Export {


  public static SailawayRouteExport from(Leg leg) {
    return new SailawayRouteExport(leg);
  }



  private SailawayRouteExport(Leg leg) {
    this.leg = leg;
  }

  public void to(PrintWriter out) {
    List<Leg> route  = leg.getRoute();
    for (Leg l : route)  {
      String wp = formatWaypoint(l);
      out.println(wp);
    }
    out.flush();
  }

  private String formatWaypoint(Leg l) {
    return formatLatLon(l.endpoint) + ";";
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
    return String.format(Locale.US,"%0"+leadingZeros+"d° %02."+precision+"f %s", degree, minutes, hdg);
  }
  // === Attributes ===

  private Leg leg;
}
