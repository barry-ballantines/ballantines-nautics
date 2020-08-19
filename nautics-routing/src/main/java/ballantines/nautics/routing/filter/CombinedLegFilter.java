package ballantines.nautics.routing.filter;

import ballantines.nautics.routing.Leg;

import java.util.LinkedList;
import java.util.List;

public class CombinedLegFilter implements LegFilter {

  private List<LegFilter> filters = new LinkedList<>();

  public void add(LegFilter filter) {
    filters.add(filter);
  }

  @Override
  public boolean accept(Leg leg) {
    return filters.stream().allMatch(filter -> filter.accept(leg));
  }
}
