package ballantines.nautics.grib2;

import javax.measure.Quantity;
import javax.measure.quantity.Angle;

public enum Orientation {
  NORTH_TO_SOUTH(-1),
  SOUTH_TO_NORTH(1),
  WEST_TO_EAST(1),
  EAST_TO_WEST(-1);

  private Orientation(int orientation) {
    this.orientation = orientation;
  }

  public int getOrientation() {
    return orientation;
  }

  private int orientation;
}
