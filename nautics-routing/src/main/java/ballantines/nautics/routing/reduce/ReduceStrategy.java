package ballantines.nautics.routing.reduce;

import ballantines.nautics.routing.Leg;
import ballantines.nautics.routing.RoutingContext;

import java.util.List;

public interface ReduceStrategy {

  default void initialize(RoutingContext context) {
    // do nothing...
  }

  /**
   * This method reduces the candidates to a sorted isochrone.
   * @param candidates - all possible legs that contribute to an isochrone
   * @return the actual isochrone - the isochrones are sorted
   */
  List<Leg> reduceCandidates(List<Leg> candidates);
}
