package ballantines.nautics.routing;

import ballantines.nautics.grib2.util.ResourcesFileExportUtil;
import ballantines.nautics.routing.filter.LatLonBoxFilter;
import ballantines.nautics.routing.polar.Polar;
import ballantines.nautics.routing.polar.PolarParser;
import ballantines.nautics.routing.wind.Grib2WindField;
import ballantines.nautics.routing.wind.WindField;
import ballantines.nautics.units.LatLon;
import tec.units.ri.format.QuantityFormat;
import tec.units.ri.format.SimpleUnitFormat;
import tec.units.ri.quantity.Quantities;
import tec.units.ri.unit.Units;

import javax.measure.Quantity;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


import static ballantines.nautics.units.NauticalUnits.*;

public class RoutingMain {

  public static void main(String... args) throws Exception {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

    Polar polar = createPolar();
    Grib2WindField windfield = (Grib2WindField) createWindField();
    LatLonBoxFilter legFilter = new LatLonBoxFilter(windfield.getBounds());

    LatLon start = new LatLon(degrees(-34.), degrees(152.0));
    LatLon destination = new LatLon(degrees(-40.), degrees(173.0));

    IsochronesRouting routing = new IsochronesRouting();

    routing.setIsochronesListener(new LoggingIsochronesListener());
    routing.setWindfield(windfield);
    routing.setPolar(polar);
    routing.setLegFilter(legFilter);

    routing.setStartingDate(windfield.getForecastTimes().get(0));
    routing.setStartingPoint(start);
    routing.setDestinationPoint(destination);

    routing.setPeriod(Quantities.getQuantity(3.0, Units.HOUR));

    Leg leg = routing.start();
    List<Leg> route = leg.getRoute();

    System.out.println("=== BEST ROUTE ===");


    QuantityFormat format = QuantityFormat.getInstance();

    for (Leg l : route) {
      StringBuilder out = new StringBuilder();
      out.append(l.time).append("\t");
      format(out, l.bearing, "%.2f").append('\t');
      format(out, l.distance, "%.2f").append("\t");
      format(out, l.endpoint.getLatitude(), "%.4f" ).append("\t");
      format(out, l.endpoint.getLongitude(), "%.4f").append("\t");
      System.out.println(out);
    }
  }

  private static WindField createWindField() throws IOException{
    File gribFile = ResourcesFileExportUtil.exportResourceToTmpFile("/test/grib2/Sydney-Wellington.grb2");
    return new Grib2WindField(gribFile);
  }

  private static Polar createPolar() throws IOException {
    Reader polarFileReader = new InputStreamReader(RoutingMain.class.getResourceAsStream("/test/polar/Class40.pol"));
    PolarParser parser = new PolarParser();
    Polar polar = parser.parsePolar(polarFileReader);
    return polar;
  }

  private static StringBuilder format(StringBuilder out, Quantity quantity, String numberFormat) throws Exception {
    if (quantity==null) {
      return out.append("---");
    }
    out.append(String.format(numberFormat, quantity.getValue()));
    if (!quantity.getUnit().isCompatible(ARC_DEGREE)) {
      out.append(" ");
    }
    SimpleUnitFormat.getInstance().format(quantity.getUnit(), out);

    return out;
  }

  public static class LoggingIsochronesListener implements IsochronesListener {

    @Override
    public void isochronesCalculated(Date date, List<Leg> isochrones) {
      System.out.println("---------------------------------------------------------------");
      System.out.println(" New Isochrone calculated");
      System.out.println("                     Time : " + date);
      System.out.println("     # of possible routes : " + isochrones.size());
    }

    @Override
    public void winningLegFound(Leg winningLeg) {
      System.out.println("===============================================================");
      System.out.println(" WINNING LEG FOUND");
      System.out.println("              ETA : " + winningLeg.time);
      System.out.println("   Total distance : " + winningLeg.totalDistance());
      System.out.println("        # of legs : " + winningLeg.totalNumberOfLegs());
      System.out.println("===============================================================");
    }

    @Override
    public void noLegFound() {
      System.out.println("===============================================================");
      System.out.println("   No Winning Leg Found");
      System.out.println("===============================================================");
    }
  }
}
