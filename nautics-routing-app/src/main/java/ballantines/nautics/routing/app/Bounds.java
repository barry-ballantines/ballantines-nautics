package ballantines.nautics.routing.app;

import ballantines.nautics.units.ArcDegreeFormat;
import ballantines.nautics.utils.LatLonBounds;
import org.springframework.context.annotation.Configuration;

import javax.measure.Quantity;
import javax.measure.quantity.Angle;

import static ballantines.nautics.units.NauticalUnits.ARC_DEGREE;

@Configuration
public class Bounds {

  private String name = "N/A";
  private boolean enabled = true;
  private String north;
  private String south;
  private String east;
  private String west;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public String getNorth() {
    return north;
  }

  public void setNorth(String north) {
    this.north = north;
  }

  public String getSouth() {
    return south;
  }

  public void setSouth(String south) {
    this.south = south;
  }

  public String getEast() {
    return east;
  }

  public void setEast(String east) {
    this.east = east;
  }

  public String getWest() {
    return west;
  }

  public void setWest(String west) {
    this.west = west;
  }

  public String toString() {
    return String.format("%s: %s - %s, %s - %s", name, south, north, west, east);
  }

  public LatLonBounds toLatLonBounds() {
    return new LatLonBounds(degree(south), degree(north), degree(west), degree(east));
  }

  private double degree(String notation) {
    Quantity<Angle> angle = ArcDegreeFormat.parseAngle(notation);
    return angle.to(ARC_DEGREE).getValue().doubleValue();
  }
}
