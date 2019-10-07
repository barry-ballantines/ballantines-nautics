package ballantines.nautics.grib2;

import ballantines.nautics.units.AngleUtil;
import ballantines.nautics.units.LatLon;
import ballantines.nautics.units.NauticalUnits;
import ballantines.nautics.utils.LatLonBounds;
import tec.units.ri.quantity.Quantities;
import ucar.grib.GribNumbers;
import ucar.grib.grib2.*;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Angle;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import static ballantines.nautics.grib2.Orientation.*;

/**
 * This class represents a GRIB2 record, containing data based on a
 * Latitude-Longitude grid.
 */
public class ProductDataGrid {

  // STATIC METHODS

  public static Optional<ProductDataGrid> fromRecord(Grib2Data data, Grib2Record record) {
    if (record.getGDS().getGdsVars().getGdtn()==0) {
      return Optional.of(new ProductDataGrid(data, record));
    }
    else {
      return Optional.empty();
    }
  }

  // MEMBERS

  private Grib2Data grib2Data;
  private Grib2Record record;
  private LatLonBounds bounds = null;
  private ProductDiscipline discipline = null;
  private ProductParameterCategory category = null;
  private ProductParameter parameter = null;

  private float[] rawdata = null;


  public ProductDataGrid(Grib2Data data, Grib2Record record) {
    this.record = record;
    this.grib2Data = data;

    assert gridDefinitionSection().getGdsVars().getGdtn()==0; // Make sure, we have a Latitude_Longitude mode
  }

  public ProductDiscipline getDiscipline() {
    if (discipline==null) {
      discipline = ProductDiscipline.valueOf(indicatorSection().getDiscipline());
    }
    return discipline;
  }

  public ProductParameterCategory getParameterCategory() {
    if (category==null) {
      category = getDiscipline().getCategory(productDefinitionSectionVars().getParameterCategory());
    }
    return category;
  }

  public ProductParameter getParameter() {
    if (parameter==null) {
      parameter = getParameterCategory().getParameter(productDefinitionSectionVars().getParameterNumber());
    }
    return parameter;
  }

  public Date getForecastDate() {
    return productDefinitionSectionVars().getForecastDate();
  }

  public LatLonBounds getBounds() {
    if (this.bounds==null) {
      Grib2GDSVariables vars = gridDefinitionSectionVars();

      assert vars.getGdtn()==0; // Make sure, we have a Latitude_Longitude mode

      int scanMode = vars.getScanMode();

      boolean westToEast = (scanMode & GribNumbers.BIT_1) == 0; // direction +i / +x
      boolean northToSouth = (scanMode & GribNumbers.BIT_2) == 0; // direction -j / -y

      float la1 = vars.getLa1();
      float la2 = vars.getLa2();
      float lo1 = vars.getLo1();
      float lo2 = vars.getLo2();

      double north = (northToSouth) ? la1 : la2;
      double south = (northToSouth) ? la2 : la1;
      double west = (westToEast) ? lo1 : lo2;
      double east = (westToEast) ? lo2 : lo1;

      this.bounds = new LatLonBounds(south, north, west, east);
    }
    return this.bounds;
  }

  public int getNumberOfPointsAlongParallel() {
    return gridDefinitionSectionVars().getNx();
  }

  public int getNumberOfPointsAlongMeridian() {
    return gridDefinitionSectionVars().getNy();
  }

  public Quantity<?> getData(LatLon position) {
    int index = getDataIndex(position);
    float value = getRawData()[index];
    Unit<?> unit = getParameter().getUnit();
    return Quantities.getQuantity(value, unit);
  }

  public int getDataIndex(LatLon position) {
    XY coords = getIndices(position);
    ScanMode scanMode = getScanMode();

    int row = (scanMode.adjacentPointsOnParallelAreConsecutive())
            ? coords.y : coords.x;
    int rowLength = (scanMode.adjacentPointsOnParallelAreConsecutive())
            ? getNumberOfPointsAlongParallel() : getNumberOfPointsAlongMeridian();
    int col = (scanMode.adjacentPointsOnParallelAreConsecutive())
            ? coords.x : coords.y;

    if (!scanMode.allRowsScanInSameDirection()) {
      boolean isAlternatedRow = (row % 2)==1; // first row (row=0) is odd
      if (isAlternatedRow) {
        col = rowLength - col;
      }
    }

    return row * rowLength + col;

  }

