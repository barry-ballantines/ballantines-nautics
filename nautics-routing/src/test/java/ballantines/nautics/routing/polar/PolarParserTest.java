package ballantines.nautics.routing.polar;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.function.BiFunction;
import org.junit.Test;
import static org.junit.Assert.*;
import tec.units.ri.quantity.Quantities;
import static ballantines.nautics.routing.polar.DefaultPolar.*;
import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Speed;

/**
 *
 * @author mbuse
 */
public class PolarParserTest {
  

  /**
   * Test of parsePolar method, of class PolarParser.
   */
  @Test
  public void testParsePolar() throws Exception {
    System.out.println("PolarParserTest.testParsePolar()");
    Reader polarFileReader = new InputStreamReader(getClass().getResourceAsStream("/test/polar/Class40.pol"));
    
    PolarParser parser = new PolarParser();
    
    Polar polar = parser.parsePolar(polarFileReader);
    
    assertEquals(30., polar.getMaximumTrueWindSpeed().getValue().doubleValue(), 0.0);
    
    
    BiFunction<Double,Double,Double> test = (tws, twa) -> {
      final Quantity<Speed> twsQ = Quantities.getQuantity(tws, WIND_SPEED_UNIT); 
      final Quantity<Angle> twaQ = Quantities.getQuantity(twa, ANGLE_UNIT);
      Quantity<Speed> bsp = polar.getVelocity(twsQ, twaQ).getRadial();
      System.out.println(" - TWS="+twsQ+", TWA="+twaQ+" -> BSP="+bsp);
      return bsp.to(BOAT_SPEED_UNIT).getValue().doubleValue();
    };
    
    assertEquals(9.42, test.apply(18., 63.), 0.0);
    assertEquals(9.03, test.apply(10., 86.), 0.0);
    assertEquals(5.13, test.apply(4., 47.1), 0.0);
    
  }

}
