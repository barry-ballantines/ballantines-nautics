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
    this(Quantities.getQuantity(lat, NauticalUnits.ARC_DEGREE), Quantities.getQuantity(lon, NauticalUnits.ARC_DEGREE));
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

  public boolean isEastOf(Quantity<Angle> lon) {
    Quantity<Angle> delta = AngleUtil.delta(lon, this.getLongitude()).to(NauticalUnits.ARC_DEGREE);
    return delta.getValue().doubleValue() > 0;
  }
  public boolean isWestOf(Quantity<Angle> lon) {
    Quantity<Angle> delta = AngleUtil.delta(this.getLongitude(), lon).to(NauticalUnits.ARC_DEGREE);
    return delta.getValue().doubleValue() > 0;
  }

  public boolean isEastOf(LatLon that) {
    return this.isEastOf(that.getLongitude());
  }
  public boolean isWestOf(LatLon that) {
    return this.isWestOf(that.getLongitude());
  }

  public boolean isNorthOf(Quantity<Angle> lat) {
    Quantity<Angle> delta = AngleUtil.delta(lat, this.getLatitude()).to(NauticalUnits.ARC_DEGREE);
    return delta.getValue().doubleValue() > 0;
  }

  public boolean isSouthOf(Quantity<Angle> lat) {
    Quantity<Angle> delta = AngleUtil.delta(this.getLatitude(), lat).to(NauticalUnits.ARC_DEGREE);
    return delta.getValue().doubleValue() > 0;
  }
  public boolean isNorthOf(LatLon that) {
    return this.isNorthOf(that.getLatitude());
  }
  public boolean isSouthOf(LatLon that) {
    return this.isSouthOf(that.getLatitude());
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

  public String toString() {
    return "(lat: " + lat + ", lon: " + lon + ")";
  }
  
  public static LatLon fromStrings(String lat, String lon) {
    return new LatLon(
            Quantities.getQuantity(lat).asType(Angle.class),
            Quantities.getQuantity(lon).asType(Angle.class));
  }
  
}