  public XY getIndices(LatLon position) {
    LatLonBounds bounds = getBounds();
    if (!bounds.contains(position)) {
      return null;
    }

    XY indices = new XY(-1,-1);
    ScanMode scanMode = getScanMode();

    Quantity<Angle> latitude = AngleUtil.normalizeToLowerBound(position.getLatitude(), bounds.getSouthLatitudeBound());
    Quantity<Angle> longitude = AngleUtil.normalizeToLowerBound(position.getLongitude(), bounds.getWestLongitudeBound());

    Quantity<Angle> delta = scanMode.isWestToEast()
            ? longitude.subtract(bounds.getWestLongitudeBound())
            : bounds.getEastLongitudeBound().subtract(longitude);
    indices.x = Math.round(delta.divide(getLongitudeIncrement()).getValue().floatValue());

    delta = scanMode.isNorthToSouth()
            ? bounds.getNorthLatitudeBound().subtract(latitude)
            : latitude.subtract(bounds.getSouthLatitudeBound());
    indices.y = Math.round(delta.divide(getLatitudeIncrement()).getValue().floatValue());

    return indices;
  }

  public Orientation getLongitudeOrientation() {
    ScanMode scanMode = getScanMode();
    return scanMode.isWestToEast() ? WEST_TO_EAST : EAST_TO_WEST;
  }

  public Orientation getLatitudeOrientation() {
    ScanMode scanMode = getScanMode();
    return scanMode.isNorthToSouth() ? NORTH_TO_SOUTH : SOUTH_TO_NORTH;
  }

  public Quantity<Angle> getLongitudeIncrement() {
    Grib2GDSVariables vars = gridDefinitionSection().getGdsVars();
    return NauticalUnits.degrees(vars.getDx());
  }

  public Quantity<Angle> getLatitudeIncrement() {
    Grib2GDSVariables vars = gridDefinitionSection().getGdsVars();
    return NauticalUnits.degrees(vars.getDy());
  }

  public void loadData() {
    this.rawdata = loadRawData();
  }

  public void freeData() {
    this.rawdata = null;
  }

  protected float[] getRawData() {
    if (this.rawdata!=null) {
      return this.rawdata;
    } else {
      return loadRawData();
    }
  }

  protected float[] loadRawData() {
    try {
      return grib2Data.getData(record.getGdsOffset(), record.getPdsOffset(), identificationSection().getRefTime());
    } catch (IOException e) {
      throw new RuntimeException("Invalid GRIB2 file: Failed to load raw data.", e);
    }
  }

  protected ScanMode getScanMode() {
    Grib2GDSVariables vars = gridDefinitionSection().getGdsVars();
    return new ScanMode(vars.getScanMode());
  }

  protected Grib2IndicatorSection indicatorSection() {
    return record.getIs();
  }

  protected Grib2IdentificationSection identificationSection() {
    return record.getId();
  }

  protected Grib2GridDefinitionSection gridDefinitionSection() {
    return record.getGDS();
  }

  protected Grib2GDSVariables gridDefinitionSectionVars() {
    return gridDefinitionSection().getGdsVars();
  }

  protected Grib2ProductDefinitionSection productDefinitionSection() {
    return record.getPDS();
  }

  protected Grib2Pds productDefinitionSectionVars() {
    return productDefinitionSection().getPdsVars();
  }

  // === STATIC INNER CLASSES ===

  public static class ScanMode {
    private int mode;

    ScanMode(int mode) {
      this.mode = mode;
    }

    public boolean isWestToEast() {
      return (mode & GribNumbers.BIT_1) == 0;
    }

    public boolean isNorthToSouth() {
      return (mode & GribNumbers.BIT_2) == 0;
    }

    public boolean adjacentPointsOnParallelAreConsecutive() {
      return (mode & GribNumbers.BIT_3) == 0;
    }

    public boolean allRowsScanInSameDirection() {
      return (mode & GribNumbers.BIT_4) == 0;
    }
  }

  // TUPLE of integer coordinates...
  public static class XY {
    public int x;
    public int y;

    XY(int x, int y) {
      this.x = x;
      this.y = y;
    }
  }
}
