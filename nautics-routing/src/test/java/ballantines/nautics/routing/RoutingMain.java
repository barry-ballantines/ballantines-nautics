package ballantines.nautics.routing;

import ballantines.nautics.grib2.util.ResourcesFileExportUtil;
import ballantines.nautics.routing.export.GPXExport;
import ballantines.nautics.routing.filter.LatLonBoxFilter;
import ballantines.nautics.routing.polar.Polar;
import ballantines.nautics.routing.polar.PolarParser;
import ballantines.nautics.routing.wind.Grib2WindField;
import ballantines.nautics.routing.wind.WindField;
import ballantines.nautics.units.LatLon;
import tec.units.ri.quantity.Quantities;
import tec.units.ri.unit.Units;

import java.io.*;
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

    routing.setPeriod(Quantities.getQuantity(1.5, Units.HOUR));

    Leg leg = routing.start();
    List<Leg> route = leg.getRoute();

    System.out.println("=== BEST ROUTE ===");

    GPXExport.export(leg).to(new PrintWriter(System.out));

    File out = exportToFile(leg, "Sydney-Wellington.gpx");
    System.out.println("Exported to file: " + out.getPath());

  }

  private static File exportToFile(Leg leg, String filename) throws IOException {
    File exportFile = getGPXExportFile(filename);
    FileOutputStream fos = new FileOutputStream(exportFile);
    PrintWriter out = new PrintWriter(fos);
    try {
      GPXExport.export(leg).to(out);
    } finally {
      out.close();
    }
    return exportFile;
  }

  private static File getGPXExportFile(String filename) {
    String tmp = System.getProperty("java.io.tmpdir");
    File outputFile = new File(tmp, filename);
    return outputFile;
  }

  private static WindField createWindField() throws IOException{
    File gribFile = ResourcesFileExportUtil.exportResourceToTmpFile("/test/grib2/Sydney-Wellington.grb2");
    return new Grib2WindField(gribFile);
  }

  private static Polar createPolar() throws IOException {
    Reader polarFileReader = new InputStreamReader(RoutingMain.class.getResourceAsStream("/test/polar/50PerformanceCruiser.pol"));
    PolarParser parser = new PolarParser();
    Polar polar = parser.parsePolar(polarFileReader);
    return polar;
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
