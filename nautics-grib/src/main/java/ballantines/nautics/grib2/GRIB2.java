package ballantines.nautics.grib2;

import ucar.grib.grib2.Grib2Data;
import ucar.grib.grib2.Grib2Input;
import ucar.grib.grib2.Grib2Record;
import ucar.unidata.io.RandomAccessFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GRIB2 {

  static {
    // make sure that all enums are loaded correctly...
    assert ProductParameter.NOT_SUPPORTED.getCategory().getDiscipline() == ProductDiscipline.NOT_SUPPORTED;
  }

  private RandomAccessFile randomAccessFile;
  private Grib2Data grib2Data;

  public GRIB2(File file) throws IOException  {
    this.randomAccessFile = new RandomAccessFile(file.getAbsolutePath(), "r");
    this.grib2Data = new Grib2Data(randomAccessFile);

  }

  public TimeSeries<ProductCollection> getProductTimeSeries() throws IOException {
    TimeSeries<ProductCollection> timeSeries = new TimeSeries<>();
    for (ProductDataGrid grid : getLatitudeLongitudeDataGrids()) {
      Date forecastDate = grid.getForecastDate();
      ProductParameter product = grid.getParameter();
      if (product == ProductParameter.NOT_SUPPORTED) continue;

      ProductCollection products = timeSeries.get(forecastDate).orElse(new ProductCollection());
      products.add(grid);

      if (!timeSeries.contains(forecastDate)) {
        timeSeries.put(forecastDate, products);
      }
    }
    return timeSeries;
  }

  public List<ProductDataGrid> getLatitudeLongitudeDataGrids() throws IOException {
    List<ProductDataGrid> grids = new LinkedList<>();
    Grib2Input input = getInput();
    for (Grib2Record record : input.getRecords()) {
      Optional<ProductDataGrid> optionalGrid = ProductDataGrid.fromRecord(this.grib2Data, record);
      if (optionalGrid.isPresent()) {
        grids.add(optionalGrid.get());
      }
    }
    return grids;
  }

  public Grib2Input getInput() throws IOException {
    randomAccessFile.order(RandomAccessFile.BIG_ENDIAN);
    Grib2Input g2i = new Grib2Input(randomAccessFile);
    g2i.scan(false, false);
    return g2i;
  }

  public RandomAccessFile getRandomAccessFile() {
    return randomAccessFile;
  }

  public Grib2Data getGrib2Data() {
    return grib2Data;
  }

}
