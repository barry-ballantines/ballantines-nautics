package ballantines.nautics.routing;

import ballantines.nautics.routing.geoid.Geoid;
import ballantines.nautics.routing.geoid.SimpleGeoid;
import ballantines.nautics.units.LatLon;

import java.util.Date;

public class RoutingContext {
  private Geoid geoid;
  private LatLon startingPoint;
  private LatLon destinationPoint;
  private Date startingDate;

  public RoutingContext() {
    this.geoid = new SimpleGeoid();
  }

  public Geoid getGeoid() {
    return geoid;
  }

  public void setGeoid(Geoid geoid) {
    this.geoid = geoid;
  }

  public LatLon getStartingPoint() {
    return startingPoint;
  }

  public void setStartingPoint(LatLon startingPoint) {
    this.startingPoint = startingPoint;
  }

  public Date getStartingDate() {
    return startingDate;
  }

  public void setStartingDate(Date startingDate) {
    this.startingDate = startingDate;
  }

  public LatLon getDestinationPoint() {
    return destinationPoint;
  }

  public void setDestinationPoint(LatLon destinationPoint) {
    this.destinationPoint = destinationPoint;
  }
}
