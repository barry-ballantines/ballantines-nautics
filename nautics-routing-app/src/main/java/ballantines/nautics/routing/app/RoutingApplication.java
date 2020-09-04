package ballantines.nautics.routing.app;

import ballantines.nautics.routing.IsochronesListener;
import ballantines.nautics.routing.IsochronesRouting;
import ballantines.nautics.routing.Leg;
import ballantines.nautics.routing.export.GPXExport;
import ballantines.nautics.routing.export.SailawayRouteExport;
import ballantines.nautics.routing.filter.CombinedLegFilter;
import ballantines.nautics.routing.filter.CrossingBorderLegFilter;
import ballantines.nautics.routing.filter.LatLonBoxFilter;
import ballantines.nautics.routing.filter.LegFilter;
import ballantines.nautics.routing.polar.PolarParser;
import ballantines.nautics.routing.wind.Grib2WindField;
import ballantines.nautics.units.LatLon;
import ballantines.nautics.units.LatLonFormat;
import ballantines.nautics.units.NauticalUnits;
import ballantines.nautics.utils.LatLonBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Supplier;

import static tec.units.ri.unit.Units.HOUR;

@SpringBootApplication
public class RoutingApplication implements CommandLineRunner, IsochronesListener {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(RoutingApplication.class);
		app.setBannerMode(Banner.Mode.CONSOLE);

