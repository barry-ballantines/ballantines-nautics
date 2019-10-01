package ballantines.nautics.grib2;

import java.util.*;

public class ProductCollection {

  private Map<ProductParameter, ProductDataGrid> map = new HashMap<>();

  public Set<ProductParameter> getSupportedProducts() {
    return Collections.unmodifiableSet(map.keySet());
  }

  public Optional<ProductDataGrid> get(ProductParameter product) {
    if (map.containsKey(product)) {
      return Optional.of(map.get(product));
    }
    else {
      return Optional.empty();
    }
  }

  public void add(ProductDataGrid grid) {
    map.put(grid.getParameter(), grid);
  }


}
