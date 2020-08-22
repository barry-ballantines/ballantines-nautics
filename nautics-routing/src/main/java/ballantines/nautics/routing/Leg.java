/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ballantines.nautics.routing;

import ballantines.nautics.routing.geoid.Geoid;
import ballantines.nautics.units.LatLon;
import ballantines.nautics.units.PolarVector;

import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Length;
import javax.measure.quantity.Speed;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static ballantines.nautics.units.NauticalUnits.nauticalMiles;

/**
 *
 * @author mbuse
 */
public class Leg {

  // Statics ...

  public static Leg createStartingLeg(Geoid geoid, LatLon startingPosition, Date startingDate) {
    Leg start = new Leg();
    start.geoid = geoid;
    start.endpoint = startingPosition;
    start.bearing = null;
    start.distance = nauticalMiles(0.0);
    start.parent = null;
    start.time = startingDate;
    start.bearingFromStart = null;
    start.distanceFromStart = nauticalMiles(0.0);
    return start;
  }

  public static Leg createChild(Leg parent, Date time, Quantity<Length> distance, Quantity<Angle> bearing, PolarVector<Speed> trueWind, Quantity<Speed> boatSpeed) {
    Leg seg = new Leg();

    seg.geoid = parent.geoid;
    seg.parent = parent;
    seg.time = time;
    seg.distance = distance;
    seg.bearing = bearing;
    seg.endpoint = seg.geoid.calculateDestination(parent.endpoint, bearing, distance);
    seg.wind = trueWind;
    seg.boatSpeed = boatSpeed;

    return seg;
  }

  // Instance...

  public Geoid geoid;
  public LatLon endpoint;
  public Quantity<Angle> bearing;
  public Quantity<Length> distance;
  public PolarVector<Speed> wind;
  public Quantity<Speed> boatSpeed;
  
  public Date time;
  
  public Leg parent = null;
  
  public Quantity<Length> distanceFromStart;
  public Quantity<Angle> bearingFromStart;

  public Quantity<Length> distanceToDestination;
  public Quantity<Angle> bearingToDestination;

  public List<Leg> getRoute() {
    List<Leg> route = new LinkedList<Leg>();
    addToRoute(route);
    return route;
  }

  public LatLon getStart() {
    if (this.parent==null) {
      return endpoint;
    }
    return this.parent.getStart();
  }

  public LatLon getDestination() {
    return endpoint;
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
