package ballantines.nautics.routing.app;

import ballantines.nautics.units.LatLon;
import ballantines.nautics.units.LatLonFormat;
import ballantines.nautics.utils.LatLonBounds;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import tec.units.ri.quantity.Quantities;
import tec.units.ri.unit.Units;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.lang.Double.isNaN;

@Configuration
@ConfigurationProperties(prefix = "routing")
public class Config {

  @Value("${routing.start.latlon:#{null}}")
  private String startLatLon;

  @Value("${routing.destination.latlon:#{null}}")
  private String destinationLatLon;

  @Value("${routing.start.date:#{null}}")
  private String startDateString;

  @Value("${routing.grib2.file:#{null}}")
  private File grib2File = null;

  @Value("${routing.polar.file:#{null}}")
  private File polarFile = null;

  @Value("${routing.export.isochrones:false}")
  private boolean exportIsochrones;

  @Value("${routing.export.isochrones.file:#{null}}")
  private File exportIsochronesFile;

  @Value("${routing.export.route:true}")
  private boolean exportRoute;

  @Value("${routing.export.route.file:#{null}}")
  private File exportRouteFile;

  @Value("${routing.export.sailaway.route:false}")
  private boolean exportSailawayRoute;

  @Value("${routing.export.sailaway.route.file:#{null}}")
  private File exportSailawayRouteFile;

  @Value("${routing.legfilter.latitude.min:NaN}")
  private double legfilterLatitudeMin;
  @Value("${routing.legfilter.latitude.max:NaN}")
  private double legfilterLatitudeMax;
  @Value("${routing.legfilter.longitude.min:NaN}")
  private double legfilterLongitudeMin;
  @Value("${routing.legfilter.longitude.max:NaN}")
  private double legfilterLongitudeMax;
  @Value("${routing.simulation.period.hours:3.0}")
  private double simulationPeriodHours;


  private List<Bounds> forbiddenAreas = new ArrayList<>();

  public List<Bounds> getForbiddenAreas() {
    return forbiddenAreas;
  }

  public void setForbiddenAreas(List<Bounds> forbiddenAreas) {
    this.forbiddenAreas = forbiddenAreas;
  }

  public Optional<LatLon> getStart() {
    return parseLatLon(startLatLon);
  }

  public Optional<LatLon> getDestination() {
    return parseLatLon(destinationLatLon);
  }

  public Optional<LocalDateTime> getStartDate() {
    if (startDateString==null || startDateString.trim().isEmpty()) {
      return Optional.empty();
    }
    return Optional.ofNullable(LocalDateTime.parse(startDateString));
  }

  public Optional<Quantity<Time>> getSimulationPeriod() {
    if (isNaN(simulationPeriodHours)) {
      return Optional.empty();
    }
    else {
      return Optional.of(Quantities.getQuantity(simulationPeriodHours, Units.HOUR));
    }
  }

  public Optional<File> getGrib2File() {
    return Optional.ofNullable(grib2File);
  }

  public Optional<File> getPolarFile() {
    return Optional.ofNullable(polarFile);
  }

  public boolean exportIsochrones() {
    return exportIsochrones;
  }

  public Optional<File> getExportIsochronesFile() {
    return Optional.ofNullable(exportIsochronesFile);
  }

  public boolean exportRoute() {
    return exportRoute;
  }

  public Optional<File> getExportRouteFile() {
    return Optional.ofNullable(exportRouteFile);
  }

  public boolean exportSailawayRoute() {
    return exportSailawayRoute;
  }

  public Optional<File> getSailawayExportRouteFile() {
    return Optional.ofNullable(exportSailawayRouteFile);
  }

  public Optional<LatLonBounds> getLegFilterBounds() {
    if (isNaN(legfilterLatitudeMax) || isNaN(legfilterLatitudeMin) ||
        isNaN(legfilterLongitudeMax) || isNaN(legfilterLongitudeMin)) {
      return Optional.empty();
    }
    else {
      return Optional.of(LatLonBounds.fromSouthWestToNorthEast(
              new LatLon(legfilterLatitudeMin, legfilterLongitudeMax),
              new LatLon(legfilterLatitudeMax, legfilterLongitudeMax)));
    }
  }

  private Optional<LatLon> parseLatLon(String string) {
    if (string==null) {
      return Optional.empty();
    }
    return Optional.of(LatLonFormat.parse(string));
  }
}
