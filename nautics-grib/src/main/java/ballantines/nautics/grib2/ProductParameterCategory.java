package ballantines.nautics.grib2;

import java.util.HashSet;
import java.util.Set;

import static ballantines.nautics.grib2.ProductDiscipline.*;

public enum ProductParameterCategory {
  TEMPERATURE(METEOROLOGICAL_PRODUCTS, 0),
  MOMENTUM(METEOROLOGICAL_PRODUCTS, 2),
  MASS(METEOROLOGICAL_PRODUCTS, 3),

  NOT_SUPPORTED(ProductDiscipline.NOT_SUPPORTED, -1);


  ProductParameterCategory(ProductDiscipline discipline, int id) {
    this.discipline = discipline;
    this.id = id;
  }


  public int getId() {
    return id;
  }

  public ProductDiscipline getDiscipline() {
    return discipline;
  }

  public ProductParameter getParameter(int id) {
    for (ProductParameter parameter : ProductParameter.values()) {
      if (this==parameter.getCategory() && id == parameter.getId()) {
        return parameter;
      }
    }
    return ProductParameter.NOT_SUPPORTED;
  }

  private ProductDiscipline discipline;
  private int id;
}
