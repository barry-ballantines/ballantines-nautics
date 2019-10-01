package ballantines.nautics.grib2;

import java.util.*;

public class TimeSeries<T> {

  private TreeMap<Date, T> map = new TreeMap<Date, T>(((d1, d2) -> d1.compareTo(d2)));

  public List<Date> getTimes() {
    return new LinkedList<>(map.keySet());
  }

  public Optional<T> get(Date time) {
    if (map.containsKey(time)) {
      return Optional.of(map.get(time));
    }
    else {
      return Optional.empty();
    }
  }

  public boolean contains(Date time) {
    return map.keySet().contains(time);
  }

  public Optional<T> getClosest(Date time) {
    if (time.before(map.firstKey()) || time.after(map.lastKey())) {
      return Optional.empty();
    }
    Date closestDate = map.firstKey();
    long smallestDelta = Math.abs(time.getTime() - closestDate.getTime());

    for (Date d : map.keySet()) {
      long delta = Math.abs(time.getTime() - d.getTime());
      if (delta <= smallestDelta) {
        closestDate = d;
        smallestDelta = delta;
      }
    }
    return get(closestDate);
  }

  public void put(Date date, T value) {
    map.put(date, value);
  }

}
