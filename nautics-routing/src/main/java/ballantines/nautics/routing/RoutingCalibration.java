package ballantines.nautics.routing;

import ballantines.nautics.routing.wind.WindField;
import ballantines.nautics.units.LatLon;
import ballantines.nautics.units.PolarVector;

import javax.measure.quantity.Speed;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static ballantines.nautics.units.NauticalUnits.*;

public class RoutingCalibration {

  public static final ZoneId UTC = ZoneId.of("UTC");
  private LocalDateTime referenceDate;
  private LatLon referencePosition;
  private WindField windField;

  public RoutingCalibration() {
    super();
  }

  public RoutingCalibration(IsochronesRouting routing) {
    RoutingContext context = routing.getContext();
    setReferenceDate(LocalDateTime.ofInstant(context.getStartingDate().toInstant(), UTC));
    setReferencePosition(context.getStartingPoint());
    setWindField(routing.getWindfield());
  }


  public void start() {
    assert referenceDate != null;
    assert referencePosition != null;
    assert windField != null;

    for (int offset= 0; offset < 6; offset++) {
      LocalDateTime date = referenceDate.minusHours(offset);
      PolarVector<Speed> wind = getWind(date);
      System.out.printf("%s :  TWD: %3.0f°    TWS: %2.1f kn %n", date, wind.getAngle(ARC_DEGREE), wind.getRadial(KNOT));
    }

  }

  protected PolarVector<Speed> getWind(LocalDateTime date) {
    return windField.getWind(referencePosition, Date.from(date.atZone(UTC).toInstant()));
  }

  public void setReferenceDate(LocalDateTime referenceDate) {
    this.referenceDate = referenceDate;
  }

  public void setWindField(WindField windField) {
    this.windField = windField;
  }

  public void setReferencePosition(LatLon referencePosition) {
    this.referencePosition = referencePosition;
  }
}
