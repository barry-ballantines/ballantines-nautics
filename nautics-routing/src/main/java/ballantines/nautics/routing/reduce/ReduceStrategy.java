package ballantines.nautics.routing.reduce;

import ballantines.nautics.routing.Leg;
import ballantines.nautics.routing.RoutingContext;

import java.util.List;

public interface ReduceStrategy {

  default void initialize(RoutingContext context) {
    // do nothing...
  }

  /**
   * the angular resolution used to reduce isochrone candidates in degrees (defaults to 1°).
   *
   * @param resolution
   */
  default void setResolution(int resolution) {
    // do nothing...
  }

  /**
   * This method reduces the candidates to a sorted isochrone.
   * @param candidates - all possible legs that contribute to an isochrone
   * @return the actual isochrone - the isochrones are sorted
   */
  List<Leg> reduceCandidates(List<Leg> candidates);
}
