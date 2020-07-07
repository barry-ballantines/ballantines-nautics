package ballantines.nautics.routing.export;

import ballantines.nautics.routing.Leg;
import ballantines.nautics.units.AngleUtil;
import ballantines.nautics.units.NauticalUnits;

import java.io.PrintWriter;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static ballantines.nautics.units.NauticalUnits.*;

public abstract class GPXExport implements Export {


  public static RouteGPXExport from(Leg leg) {
    return new RouteGPXExport(leg);
  }

  public static IsochroneGPXExport from(List<Leg> isochrone) {
    return new IsochroneGPXExport(isochrone);
  }

  private GPXExport() {
  }

  public void to(PrintWriter out) {
    printHeader(out);
    printBody(out);
    printFooter(out);
    out.flush();
  }

  protected void printHeader(PrintWriter out) {
    out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    out.println("<gpx version=\"1.1\" creator=\"Ballantines Nautics Routing\"\n" +
                "     xmlns=\"http://www.topografix.com/GPX/1/1\"\n" +
                "     xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "     xsi:schemaLocation=\"http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd\">");
  }

  protected abstract void printBody(PrintWriter out);

  protected void printFooter(PrintWriter out) {
    out.println("</gpx>");
  }

  protected void printRoute(PrintWriter out, List<Leg> route, String routeName) {
    out.println("  <rte>");
    if (routeName!=null) {
      out.println("    <name>"+routeName+"</name>");
    }
    printWaypoints(out, route);
    out.println("  </rte>");
  }


  protected void printWaypoints(PrintWriter out, List<Leg> legs) {

    for (int index=0; index<legs.size(); index++) {
      String name = getWaypointName(legs, index);
      printWaypoint(out, legs.get(index), name);
    }
  }


  protected void printWaypoint(PrintWriter out, Leg leg, String name) {
    Number lat = leg.endpoint.getLatitude().to(ARC_DEGREE).getValue();
    Number lon = leg.endpoint.getLongitude().to(ARC_DEGREE).getValue();
    String timestamp = (leg.time==null) ? null
            : leg.time.toInstant().atZone(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_INSTANT);

    out.println(String.format(Locale.US,"    <rtept lat=\"%.8f\" lon=\"%.8f\">", lat, lon));
    if (name!=null) {
      out.println("      <name>" + name + "</name>");
    }
    if (timestamp!=null) {
      out.println("      <time>" + timestamp + "</time>");
    }
    String comment = getWaypointComment(leg);
    if (comment!=null && comment.length()>0) {
      out.println("      <cmt>");
      out.println(comment);
      out.println("      </cmt>");
    }
    out.println("    </rtept>");
  }

  protected String getWaypointComment(Leg leg) {
   return null;
  }

  protected String getRouteName(List<Leg> route) {
    return null;
  }

  protected String getWaypointName(List<Leg> legs, int index) {
    return null;
  }

  // === IMPLEMENTATIONS ===

  public static class IsochroneGPXExport extends GPXExport {
    private IsochroneGPXExport(List<Leg> isochrone) {
      this.isochrones = new LinkedList<>();
      this.isochrones.add(isochrone);
    }

    public void and(List<Leg> isochrone) {
      this.isochrones.add(isochrone);
    }
    @Override
    protected void printBody(PrintWriter out) {
      for (List<Leg> isochrone : isochrones) {
        if (!isochrone.isEmpty()) {
          printRoute(out, isochrone, "Isochrone for time " + isochrone.get(0).time);
        }
      }
    }

    private List<List<Leg>> isochrones;
  }

  public static class RouteGPXExport extends GPXExport {

    private RouteGPXExport(Leg leg) {
      this.leg = leg;
    }

    @Override
    protected void printBody(PrintWriter out) {
      printRoute(out, leg.getRoute(), String.format("Route from %s to %s (%s)", leg.getStart(), leg.getDestination(), leg.totalDistance()));
    }

    @Override
    protected String getWaypointName(List<Leg> legs, int index) {
      return (index==0) ? "Start" : (index==legs.size()-1) ? "Destination" : "Wpt " + index;
    }

    @Override
    protected String getWaypointComment(Leg leg) {
      StringBuilder out = new StringBuilder();
      if (leg.bearing!=null) {
        out.append(String.format(Locale.US, "          Bearing : %d°", leg.bearing.to(ARC_DEGREE).getValue().intValue()));
        out.append("\n");
      }
      if (leg.distance!=null) {
        out.append(String.format(Locale.US, "         Leg dist : %.1f nm", leg.distance.to(NAUTICAL_MILE).getValue().floatValue()));
        out.append("\n");
      }
      if (leg.boatSpeed!=null) {
        out.append(String.format(Locale.US, "       Boat speed : %.1f kn", leg.boatSpeed.to(KNOT).getValue().floatValue()));
        out.append("\n");
      }
      if (leg.wind!=null) {
        out.append(String.format(Locale.US, "             Wind : %.1f kn, %d°", leg.wind.getRadial().to(KNOT).getValue().floatValue(),
                Math.round(AngleUtil.normalizeToLowerBound(leg.wind.getAngle().to(ARC_DEGREE).getValue().doubleValue(), 0.))));
        out.append("\n");
      }
      return out.toString();
    }

    private Leg leg;
  }


}
