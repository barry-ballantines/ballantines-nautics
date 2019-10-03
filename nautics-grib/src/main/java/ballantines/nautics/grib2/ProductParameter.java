package ballantines.nautics.grib2;


import javax.measure.Unit;

import static ballantines.nautics.grib2.ProductParameterCategory.*;
import static ballantines.nautics.units.NauticalUnits.ARC_DEGREE;
import static tec.units.ri.unit.Units.*;
public enum ProductParameter {
  WIND_DIRECTION(0, MOMENTUM, ARC_DEGREE),
  WIND_SPEED(1, MOMENTUM, METRE_PER_SECOND),
  U_COMPONENT_OF_WIND(2, MOMENTUM, METRE_PER_SECOND),
  V_COMPONENT_OF_WIND(3, MOMENTUM, METRE_PER_SECOND),

  PRESSURE(0, MASS, PASCAL),
  PRESSURE_REDUCED_TO_MSL(1, MASS, PASCAL),

  NOT_SUPPORTED(-1, ProductParameterCategory.NOT_SUPPORTED, null);

  private ProductParameter(int id, ProductParameterCategory category, Unit unit) {
    this.category = category;
    this.id = id;
    this.unit = unit;
  }

  public int getId() {
    return id;
  }

  public Unit getUnit() {
    return unit;
  }

  public ProductParameterCategory getCategory() {
    return category;
  }

  private int id;
  private ProductParameterCategory category;
  private Unit unit;

}
