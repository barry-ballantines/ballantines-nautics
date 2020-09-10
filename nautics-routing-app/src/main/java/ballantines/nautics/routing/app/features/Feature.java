package ballantines.nautics.routing.app.features;

import ballantines.nautics.routing.IsochronesRouting;
import ballantines.nautics.routing.app.Config;
import ballantines.nautics.routing.app.Input;

public interface Feature {

  void prepare(Config config, Input in);

  default void postPrepare(Input in) {};

  void decorate(IsochronesRouting routing);

}
