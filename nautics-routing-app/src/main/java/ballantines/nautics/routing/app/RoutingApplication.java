package ballantines.nautics.routing.app;

import ballantines.nautics.routing.IsochronesRouting;
import ballantines.nautics.routing.RoutingCalibration;
import ballantines.nautics.routing.app.features.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.Scanner;
import java.util.TimeZone;

@SpringBootApplication
public class RoutingApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(RoutingApplication.class);
		app.setBannerMode(Banner.Mode.CONSOLE);

		app.run(RoutingApplication.class, args);
	}


  @Autowired
  private Config config;

	private IsochronesRouting routing = new IsochronesRouting();

	private Scanner scanner = new Scanner(System.in);



	private Feature[] features = new Feature[] {
	        new RouteConfigurationFeature(),
          new Grib2ConfigurationFeature(),
          new PolarConfigurationFeature(),
          new ExportFileConfiguration(),
          new AngularResolutionFeature(),
          new ForbiddenAreasConfigurationFeature(),
          new ForbiddenBorderConfigurationFeature()
	    };


	@Override
	public void run(String... args) throws Exception {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    Scanner scanner = new Scanner(System.in);
    Input input = new Input();

    input.println("=== ROUTING APPLICATION === ");
    input.println();

    Arrays.stream(features).forEach(f -> f.prepare(config, input));

    input.println("--- Configuration -----------------------------------");
    input.println();

    Arrays.stream(features).forEach(f -> f.postPrepare(input));

    input.println("-----------------------------------------------------");
    input.println();

    Arrays.stream(features).forEach(f -> f.decorate(routing));

    if (args.length > 0 && "calibrate".equals(args[0])) {
      input.println("=== CALIBRATE ===");
      input.println("");

      calibrate();
    }
    else {
      input.println("=== STARTING SIMULATION ===");
      input.println("");

      this.routing.start();
    }
	}

	private void calibrate() {
    RoutingCalibration calibration = new RoutingCalibration(this.routing);
    calibration.start();
  }




}
