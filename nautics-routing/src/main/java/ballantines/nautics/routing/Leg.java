/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ballantines.nautics.routing;

import ballantines.nautics.units.LatLon;

import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Length;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author mbuse
 */
public class Leg {
  
  public LatLon endpoint;
  public Quantity<Angle> bearing;
  public Quantity<Length> distance;
  
  public Date time;
  
  public Leg parent = null;
  
  public Quantity<Length> distanceFromStart;
  public Quantity<Angle> bearingFromStart;

  public List<Leg> getRoute() {
    List<Leg> route = new LinkedList<Leg>();
    addToRoute(route);
    return route;
  }

  public int totalNumberOfLegs() {
    return (parent==null) ? 1 : parent.totalNumberOfLegs() +1;
  }

  public Quantity<Length> totalDistance() {
    return (parent==null) ? distance : parent.totalDistance().add(distance);
  }

  private void addToRoute(List<Leg> route) {
    if (parent!=null) {
      parent.addToRoute(route);
    }
    route.add(this);
  }
}
