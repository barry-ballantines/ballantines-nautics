/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ballantines.nautics.routing;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import ballantines.nautics.routing.filter.LegFilter;
import ballantines.nautics.routing.geoid.Geoid;
import ballantines.nautics.routing.geoid.SimpleGeoid;
import ballantines.nautics.routing.polar.Polar;
import ballantines.nautics.routing.wind.WindField;
import ballantines.nautics.units.LatLon;
import ballantines.nautics.units.PolarVector;
import tec.units.ri.quantity.Quantities;


import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Length;
import javax.measure.quantity.Speed;
import javax.measure.quantity.Time;

import static ballantines.nautics.units.NauticalUnits.*;
import static tec.units.ri.unit.Units.HOUR;
import static tec.units.ri.unit.Units.MINUTE;
import static tec.units.ri.unit.Units.SECOND;


/**
 *
 * @author mbuse
 */
public class IsochronesRouting {
  
  private final static int METERS_PER_SM = 1852;
  
  //private final GeodeticCalculator calculator = new GeodeticCalculator();
  
  private LatLon startingPoint;
  private LatLon destinationPoint;
  private Date   startingDate;
  private Polar polar;
  private WindField windfield;
  private LegFilter legFilter;
  private Quantity<Time> period = Quantities.getQuantity(6.0, HOUR);
  private IsochronesListener isochronesListener;

  private Geoid geoid = new SimpleGeoid();
  
  // === METHODS ===
  
  public Leg start() {
    Leg start = new Leg();
    start.endpoint = startingPoint;
    start.bearing = null;
    start.distance = nauticalMiles(0.0);
    start.parent = null;
    start.time = startingDate;
    start.bearingFromStart = null;
    start.distanceFromStart = nauticalMiles(0.0);
    
    // START LOOPING...
    
    Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
    calendar.setTime(startingDate);
    List<Leg> isochrone = Collections.singletonList(start);
    Leg winningLeg = findWinningLegOrNull(isochrone);
    
    while (winningLeg==null && !isochrone.isEmpty()) {
      calendar.add(Calendar.HOUR, period.to(HOUR).getValue().intValue());
      isochrone = findNextIsochrone(isochrone, calendar.getTime());
      
      if (isochronesListener!=null) {
        isochronesListener.isochronesCalculated(calendar.getTime(), isochrone);
      }
      
      winningLeg = findWinningLegOrNull(isochrone);
    }
    
    if (isochronesListener!=null) {
      if (winningLeg!=null) {
        isochronesListener.winningLegFound(winningLeg);
      }
      else {
        isochronesListener.noLegFound();
      }
    }
    
    return winningLeg;
    
  }
  
  private List<Leg> findNextIsochrone(List<Leg> lastIsochrone, Date time) {
    List<Leg> candidates = new LinkedList<>();
    for (Leg ref : lastIsochrone) {
      calculateCandidates(ref, time, candidates);
    }
    return reduceIsochrones(candidates);
  }
  
  private void calculateCandidates(Leg reference, Date time, List<Leg> candidates) {
    if (!windfield.supports(reference.endpoint, reference.time)) {
      return; // there is no wind data...
    }
    PolarVector<Speed> trueWind = windfield.getWind(reference.endpoint, reference.time);
    
    List<Leg> newCandidates = new LinkedList<>();
    for (int angle = -179; angle <= 180; angle++) {
      Quantity<Angle> bearing = degrees((double) angle);
      PolarVector<Speed> velocity = polar.getVelocity(trueWind.getRadial(), twa(bearing, trueWind.getAngle())); // sm/h
      Quantity<Length> distance = velocity.getRadial().multiply(period).asType(Length.class);
      
      LatLon endpoint = this.geoid.calculateDestination(reference.endpoint, bearing, distance);
      
      Leg seg = new Leg();
      seg.parent = reference;
      seg.time = time;
      seg.distance = distance;
      seg.bearing = bearing;
      seg.endpoint = endpoint;
      
      newCandidates.add(seg);
    }
    newCandidates = filterCandidates(newCandidates);
    calculateBearingAndDistanceFromStart(newCandidates);
    candidates.addAll(newCandidates);
    
  }
  
  private List<Leg> filterCandidates(List<Leg> candidates) {
    if (this.legFilter!=null) {
      return candidates.stream().filter(leg -> this.legFilter.accept(leg)).collect(Collectors.toList());
    }
    return candidates;
  }
  
  private void calculateBearingAndDistanceFromStart(List<Leg> candidates) {
    for (Leg leg : candidates) {
      PolarVector<Length> vector = this.geoid.calculateOrthodromicDistanceAndBearing(startingPoint, leg.endpoint);
      leg.bearingFromStart = vector.getAngle();
      leg.distanceFromStart = vector.getRadial();
    }
  }
  
