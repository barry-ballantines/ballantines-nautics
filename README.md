# Ballantines Nautics - Tools for Programming Sailors

This repository contains APIs and Tools that are around sailing.

 - nautics-api : An API that defines nautical units like knots, nautical miles, different formats for latitudes and longitudes
 - nautics-grib : Helper classes for parsing GRIB2 files, used for weather routing
 - nautics-routing : The implementation of the Optimized Isochrones Algorithm for weather routing
 - nautics-routing-app : A weather routing application, based on Spring Boot
 
 ## Building everything
 
 Download or clone the Ballantines Bautics project to your computer and run the maven build process:
 
    mvn clean install
    
 Make sure that you have Java SDK 8 and Maven 3.3 (or later) installed and configured correctly.
 
 ## Starting Weather Routing
 
 Go to the sub module [nautics-routing-app][nautics-routing-app]) to find more information about the routing application, the prerequisites and the configuration.

[nautics-routing-app]: nautics-routing-app
