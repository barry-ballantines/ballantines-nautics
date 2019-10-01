package ballantines.nautics.routing;

import ballantines.nautics.routing.filter.LatLonBoxFilter;
import ballantines.nautics.routing.polar.Polar;
import ballantines.nautics.routing.polar.PolarParser;
import ballantines.nautics.routing.wind.ConstantWindField;
import ballantines.nautics.units.LatLon;
import ballantines.nautics.units.NauticalUnits;
import ballantines.nautics.units.PolarVector;
import ballantines.nautics.utils.LatLonBounds;
import tec.units.ri.format.QuantityFormat;
import tec.units.ri.format.SimpleUnitFormat;

import javax.measure.Quantity;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Calendar;
import java.util.List;


import static ballantines.nautics.units.NauticalUnits.*;

public class RoutingMain {

  public static void main(String... args) throws Exception {

    Reader polarFileReader = new InputStreamReader(RoutingMain.class.getResourceAsStream("/test/polar/Class40.pol"));
    PolarParser parser = new PolarParser();
    Polar polar = parser.parsePolar(polarFileReader);

    IsochronesRouting routing = new IsochronesRouting();
    routing.setWindfield(new ConstantWindField(PolarVector.create(15, NauticalUnits.KNOT, 270, NauticalUnits.ARC_DEGREE)));
    routing.setPolar(polar);
    LatLonBounds bounds = LatLonBounds.fromNorthWestToSouthEast(new LatLon(degrees(45.), degrees(-80.)), new LatLon(degrees(15.0), degrees(-20.)));
    LatLonBoxFilter legFilter = new LatLonBoxFilter(bounds);
    routing.setLegFilter(legFilter);
    routing.setStartingDate(Calendar.getInstance().getTime());
    routing.setStartingPoint(new LatLon(degrees(38.5), degrees(-28.7)));
    routing.setDestinationPoint(new LatLon(degrees(21.5), degrees(-71.1)));

    Leg leg = routing.start();
    List<Leg> route = leg.getRoute();

    System.out.println("=== BEST ROUTE ===");


    QuantityFormat format = QuantityFormat.getInstance();

    for (Leg l : route) {
      StringBuilder out = new StringBuilder();
      out.append(l.time).append("\t");
      format(out, l.bearing, "%.2f").append('\t');
      format(out, l.distance, "%.2f").append("\t");
      format(out, l.endpoint.getLatitude(), "%.4f" ).append("\t");
      format(out, l.endpoint.getLongitude(), "%.4f").append("\t");
      System.out.println(out);
    }


  }

  private static StringBuilder format(StringBuilder out, Quantity quantity, String numberFormat) throws Exception {
    if (quantity==null) {
      return out.append("---");
    }
    out.append(String.format(numberFormat, quantity.getValue()));
    if (!quantity.getUnit().isCompatible(ARC_DEGREE)) {
      out.append(" ");
    }
    SimpleUnitFormat.getInstance().format(quantity.getUnit(), out);

    return out;
  }
}
