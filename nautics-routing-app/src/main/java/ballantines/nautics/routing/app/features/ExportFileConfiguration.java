package ballantines.nautics.routing.app.features;

import ballantines.nautics.routing.IsochronesListener;
import ballantines.nautics.routing.IsochronesRouting;
import ballantines.nautics.routing.Leg;
import ballantines.nautics.routing.app.Config;
import ballantines.nautics.routing.app.Input;
import ballantines.nautics.routing.export.GPXExport;
import ballantines.nautics.routing.export.SailawayRouteExport;
import ballantines.nautics.units.NauticalUnits;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class ExportFileConfiguration implements Feature, IsochronesListener {

  private GPXExport.IsochroneGPXExport isochronesExport = null;

  private File isochronesExportFile = null;
  private File routeExportFile = null;
  private File sailawayRouteFile = null;

  private boolean exportRoute;
  private boolean exportIsochrones;
  private boolean exportSailawayRoute;

  private Input output;

  @Override
  public void prepare(Config config, Input in) {
    exportRoute = config.exportRoute();
    exportIsochrones = config.exportIsochrones();
    exportSailawayRoute = config.exportSailawayRoute();

    if (exportRoute) {
      routeExportFile = config.getExportRouteFile().orElseGet(in.readFile(
              "Route export file (GPX)          : ", false));
    }

    if (exportIsochrones) {
      isochronesExportFile = config.getExportIsochronesFile().orElseGet(in.readFile(
              "Isochrones export file (GPX)     : ", false));
    }

    if (exportSailawayRoute) {
      sailawayRouteFile = config.getSailawayExportRouteFile().orElseGet(in.readFile(
              "Sailaway route export file (CSV) : ", false));
    }

    in.println();
  }

  @Override
  public void postPrepare(Input in) {
    this.output = in;
    in.printf("Export route to        : %s%n", routeExportFile);
    in.printf("Export isochrones to   : %s%n", isochronesExportFile);
    in.printf("Export sailaway to     : %s%n", sailawayRouteFile);
    in.println();
  }

  @Override
  public void decorate(IsochronesRouting routing) {
    routing.setIsochronesListener(this);
  }

  // === ISOCHRONE LISTENER

  @Override
  public void isochronesCalculated(Date date, List<Leg> isochrones) {
    output.println("New Isochrone: " + date);
    if (exportIsochrones) {
      if (isochronesExport == null) {
        isochronesExport = GPXExport.from(isochrones);
      } else {
        isochronesExport.and(isochrones);
      }
    }
  }

  @Override
  public void winningLegFound(Leg winningLeg) {

    output.println("");
    output.println("=== SUCCESS - Winning Leg found! ===");
    output.println("");
    output.printf("ETA:      %s\n", LocalDateTime.ofInstant(winningLeg.time.toInstant(), ZoneId.of("UTC")));
    output.printf("Distance: %5.1f nm \n", winningLeg.totalDistance().to(NauticalUnits.NAUTICAL_MILE).getValue());
    output.println("");
    exportRoute(winningLeg);
    exportSailawayRoute(winningLeg);
    exportIsochrones();
  }

  @Override
  public void noLegFound(Leg bestLeg) {

    output.println("");
    output.println("=== FAIL - No winning rout found! ===");
    output.println("");
    output.println(bestLeg.distance + " missing to destination.");
    output.println("");
    exportRoute(bestLeg);
    exportSailawayRoute(bestLeg);
    exportIsochrones();
  }

  protected void exportRoute(Leg winningLeg) {

    if (exportRoute) {
      GPXExport.from(winningLeg).to(routeExportFile);
      output.println("Route exported.");
    }
  }

  protected void exportIsochrones() {
    if (exportIsochrones) {
      isochronesExport.to(isochronesExportFile);
      output.println("Isochrones exported.");
    }
  }

  private void exportSailawayRoute(Leg leg) {
    if (exportSailawayRoute) {
      SailawayRouteExport.from(leg).to(sailawayRouteFile);
      output.println("Sailaway route exported.");
    }
  }
}
