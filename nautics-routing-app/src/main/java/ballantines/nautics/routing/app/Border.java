package ballantines.nautics.routing.app;

import ballantines.nautics.units.LatLon;
import ballantines.nautics.units.LatLonFormat;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class Border {
  private String name = "N/A";
  private List<String> locations = new ArrayList<>();


  public List<String> getLocations() {
    return locations;
  }

  public void setLocations(List<String> locations) {
    this.locations = locations;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<LatLon> getLatLons() {
    Stream<LatLon>  latLonStream = this.getLocations().stream().map(LatLonFormat::parse);
    return latLonStream.collect(Collectors.toList());
  }
}