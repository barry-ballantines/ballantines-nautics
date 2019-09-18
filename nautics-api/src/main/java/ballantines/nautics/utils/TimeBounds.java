package ballantines.nautics.utils;

import java.util.Date;

public class TimeBounds {
  private Date start;
  private Date end;

  public TimeBounds(Date start, Date end) {
    this.start = start;
    this.end = end;
  }

  public boolean contains(Date date) {
    if (this.start!=null && date.before(this.start)) {
      return false;
    }
    if (this.end!=null && date.after(this.end)) {
      return false;
    }
    return true;
  }
}
