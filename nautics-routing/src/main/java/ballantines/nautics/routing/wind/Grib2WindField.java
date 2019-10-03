package ballantines.nautics.routing.wind;

import ballantines.nautics.grib2.GRIB2;
import ballantines.nautics.grib2.ProductCollection;
import ballantines.nautics.grib2.ProductDataGrid;
import ballantines.nautics.grib2.TimeSeries;
import ballantines.nautics.units.LatLon;
import ballantines.nautics.units.PolarVector;
import ballantines.nautics.utils.LatLonBounds;

import javax.measure.Quantity;
import javax.measure.quantity.Speed;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static ballantines.nautics.grib2.ProductParameter.U_COMPONENT_OF_WIND;
import static ballantines.nautics.grib2.ProductParameter.V_COMPONENT_OF_WIND;
import static ballantines.nautics.units.NauticalUnits.KNOT;

public class Grib2WindField implements WindField {

  /**
   * Constructs a Wind Field based on the data of the given GRIB2 file.
   * @param grib2File A GRIB2 file containing time series of U and V components of wind.
   */
  public Grib2WindField(File grib2File) throws IOException {
    GRIB2 grib = new GRIB2(grib2File);
    this.timeSeries = grib.getProductTimeSeries();
    // check...

    if (timeSeries.getTimes().size()==0) {
      throw new IllegalArgumentException("Invalid GRIB2 file: No forecasts found.");
    };
    for (Date time : timeSeries.getTimes()) {
      ProductCollection collection = timeSeries.get(time).get();
      if (! collection.contains(U_COMPONENT_OF_WIND, V_COMPONENT_OF_WIND)) {
        throw new IllegalArgumentException("Invalid GRIB2 file: No products 'u/v compoents of winds' for forecast time " + time + ".");
      }
    }
  }

  public LatLonBounds getBounds() {
    return timeSeries.get(timeSeries.getTimes().get(0))
            .orElseThrow(() -> new RuntimeException("Invalid GRIB2 file: No forecasts found."))
            .get(U_COMPONENT_OF_WIND)
            .orElseThrow(() -> new RuntimeException("Invalid GRIB2 file: No U_COMPONENT_OF_WIND."))
            .getBounds();
  }

  public List<Date> getForecastTimes() {
    return timeSeries.getTimes();
  }

  @Override
  public boolean supports(LatLon position, Date time) {
    if (!timeSeries.isTimeInRange(time)) {
      return false;
    }
    loadProductData(time);
    if (uWindComponentGrid.getBounds().contains(position) && vWindComponentGrid.getBounds().contains(position)) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public PolarVector<Speed> getWind(LatLon position, Date time) {
    loadProductData(time);
    Quantity<Speed> uWind = uWindComponentGrid.getData(position).asType(Speed.class).to(KNOT);
    Quantity<Speed> vWind = vWindComponentGrid.getData(position).asType(Speed.class).to(KNOT);
    return PolarVector.createFromCartesianCoordinates(uWind, vWind);
  }

  private void loadProductData(Date time) {
    Date forecastTime = timeSeries.getClosestForecastTime(time);
    if (isCachedProductDataValid(forecastTime)) {
      return;
    }
    freeProductData();
    ProductCollection products = timeSeries.get(forecastTime)
            .orElseThrow(() -> new IllegalArgumentException("Invalid GRIB2 file: No products for forecast time " + time + "."));
    this.uWindComponentGrid = products.get(U_COMPONENT_OF_WIND)
            .orElseThrow(()-> new IllegalArgumentException("Invalid GRIB2 file: No U_COMPONENT_OF_WIND for forecast time " + time + "."));
    this.vWindComponentGrid = products.get(V_COMPONENT_OF_WIND)
            .orElseThrow(()-> new IllegalArgumentException("Invalid GRIB2 file: No V_COMPONENT_OF_WIND for forecast time " + time + "."));

    this.uWindComponentGrid.loadData();
    this.vWindComponentGrid.loadData();
  }


  public void freeProductData() {
    if (uWindComponentGrid !=null) {
      uWindComponentGrid.freeData();
      uWindComponentGrid = null;
    }
    if (vWindComponentGrid !=null) {
      vWindComponentGrid.freeData();
      vWindComponentGrid = null;
    }
  }

  private boolean isCachedProductDataValid(Date forecastTime) {
    if (uWindComponentGrid ==null || vWindComponentGrid ==null) {
      return false;
    }
    return  uWindComponentGrid.getForecastDate().equals(forecastTime) && vWindComponentGrid.getForecastDate().equals((forecastTime));
  }

  /** The GRIB2 file as a time series of product data grids **/
  private TimeSeries<ProductCollection> timeSeries;

  /** The current product data grid, representing the u-component of wind (West to East) **/
  private ProductDataGrid uWindComponentGrid = null;

  /** The current product data grid, representing the v-component of wind (South to North) **/
  private ProductDataGrid vWindComponentGrid = null;
}
