package ballantines.nautics.grib2;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public enum ProductDiscipline {

  METEOROLOGICAL_PRODUCTS(0),
  NOT_SUPPORTED(-1);

  public static ProductDiscipline valueOf(int id) {
    for (ProductDiscipline dis : values()) {
      if (dis.id == id) return dis;
    }
    return NOT_SUPPORTED;
  }

  ProductDiscipline(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public ProductParameterCategory getCategory(int catId) {
    for (ProductParameterCategory cat : categories) {
      if (cat.getId()==catId) return cat;
    }
    return ProductParameterCategory.NOT_SUPPORTED;
  }

  void registerCategory(ProductParameterCategory cat) {
    assert cat.getDiscipline() == this;
    this.categories.add(cat);
  }

  private Set<ProductParameterCategory> categories = new HashSet<>();
  private int id;
}
