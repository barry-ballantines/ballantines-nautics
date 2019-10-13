package ballantines.nautics.routing.app;

import ballantines.nautics.routing.IsochronesListener;
import ballantines.nautics.routing.IsochronesRouting;
import ballantines.nautics.routing.Leg;
import ballantines.nautics.routing.export.GPXExport;
import ballantines.nautics.routing.filter.LatLonBoxFilter;
import ballantines.nautics.routing.polar.PolarParser;
import ballantines.nautics.routing.wind.Grib2WindField;
import ballantines.nautics.units.LatLon;
import ballantines.nautics.units.LatLonFormat;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.TimeZone;
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

    LocalDateTime startTime = config.getStartDate().orElseGet(
            () -> LocalDateTime.ofInstant(windfield.getForecastTimes().get(0).toInstant(), ZoneId.of("UTC")));

    LatLonBounds bounds = config.getLegFilterBounds().orElseGet(()->windfield.getBounds());

    System.out.println("--- Configuration -----------------------------------");
    System.out.println("Start       : " + start);
    System.out.println("Start date  : " + startTime);
    System.out.println("Destination : " + destination);
    System.out.println();
		System.out.println("Bounds      : " + bounds);
		System.out.println();
    System.out.println("Polar file  : " + polarFile);
    System.out.println("GRIB2 file  : " + grib2File);
    System.out.println();
    System.out.println("Simulation period : " + simulationPeriod);
    System.out.println();
    System.out.println("Export route to        : " + routeExportFile);
    System.out.println("Export isochrones to   : " + isochronesExportFile);
    System.out.println("-----------------------------------------------------");
    System.out.println();


		this.routing.setStartingPoint(start);
		this.routing.setStartingDate(Date.from(startTime.atZone(ZoneId.of("UTC")).toInstant()));
		this.routing.setDestinationPoint(destination);
		this.routing.setPeriod(simulationPeriod);
    this.routing.setLegFilter(new LatLonBoxFilter(bounds));
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
        isochronesExport = GPXExport.exportIsochrones(isochrones);
      }
      else {
        isochronesExport.and(isochrones);
      }
    }
  }

  @Override
  public void winningLegFound(Leg winningLeg) {
    System.out.println("=== SUCCESS - Winning Leg found! ===");
    if (config.exportRoute()) {
      exportAsGpx(GPXExport.export(winningLeg), routeExportFile);
      System.out.println("Route exported.");
      finish();
    }
  }

  @Override
  public void noLegFound(Leg bestLeg) {
    System.out.println("=== FAIL - No winning rout found! ===");
    System.out.println(bestLeg.distance + " missing to destination.");
    File bestLegFile = new File(routeExportFile.getParentFile(), "closest-" + routeExportFile.getName());
    exportAsGpx(GPXExport.export(bestLeg), bestLegFile);
    finish();
  }

  protected void finish() {
    if (config.exportIsochrones()) {
      exportAsGpx(isochronesExport, isochronesExportFile);
      System.out.println("Isochrones exported.");
    }
  }

  protected void exportAsGpx(GPXExport export, File outputFile) {

    try (PrintWriter writer = new PrintWriter(outputFile, "UTF-8"))
    {
      export.to(writer);
      writer.flush();
    } catch(IOException ex) {
      System.err.println("Failed to write to file: " + outputFile);
      System.err.println(ex);
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
