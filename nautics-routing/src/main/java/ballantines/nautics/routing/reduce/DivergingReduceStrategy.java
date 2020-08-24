package ballantines.nautics.routing.reduce;

import ballantines.nautics.routing.Leg;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static ballantines.nautics.units.NauticalUnits.ARC_DEGREE;
import static ballantines.nautics.units.NauticalUnits.NAUTICAL_MILE;

/**
 * This strategy reduces the candidate legs for an isochrone by their bearing and distance from start.
 *
 * (1) the candidates are divided into 1° sectors based on their bearing from start.
 * (2) for every sector the leg with the greatest endpoint distance from start is choosen.
 *
 * This strategy is diverging, for the distance between the endpoints of the choosen legs is increasing with the
 * distance from the start. There is no convergence towards the destination of the route.
 */
public class DivergingReduceStrategy implements ReduceStrategy {

  @Override
  public List<Leg> reduceCandidates(List<Leg> candidates) {
    Map<Integer,Leg> map = new TreeMap<>();

    candidates.stream().forEach(leg -> {
      Integer sector = getSector(leg);

      Leg previous = map.get(sector);
      if (previous==null ||
              leg.getVectorFromStart().getRadial(NAUTICAL_MILE) > previous.getVectorFromStart().getRadial(NAUTICAL_MILE)) {
        map.put(sector, leg);
      }
    });

    List<Leg> reduced = new LinkedList<>(map.values());
    return reduced;
  }

  private Integer getSector(Leg leg) {
    Integer sector = Math.round(leg.getVectorFromStart().getAngle().to(ARC_DEGREE).getValue().floatValue());
    if (sector==-180) {
      sector = 180; // close the circle
    }
    return sector;
  }
}
