/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ballantines.nautics.units;

import java.awt.geom.Point2D;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Angle;

/**
 *
 * @author mbuse
 */
public class LatLon {
  
  
  private final Quantity<Angle> lat;
  private final Quantity<Angle> lon;
  
  
  public LatLon(double lat, double lon) {
    this(Quantities.getQuantity(lat, NauticalUnits.ARC_DEGREE), Quantities.getQuantity(lat, NauticalUnits.ARC_DEGREE));
  }
  
  public LatLon(Quantity<Angle> lat, Quantity<Angle> lon) {
    this.lat = lat;
    this.lon = lon;
  }

  public Quantity<Angle> getLatitude() {
    return lat;
  }

  public Quantity<Angle> getLongitude() {
    return lon;
  }
  
  public Point2D toPoint2D() {
    return new Point2D() {
      @Override
      public double getX() {
        return lon.to(NauticalUnits.ARC_DEGREE).getValue().doubleValue();
      }

      @Override
      public double getY() {
        return lat.to(NauticalUnits.ARC_DEGREE).getValue().doubleValue();
      }

      @Override
      public void setLocation(double x, double y) {
        throw new UnsupportedOperationException("Not supported yet.");
      }
      
    }; 
  } 
  
  public static LatLon fromStrings(String lat, String lon) {
    return new LatLon(
            Quantities.getQuantity(lat).asType(Angle.class),
            Quantities.getQuantity(lon).asType(Angle.class));
  }
  
}
