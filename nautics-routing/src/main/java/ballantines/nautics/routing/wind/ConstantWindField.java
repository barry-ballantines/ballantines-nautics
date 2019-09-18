package ballantines.nautics.routing.wind;

import ballantines.nautics.units.LatLon;
import ballantines.nautics.units.PolarVector;
import ballantines.nautics.utils.LatLonBounds;
import ballantines.nautics.utils.TimeBounds;

import javax.measure.quantity.Speed;
import java.util.Date;

/**
 * A very basic implementation of WindField.
 *
 * can be used for testing or as fallback wind field.
 */
public class ConstantWindField implements WindField {

  private PolarVector<Speed> windVector;
  private TimeBounds timeBounds;
  private LatLonBounds latLonBounds;

  public ConstantWindField(PolarVector<Speed> windVector) {
    this(windVector, null, null);
  }

  public ConstantWindField(PolarVector<Speed> windVector, TimeBounds timeBounds, LatLonBounds latLonBounds) {
    this.windVector = windVector;
    this.timeBounds = timeBounds;
    this.latLonBounds = latLonBounds;
  }

  @Override
  public boolean supports(LatLon position, Date time) {
    return (timeBounds==null || timeBounds.contains(time))
            && (latLonBounds==null || latLonBounds.contains(position));
  }

  @Override
  public PolarVector<Speed> getWind(LatLon position, Date time) {
    return windVector;
  }
}
