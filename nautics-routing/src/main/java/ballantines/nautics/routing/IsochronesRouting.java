/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ballantines.nautics.routing;

import java.util.*;

import ballantines.nautics.routing.filter.LegFilter;
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

  private RoutingContext context = new RoutingContext();

  private Polar polar;
  private WindField windfield;
  private LegFilter legFilter;
  private Quantity<Time> period = Quantities.getQuantity(6.0, HOUR);
  private IsochronesListener isochronesListener;

  
  // === METHODS ===
  
  public Leg start() {

    Leg start = Leg.createStartingLeg(this.context);
    
    // START LOOPING...

    Date time = context.getStartingDate();
    List<Leg> isochrone = Collections.singletonList(start);
    List<Leg> lastIsochrone = null;
    Leg winningLeg = findWinningLegOrNull(isochrone);
    
    while (winningLeg==null && !isochrone.isEmpty()) {
      time = addHours(time, period);
      lastIsochrone = isochrone;
      isochrone = findNextIsochrone(isochrone, time);
      
      if (isochronesListener!=null && !isochrone.isEmpty()) {
        isochronesListener.isochronesCalculated(time, isochrone);
      }
      
      winningLeg = findWinningLegOrNull(isochrone);
    }
    
    if (isochronesListener!=null) {
      if (winningLeg!=null) {
        isochronesListener.winningLegFound(winningLeg);
      }
      else {
        Leg bestLeg = findBestLeg(lastIsochrone);
        isochronesListener.noLegFound(bestLeg);
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
      
      LatLon endpoint = this.context.getGeoid().calculateDestination(reference.endpoint, bearing, distance);

      Leg seg = Leg.createChild(reference, time, distance, bearing, trueWind, velocity.getRadial());

      if (this.legFilter.accept(seg)) {
        newCandidates.add(seg);
      }
    }
    candidates.addAll(newCandidates);
  }


  private Leg findBestLeg(List<Leg> isochrones) {
    Leg bestLeg = null;
    double distance = Double.MAX_VALUE;

    for (Leg leg : isochrones) {
      PolarVector<Length> toDestination = this.context.getGeoid().calculateOrthodromicDistanceAndBearing(leg.endpoint, context.getDestinationPoint());
      if (distance > toDestination.getRadial(NAUTICAL_MILE)) {
        distance = toDestination.getRadial(NAUTICAL_MILE);
        bestLeg = leg;
      }
    }

    PolarVector<Length> toDestination = this.context.getGeoid().calculateOrthodromicDistanceAndBearing(bestLeg.endpoint, context.getDestinationPoint());

    Leg finalLeg = new Leg();
    finalLeg.endpoint = context.getDestinationPoint();
    finalLeg.time = null;
    finalLeg.wind = null;
    finalLeg.boatSpeed = null;
    finalLeg.distance = toDestination.getRadial();
    finalLeg.bearing = toDestination.getAngle();
    finalLeg.parent = bestLeg;

    return finalLeg;
  }

  private Leg findWinningLegOrNull(List<Leg> candidates) {
    Leg bestLeg = null;
    // for every candidate...
    for (Leg leg : candidates) {
      if (!windfield.supports(leg.endpoint,leg.time)) {
        continue; // NO WIND DATA...
      }
      // ... calculate the distance to the final destination...
      PolarVector<Length> toDestination = this.context.getGeoid().calculateOrthodromicDistanceAndBearing(leg.endpoint, context.getDestinationPoint());
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
          bestLeg.endpoint = context.getDestinationPoint();
          bestLeg.time = arrivalTime;
          bestLeg.boatSpeed = boatSpeed.getRadial();
          bestLeg.wind = trueWind;
        }
        else if (arrivalTime.before(bestLeg.time)) {
          // if the current arrival time is before the best legs arrival time, then we are faster.
          bestLeg.parent = leg;
          bestLeg.bearing = toDestination.getAngle();
          bestLeg.distance = toDestination.getRadial();
          bestLeg.endpoint = context.getDestinationPoint();
          bestLeg.time = arrivalTime;
          bestLeg.boatSpeed = boatSpeed.getRadial();
          bestLeg.wind = trueWind;
        }
      }
    }
    return bestLeg;
  }
  
  private List<Leg> reduceIsochrones(List<Leg> candidates) {
    Map<Integer,Leg> map = new TreeMap<>();
    
    candidates.stream().forEach(leg -> {
      Integer key = Math.round(leg.getVectorFromStart().getAngle().to(ARC_DEGREE).getValue().floatValue());
      if (key==-180) {
        key = 180; // close the circle
      }
      Leg previous = map.get(key);
      if (previous==null ||
              leg.getVectorFromStart().getRadial(NAUTICAL_MILE) > previous.getVectorFromStart().getRadial(NAUTICAL_MILE)) {
        map.put(key, leg);
      }
    });
    
    List<Leg> reduced = new LinkedList<>(map.values());
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
    this.context.setStartingPoint(startingPoint);
  }

  /**
   * @return the destinationPoint
   */
  public LatLon getDestinationPoint() {
    return context.getDestinationPoint();
  }

  /**
   * @param destinationPoint the destinationPoint to set
   */
  public void setDestinationPoint(LatLon destinationPoint) {
    context.setDestinationPoint(destinationPoint);
  }

  /**
   * @return the startingDate
   */
  public Date getStartingDate() {
    return context.getStartingDate();
  }

  /**
   * @param startingDate the startingDate to set
   */
  public void setStartingDate(Date startingDate) {
    this.context.setStartingDate(startingDate);
  }

  /**
   * @param polar the polar to set
   */
  public void setPolar(Polar polar) {
    this.polar = polar;
  }

  /**
   * @param period the time interval used for calculating the isochrones
   */
  public void setPeriod(Quantity<Time> period) {
    this.period = period;
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

  public void setIsochronesListener(IsochronesListener isochronesListener) {
    this.isochronesListener = isochronesListener;
  }
}
