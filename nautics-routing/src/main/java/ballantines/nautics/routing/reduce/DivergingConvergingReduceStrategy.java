package ballantines.nautics.routing.reduce;

import ballantines.nautics.routing.Leg;
import ballantines.nautics.routing.RoutingContext;
import ballantines.nautics.routing.geoid.Geoid;
import ballantines.nautics.units.AngleUtil;
import ballantines.nautics.units.PolarVector;

import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Length;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static ballantines.nautics.units.NauticalUnits.NAUTICAL_MILE;

public class DivergingConvergingReduceStrategy implements ReduceStrategy {

  private Quantity<Angle> bearingAtStart;
  private Quantity<Angle> bearingAtDestination;

  private int resolution = 1;
  private boolean maxResolution = (resolution==1);

  @Override
  public void setResolution(int resolution) {
    this.resolution = resolution;
    this.maxResolution = (resolution==1);
  }

  @Override
  public void initialize(RoutingContext context) {
    Geoid geoid = context.getGeoid();
    PolarVector<Length>  courseVectorAtStart = geoid.calculateOrthodromicDistanceAndBearing(context.getStartingPoint(), context.getDestinationPoint());
    PolarVector<Length>  courseVectorAtDestination
            = geoid.calculateOrthodromicDistanceAndBearing(context.getDestinationPoint(), context.getStartingPoint());

    this.bearingAtStart = courseVectorAtStart.getAngle();
    this.bearingAtDestination = courseVectorAtDestination.getAngle();
  }

  @Override
  public List<Leg> reduceCandidates(List<Leg> candidates) {
    Map<Integer,Leg> sectors = new TreeMap<>();

    candidates.stream().forEach(leg -> reduceCandidate(leg, sectors));

    List<Leg> reduced = new LinkedList<>(sectors.values());
    return reduced;
  }

  private void reduceCandidate(Leg actual, Map<Integer, Leg> sectors) {

    boolean actualConverging = isConverging(actual);

    // calculate sector...
    Integer sector = getSector(actual, actualConverging);

    Leg previous = sectors.get(sector);

    if (previous == null) {
      sectors.put(sector, actual);
      return; // First leg in this sector
    }

    // compare with previous...

    boolean previousConverging = isConverging(previous);

    if (actualConverging) {
      if (previousConverging) {
        double actualDistance = actual.getVectorFromDestination().getRadial(NAUTICAL_MILE);
        double previousDistance = previous.getVectorFromDestination().getRadial(NAUTICAL_MILE);
        if (actualDistance < previousDistance) {
          sectors.put(sector, actual); // actual is closer to destination
          return;
        }
      }
      else {
        sectors.put(sector, actual); // actual must be closer to destination ...
        return;
      }
    }
    else {
      if (!previousConverging) {
        double actualDistance = actual.getVectorFromStart().getRadial(NAUTICAL_MILE);
        double previousDistance = previous.getVectorFromStart().getRadial(NAUTICAL_MILE);
        if (actualDistance > previousDistance) {
          sectors.put(sector, actual); // actual is farther from start
          return;
        }
      }
    }
  }

  private boolean isConverging(Leg leg) {
    return leg.getVectorFromDestination().getRadial(NAUTICAL_MILE) < leg.getVectorFromStart().getRadial(NAUTICAL_MILE);
  }

  private Integer getSector(Leg leg, boolean isConverging) {
    Integer sector = null;
    PolarVector<Length> actualFromStart = leg.getVectorFromStart();
    PolarVector<Length> actualFromDestination = leg.getVectorFromDestination();

    // calculate sector...
    if (isConverging) {
      Quantity<Angle> delta = actualFromDestination.getAngle().subtract(bearingAtDestination);
      sector = - roundToSector(AngleUtil.normalizeToUpperBound(delta, AngleUtil.DEGREE_180).getValue().floatValue());
    } else {
      Quantity<Angle> delta = actualFromStart.getAngle().subtract(bearingAtStart);
      sector = roundToSector(AngleUtil.normalizeToUpperBound(delta,AngleUtil.DEGREE_180).getValue().floatValue());
    }
    if (sector==-180) {
      sector = 180; // close the circle
    }

    return sector;
  }

  private int roundToSector(float rawAngle) {
    return (maxResolution) ? Math.round(rawAngle) : resolution * Math.round(rawAngle / resolution);
  }
}
