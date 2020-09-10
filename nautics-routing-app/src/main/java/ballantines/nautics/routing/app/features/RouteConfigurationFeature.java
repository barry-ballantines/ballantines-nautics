package ballantines.nautics.routing.app.features;

import ballantines.nautics.routing.IsochronesRouting;
import ballantines.nautics.routing.app.Config;
import ballantines.nautics.routing.app.Input;
import ballantines.nautics.units.LatLon;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static tec.units.ri.unit.Units.HOUR;

public class RouteConfigurationFeature implements Feature {


  private LatLon start;
  private LatLon destination;
  private LocalDateTime startDateTime;
  private Quantity<Time> simulationPeriod;

  @Override
  public void prepare(Config config, Input in) {
    start = config.getStart().orElseGet(in.readLatLon(
            "Start (lat lon)       : "));
    destination = config.getDestination().orElseGet(in.readLatLon(
            "Destination (lat lon) : "));
    startDateTime = config.getStartDate().orElseGet(in.readDateTime(
            "Start Date (UTC)      : "));
    simulationPeriod = config.getSimulationPeriod().orElseGet(in.readQuantity(
            "Simulation Period (h) : ", HOUR));
    in.println();
  }

  @Override
  public void postPrepare(Input in) {
    in.printf("Start         : %s%n", start);
    in.printf("Start date    : %s%n", startDateTime);
    in.printf("Destination   : %s%n", destination);
    in.println();
    in.println("Simulation period : " + simulationPeriod);
    in.println();
  }

  @Override
  public void decorate(IsochronesRouting routing) {
    routing.setStartingPoint(start);
    routing.setDestinationPoint(destination);
    routing.setStartingDate(Date.from(startDateTime.atZone(ZoneId.of("UTC")).toInstant()));
    routing.setPeriod(simulationPeriod);
  }
}