  private Leg findWinningLegOrNull(List<Leg> candidates) {
    Leg bestLeg = null;
    // for every candidate...
    for (Leg leg : candidates) {
      if (!windfield.supports(leg.endpoint,leg.time)) {
        continue; // NO WIND DATA...
      }
      // ... calculate the distance to the final destination...
      PolarVector<Length> toDestination = geoid.calculateOrthodromicDistanceAndBearing(leg.endpoint, destinationPoint);
      // ... and calculate the time to destination based on the currents positions wind and the boats polar.
      PolarVector<Speed> trueWind = windfield.getWind(leg.endpoint, leg.time);
      PolarVector<Speed> boatSpeed = polar.getVelocity(trueWind.getRadial(), twa(toDestination.getAngle(), trueWind.getAngle()));
      Quantity<Time> timeToDestination = toDestination.getRadial().divide(boatSpeed.getRadial()).asType(Time.class);

      // If the time to destination is less than the period...
      if (timeToDestination.to(HOUR).getValue().doubleValue() <= period.to(HOUR).getValue().doubleValue()) {
        // ... we found a finishing leg.

        Date arrivalTime = addHours(leg.time, timeToDestination);
        if (bestLeg==null) {
          // there is no best leg yet... so this is it!
          bestLeg = new Leg();
          bestLeg.parent = leg;
          bestLeg.bearing = toDestination.getAngle();
          bestLeg.distance = toDestination.getRadial();
          bestLeg.endpoint = destinationPoint;
          bestLeg.time = arrivalTime;
        }
        else if (arrivalTime.before(bestLeg.time)) {
          // if the current arrival time is before the best legs arrival time, then we are faster.
          bestLeg.parent = leg;
          bestLeg.bearing = toDestination.getAngle();
          bestLeg.distance = toDestination.getRadial();
          bestLeg.endpoint = destinationPoint;
          bestLeg.time = arrivalTime;
        }
      }
    }
    return bestLeg;
  }
  
  private List<Leg> reduceIsochrones(List<Leg> candidates) {
    Map<Integer,Leg> map = new HashMap<>();
    
    candidates.stream().forEach(leg -> {
      Integer key = leg.bearingFromStart.getValue().intValue();
      Leg previous = map.get(key);
      if (previous==null ||
              leg.distanceFromStart.getValue().doubleValue() > previous.distanceFromStart.getValue().doubleValue()) {
        map.put(key, leg);
      }
    });
    
    List<Leg> reduced = new LinkedList<>(map.values());
    reduced.sort((leg1, leg2) -> (Double.compare(leg1.bearingFromStart.getValue().doubleValue(), leg2.bearingFromStart.getValue().doubleValue())));
    return reduced;
  }
  
  /**
   * 
   * @param course        the bearing towards the new point
   * @param windDirection the wind direction (FROM)
   * @return 
   */
  private Quantity<Angle> twa(Quantity<Angle> course, Quantity<Angle> windDirection) {
    return course.subtract(windDirection);
  }
  
  private Date addHours(Date time, Quantity<Time> offset) {
    int hours = (int) offset.to(HOUR).getValue().doubleValue();
    offset = offset.subtract(Quantities.getQuantity(hours, HOUR));
    int minutes = (int) offset.to(MINUTE).getValue().doubleValue();
    offset = offset.subtract(Quantities.getQuantity(minutes, MINUTE));
    int seconds = (int) offset.to(SECOND).getValue().doubleValue();
    
    Calendar cal = Calendar.getInstance();
    cal.setTime(time);
    cal.add(Calendar.HOUR_OF_DAY, hours);
    cal.add(Calendar.MINUTE, minutes);
    cal.add(Calendar.SECOND, seconds);
    return cal.getTime();
  }
  
  // === ACCESSORS ===

  /**
   * @param startingPoint the startingPoint to set
   */
  public void setStartingPoint(LatLon startingPoint) {
    this.startingPoint = startingPoint;
  }

  /**
   * @return the destinationPoint
   */
  public LatLon getDestinationPoint() {
    return destinationPoint;
  }

  /**
   * @param destinationPoint the destinationPoint to set
   */
  public void setDestinationPoint(LatLon destinationPoint) {
    this.destinationPoint = destinationPoint;
  }

  /**
   * @return the startingDate
   */
  public Date getStartingDate() {
    return startingDate;
  }

  /**
   * @param startingDate the startingDate to set
   */
  public void setStartingDate(Date startingDate) {
    this.startingDate = startingDate;
  }

  /**
   * @param polar the polar to set
   */
  public void setPolar(Polar polar) {
    this.polar = polar;
  }

  /**
   * @param windfield the windfield to set
   */
  public void setWindfield(WindField windfield) {
    this.windfield = windfield;
  }

  public void setLegFilter(LegFilter legFilter) {
    this.legFilter = legFilter;
  }
  
}
