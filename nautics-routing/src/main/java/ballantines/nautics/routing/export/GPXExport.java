package ballantines.nautics.routing.export;

import ballantines.nautics.routing.Leg;
import ballantines.nautics.units.AngleUtil;
import ballantines.nautics.units.NauticalUnits;

import java.io.PrintWriter;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static ballantines.nautics.units.NauticalUnits.*;

public class GPXExport {


  public static GPXExport export(Leg leg) {
    return new GPXExport(leg);
  }

  private GPXExport(Leg leg) {
    this.leg = leg;
  }

  public void to(PrintWriter out) {
    printHeader(out);
    printRoute(out);
    printFooter(out);
    out.flush();
  }

  private void printHeader(PrintWriter out) {
    out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    out.println("<gpx version=\"1.1\" creator=\"Ballantines Nautics Routing\"\n" +
                "     xmlns=\"http://www.topografix.com/GPX/1/1\"\n" +
                "     xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "     xsi:schemaLocation=\"http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd\">");
  }

  private void printFooter(PrintWriter out) {
    out.println("</gpx>");
  }

  private void printRoute(PrintWriter out) {
    out.println("  <rte>");
    printLeg(out, this.leg);
    out.println("  </rte>");
  }

  private int printLeg(PrintWriter out, Leg leg) {
    int index = 0;
    if (leg.parent!=null) {
      index = printLeg(out, leg.parent);
    }
    Number lat = leg.endpoint.getLatitude().to(ARC_DEGREE).getValue();
    Number lon = leg.endpoint.getLongitude().to(ARC_DEGREE).getValue();
    String timestamp = leg.time.toInstant().atZone(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_INSTANT);

    out.println(String.format(Locale.US,"    <rtept lat=\"%.8f\" lon=\"%.8f\">", lat, lon));
    out.println("      <name>" + index +"</name>");
    out.println("      <time>" + timestamp + "</time>");
    if (leg.bearing!=null || leg.distance!=null || leg.boatSpeed!=null || leg.wind!=null) {
      out.println("      <cmt>");
      if (leg.bearing!=null) {
        out.println(String.format(Locale.US, "          Bearing : %d°", leg.bearing.to(ARC_DEGREE).getValue().intValue()));
      }
      if (leg.distance!=null) {
        out.println(String.format(Locale.US, "         Leg dist : %.1f nm", leg.distance.to(NAUTICAL_MILE).getValue().floatValue()));
      }
      if (leg.boatSpeed!=null) {
        out.println(String.format(Locale.US, "       Boat speed : %.1f kn", leg.boatSpeed.to(KNOT).getValue().floatValue()));
      }
      if (leg.wind!=null) {
        out.println(String.format(Locale.US, "             Wind : %.1f kn, %d°", leg.wind.getRadial().to(KNOT).getValue().floatValue(),
                Math.round(AngleUtil.normalizeToLowerBound(leg.wind.getAngle().to(ARC_DEGREE).getValue().doubleValue(), 0.))));
      }
      out.println("      </cmt>");
    }
    out.println("    </rtept>");
    return index+1;
  }

  private Leg leg;
}
