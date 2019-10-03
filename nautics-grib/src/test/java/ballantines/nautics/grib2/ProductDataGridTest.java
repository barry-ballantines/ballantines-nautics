package ballantines.nautics.grib2;

import ballantines.nautics.grib2.util.ResourcesFileExportUtil;
import ballantines.nautics.units.LatLon;
import ballantines.nautics.units.NauticalUnits;
import ballantines.nautics.units.PolarVector;
import ballantines.nautics.utils.LatLonBounds;
import org.junit.*;


import javax.measure.Quantity;
import javax.measure.quantity.Speed;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class ProductDataGridTest {

  private File gribFile;
  private GRIB2 grib;

  @BeforeClass
  public static void setup() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
  }

  @Before
  public void prepare() throws IOException {
    gribFile = ResourcesFileExportUtil.exportResourceToTmpFile("/grib2/example.grb2");
    System.out.println("Downloading test GRIB file to " + gribFile);
    grib = new GRIB2(gribFile);
  }

  @Test
  public void testGetLatitudeLongitudeDataGrids() throws Throwable {
    List<ProductDataGrid> grids = grib.getLatitudeLongitudeDataGrids();

    System.out.println("LatitudeLongitude Grids found: " + grids.size());

    for (ProductDataGrid g : grids) {
      System.out.println("-----------------------------------------");
      System.out.println("          Discipline : " + g.getDiscipline());
      System.out.println("            Category : " + g.getParameterCategory());
      System.out.println("   Product Parameter : " + g.getParameter());
      System.out.println("       Forecast date : " + g.getForecastDate());
      System.out.println("              Bounds : " + g.getBounds());
      System.out.println("       Lat-Increment : " + g.getLatitudeIncrement() + ", " + g.getLatitudeOrientation());
      System.out.println("       Lon-Increment : " + g.getLongitudeIncrement() + ", " + g.getLongitudeOrientation());
    }
  }

  @Test
  public void testGetIndeces() throws Throwable {
    ProductDataGrid grid = grib.getLatitudeLongitudeDataGrids().get(0);
    LatLonBounds bounds = grid.getBounds();
    LatLon nw = new LatLon(bounds.getNorthLatitudeBound(), bounds.getWestLongitudeBound());
    LatLon ne = new LatLon(bounds.getNorthLatitudeBound(), bounds.getEastLongitudeBound());
    LatLon sw = new LatLon(bounds.getSouthLatitudeBound(), bounds.getWestLongitudeBound());
    LatLon se = new LatLon(bounds.getSouthLatitudeBound(), bounds.getEastLongitudeBound());

    Function<LatLon, String> render = (pos) -> {
      ProductDataGrid.XY indices = grid.getIndices(pos);
      int dataIndex = grid.getDataIndex(pos);
      return "(x:" + indices.x + ", y:" + indices.y + ") -> dataIndex:" + dataIndex;
    };

    System.out.println("=== INDICES ========================================");
    System.out.println(" South West -> " + render.apply(sw));
    System.out.println(" South East -> " + render.apply(se));
    System.out.println(" North West -> " + render.apply(nw));
    System.out.println(" North East -> " + render.apply(ne));

    ProductDataGrid.XY xy;
    int dataIndex;

    xy = grid.getIndices(sw);
    dataIndex = grid.getDataIndex(sw);
    Assert.assertEquals(0, xy.x);
    Assert.assertEquals(0, xy.y);
    Assert.assertEquals(0, dataIndex);

    xy = grid.getIndices(se);
    dataIndex = grid.getDataIndex(se);
    Assert.assertEquals(234, xy.x);
    Assert.assertEquals(0, xy.y);
    Assert.assertEquals(234, dataIndex);

    xy = grid.getIndices(nw);
    dataIndex = grid.getDataIndex(nw);
    Assert.assertEquals(0, xy.x);
    Assert.assertEquals(117, xy.y);
    Assert.assertEquals(235 * 117, dataIndex);

    xy = grid.getIndices(ne);
    dataIndex = grid.getDataIndex(ne);
    Assert.assertEquals(234, xy.x);
    Assert.assertEquals(117, xy.y);
    Assert.assertEquals(235 * 118 - 1, dataIndex);
  }

  @Test
  public void testGetData() throws IOException {
    List<ProductDataGrid> grids = grib.getLatitudeLongitudeDataGrids();
    for (ProductDataGrid grid : grids) {
      LatLonBounds bounds = grid.getBounds();
      LatLon nw = new LatLon(bounds.getNorthLatitudeBound(), bounds.getWestLongitudeBound());
      LatLon ne = new LatLon(bounds.getNorthLatitudeBound(), bounds.getEastLongitudeBound());
      LatLon sw = new LatLon(bounds.getSouthLatitudeBound(), bounds.getWestLongitudeBound());
      LatLon se = new LatLon(bounds.getSouthLatitudeBound(), bounds.getEastLongitudeBound());

      grid.loadData();
      System.out.println("=============================================================");
      System.out.println("  Forcast of    : " + grid.getParameter());
      System.out.println("  Forecast Date : " + grid.getForecastDate());
      System.out.println("");
      System.out.println(" " + nw + " : " + grid.getData(nw) );
      System.out.println(" " + ne + " : " + grid.getData(ne) );
      System.out.println(" " + sw + " : " + grid.getData(sw) );
      System.out.println(" " + se + " : " + grid.getData(se) );

      grid.freeData();
    }
  }

  @Test
  public void testTimeSeries() throws IOException {
    TimeSeries<ProductCollection> timeSeries = grib.getProductTimeSeries();
    for (Date time : timeSeries.getTimes()) {
      ProductCollection products = timeSeries.get(time).get();
      ProductDataGrid uWind = products.get(ProductParameter.U_COMPONENT_OF_WIND).get();
      ProductDataGrid vWind = products.get(ProductParameter.V_COMPONENT_OF_WIND).get();

      uWind.loadData();
      vWind.loadData();

      LatLonBounds bounds = uWind.getBounds();
      LatLon nw = new LatLon(bounds.getNorthLatitudeBound(), bounds.getWestLongitudeBound());
      LatLon ne = new LatLon(bounds.getNorthLatitudeBound(), bounds.getEastLongitudeBound());
      LatLon sw = new LatLon(bounds.getSouthLatitudeBound(), bounds.getWestLongitudeBound());
      LatLon se = new LatLon(bounds.getSouthLatitudeBound(), bounds.getEastLongitudeBound());

      LatLon[] positions = new LatLon[] { nw, ne, sw, se };

      System.out.println("==========================================================");
      System.out.println("      Forecast date : " + time);

      for (LatLon pos : positions) {

        Quantity<Speed> uSpeed = uWind.getData(pos).asType(Speed.class).to(NauticalUnits.KNOT);
        Quantity<Speed> vSpeed = vWind.getData(pos).asType(Speed.class).to(NauticalUnits.KNOT);

        PolarVector<Speed> polar = PolarVector.createFromCartesianCoordinates(uSpeed, vSpeed).reverse(); // Wind direction is reverse to speed direction

        System.out.println("");
        System.out.println("  Forecast position : " + pos);
        System.out.println("               Wind : " + polar);
      }

      uWind.freeData();
      vWind.freeData();
    }

  }

  @After
  public void cleanup() throws IOException {
    gribFile.deleteOnExit();
  }

}