		app.run(RoutingApplication.class, args);
	}


  @Autowired
  private Config config;

	private IsochronesRouting routing = new IsochronesRouting();

	private Scanner scanner = new Scanner(System.in);

	private PolarParser polarParser = new PolarParser();

	private GPXExport.IsochroneGPXExport isochronesExport = null;

	private File isochronesExportFile=null;
	private File routeExportFile=null;
	private File sailawayRouteFile = null;


	@Override
	public void run(String... args) throws Exception {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

    System.out.println("=== ROUTING APPLICATION === ");
    LatLon start = config.getStart().orElseGet(readLatLon("Start (lat lon): "));
    LatLon destination = config.getDestination().orElseGet(readLatLon("Destination (lat lon): "));

    File grib2File = config.getGrib2File().orElseGet(readFile("GRIB2 weather file: ", true));
    Grib2WindField windfield = new Grib2WindField(grib2File);
    File polarFile = config.getPolarFile().orElseGet(readFile("Polar diagram file: ", true));

    Quantity<Time> simulationPeriod = config.getSimulationPeriod().orElseGet(
                      () -> Quantities.getQuantity(readDouble("Simulation period (h): ").get(), HOUR));

    if (config.exportRoute()) {
      routeExportFile = config.getExportRouteFile().orElseGet(readFile("Route export file: ", false));
    }

    if (config.exportIsochrones()) {
      isochronesExportFile = config.getExportIsochronesFile().orElseGet(readFile("Isochrones export file: ", false));
    }

    if (config.exportSailawayRoute()) {
      sailawayRouteFile = config.getSailawayExportRouteFile().orElseGet(readFile("Sailaway route export file: ", false));
    }

    LocalDateTime startTime = config.getStartDate().orElseGet(
            () -> LocalDateTime.ofInstant(windfield.getForecastTimes().get(0).toInstant(), ZoneId.of("UTC")));

    LatLonBounds boundaryBox = config.getBoundaryBox()!=null ? config.getBoundaryBox().toLatLonBounds() : windfield.getBounds();


    System.out.println("--- Configuration -----------------------------------");
    System.out.println("Start        : " + start);
    System.out.println("Start date   : " + startTime);
    System.out.println("Destination  : " + destination);
    System.out.println();
		System.out.println("Boundary Box : " + boundaryBox);
		System.out.println();
    System.out.println("Polar file   : " + polarFile);
    System.out.println("GRIB2 file   : " + grib2File);
    System.out.println();
    System.out.println("Simulation period : " + simulationPeriod);
    System.out.println();
    if (!config.getForbiddenAreas().isEmpty()) {
      System.out.println("Forbidden areas:");
      for (Bounds area : config.getForbiddenAreas() ) {
        if (area.isEnabled()) {
          System.out.println(" - " + area);
        }
      }
      System.out.println();
    }
    if (!config.getBorders().isEmpty()) {
      System.out.println("Borders:");
      for (Border border : config.getBorders()) {
        if (border.isEnabled()) {
          System.out.printf(" - name: %s %n", border.getName());
          System.out.printf("   locations: %n");
          for (String loc : border.getLocations()) {
            System.out.printf("              %s %n", loc);
          }
        }
      }
    }
    System.out.println("Export route to        : " + routeExportFile);
    System.out.println("Export isochrones to   : " + isochronesExportFile);
    System.out.println("Export sailaway to     : " + sailawayRouteFile);
    System.out.println("-----------------------------------------------------");
    System.out.println();


    CombinedLegFilter legFilters = new CombinedLegFilter();
    legFilters.add(new LatLonBoxFilter(boundaryBox));

    List<Bounds> noGoAreas = config.getForbiddenAreas();
    noGoAreas.stream().filter(b -> b.isEnabled()).forEach(b -> {
        LatLonBoxFilter filter = new LatLonBoxFilter(b.toLatLonBounds());
        legFilters.add(filter.inverse());
    });

    List<Border> borders = config.getBorders();
    borders.stream().filter(b -> b.isEnabled()).forEach(b -> {
      CrossingBorderLegFilter filter = new CrossingBorderLegFilter();
      filter.setBorder(b.getLatLons());
      legFilters.add(filter);
    });

		this.routing.setStartingPoint(start);
		this.routing.setStartingDate(Date.from(startTime.atZone(ZoneId.of("UTC")).toInstant()));
		this.routing.setDestinationPoint(destination);
		this.routing.setPeriod(simulationPeriod);
    this.routing.setLegFilter(legFilters);
    this.routing.setWindfield(windfield);
    this.routing.setPolar(polarParser.parsePolar(polarFile));

    this.routing.setIsochronesListener(this);

    System.out.println("=== STARTING SIMULATION ===");
    this.routing.start();
	}


  @Override
  public void isochronesCalculated(Date date, List<Leg> isochrones) {
    System.out.println("New Isochrone: " + date);
    if (config.exportIsochrones()) {
      if (isochronesExport==null) {
        isochronesExport = GPXExport.from(isochrones);
      }
      else {
        isochronesExport.and(isochrones);
      }
    }
  }

  @Override
  public void winningLegFound(Leg winningLeg) {
    System.out.println("=== SUCCESS - Winning Leg found! ===");
    System.out.println("");
    System.out.printf(Locale.US, "ETA:      %s\n", LocalDateTime.ofInstant(winningLeg.time.toInstant(), ZoneId.of("UTC")));
    System.out.printf(Locale.US, "Distance: %5.1f nm \n", winningLeg.totalDistance().to(NauticalUnits.NAUTICAL_MILE).getValue());
    exportRoute(winningLeg);
    exportSailawayRoute(winningLeg);
    exportIsochrones();
  }

  @Override
  public void noLegFound(Leg bestLeg) {
    System.out.println("=== FAIL - No winning rout found! ===");
    System.out.println(bestLeg.distance + " missing to destination.");
    exportRoute(bestLeg);
    exportSailawayRoute(bestLeg);
    exportIsochrones();
  }

  protected void exportRoute(Leg winningLeg) {
    if (config.exportRoute()) {
      GPXExport.from(winningLeg).to(routeExportFile);
      System.out.println("Route exported.");
    }
  }

  protected void exportIsochrones() {
    if (config.exportIsochrones()) {
      isochronesExport.to(isochronesExportFile);
      System.out.println("Isochrones exported.");
    }
  }

  private void exportSailawayRoute(Leg leg) {
	  if (config.exportSailawayRoute()) {
      SailawayRouteExport.from(leg).to(sailawayRouteFile);
      System.out.println("Sailaway route exported.");
    }
  }

  private Supplier<File> readFile(String prompt, boolean mustExist) {
	  Supplier<String> supplier = readLine(prompt);
	  return () -> {
      File file = null;
      while (file==null) {
        String in = supplier.get();
        file = new File(in);
        if (mustExist && !file.exists()) {
          System.out.println("The file " + file + " does not exist! Please specify an existing file!");
          file = null;
        }
      }
	    return file;
    };
  }

  private Supplier<LatLon> readLatLon(String prompt) {
	  Supplier<String> supplier = readLine(prompt);
	  return () -> {
	    String in = supplier.get();
      return LatLonFormat.parse(in);
    };
  }

  private Supplier<String> readLine(String prompt) {
	  System.out.print(prompt);
	  return () -> scanner.nextLine();
  }

  private Supplier<Double> readDouble(String prompt) {
    System.out.print(prompt);
    return () -> scanner.nextDouble();
  }
}
