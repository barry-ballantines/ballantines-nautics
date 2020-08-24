# Ballantines Nautics Routing App

This module contains a Spring Boot application for weather routing, based
on the Optimized Isochrones Algorithm.

See module "nautics-routing" for more information.

## Starting the Routing Application

Before you start the routing application, make sure that you successfully
built the Ballantines Nautics project workspace.

You need the following files in order to start the routing:

 - The routing app: ```target/routing-app-1.0-SNAPSHOT.jar```
 - The current GRIB2 file. This file can be downloaded from NOAA or other sources
   it is not part of this distribution.
 - The polar diagrams of your boat: e.g. ```polars\Imoca-60.pol```
 - The configuration file: ```application.properties```
 
Start the routing with the following command.

   java -jar target/routing-app-1.0-SNAPSHOT.jar
   
Make sure that the ```application.properties``` file is located in your working directory.

## Application configuration

The routing parameters are configured in the file ```application.properties```.

### Start and destination start time and simulation interval.

The destination of the routing and the start point, e.g. the position of your boat, is configured by the following parameters:

    routing.start.latlon        = N 49 19.869 W 32 30.168
    routing.destination.latlon  = 40.7073 -74.0228 

The time at which the simulation starts is defined by the property

    routing.start.date          =2020-08-20T12:25:00

For information of the time and location formats please check out the following sections.

The simulation period defines how far the calculated waypoints are away from each other. For most simulations a value of 3 hours is a good match. Smaller values create more waypoints, but also increase the computation time.

   # Simulation period (= the time difference between isochrones)
   routing.simulation.period.hours = 3.0

### Meteological data and boat polars

The weather information needs to be downloaded as a GRIB2 file. I recommend to use a tool such as XyGrib to download the actual weather file. XyGrib also contains a nice viewer for the weather data.

    # GRIB2 file
    routing.grib2.file  =./20200820_122541_GFS_P25_.grb2
    
The polar diagrams of your boat need to be provided in a ```.pol``` file. Check out the examples in the ```polars/``` folder.

    # Polars
    routing.polar.file  =./polars/Imoca-60.pol
    
### Output

This program will generate three output files by default:

 - the best route (GPX)
 - a set of isochrones (GPX)
 - the route for import to Sailaway Simulator (CSV)
 
You can disable the output of any of these file, if needed. The output files are already configured to use a common timestamp. So it is only necessary to change the timestamp property if you want to compare the result of several simulations. The timestamp is just a tag and may have any format you like:

    # routing timestamp is used for labeling the export files only...
    routing.timestamp   =202000820_1225_3h
    
    # Export files
    routing.export.isochrones       = true
    routing.export.isochrones.file  = ./${routing.timestamp}_isochrones.gpx
    
    routing.export.route            = true
    routing.export.route.file       = ./${routing.timestamp}_route.gpx
    
    routing.export.sailaway.route       = true
    routing.export.sailaway.route.file  =./${routing.timestamp}_sailaway_route.csv
    
### Boundary Box

The routing application can only simulate routes within an area that is covered by the GRIB2 file. But it is possible to restrict the simulation area to a smaller area, if needed. This may make sense at the end of a race, when you want to simulate the race finish with a smaller simulation period on a smaller area. In this case you need to uncomment the boundary box properties and define upper and lower limits for the latitudes and longitudes:

    # Boundary Box of the simulation - If not defined, the boundary box of the GRIB file is used instead.
    routing.boundaryBox.name  =Boundary Box
    routing.boundaryBox.north =N 55
    routing.boundaryBox.south =N 40
    routing.boundaryBox.east  =W 25
    routing.boundaryBox.west  =W 75
    
### Forbidden Areas

The routing algorithm does not know anything about landmasses and coastlines. This is because checking if a calculated waypoint is on water or on land is a non-trivial and also very time-consuming process. Therefore, it might happen that the calculated route leads you over land.

To avoid these useless routes, Barry's Routing Application supports the definition of "forbidden areas", which are boundary boxes that should not be entered by the algorithm. You can define as many forbidden areas as you like in an array-like style. Make sure that the indices are consecutive numbers, starting at 0:

    routing.forbiddenAreas[0].name  =Newfoundland
    routing.forbiddenAreas[0].north =N 52
    routing.forbiddenAreas[0].south =N 46
    routing.forbiddenAreas[0].east  =W 52
    routing.forbiddenAreas[0].west  =W 70
    
    routing.forbiddenAreas[1].name  =Long Island
    routing.forbiddenAreas[1].north =N 45 00.000
    routing.forbiddenAreas[1].south =N 40 40.000
    routing.forbiddenAreas[1].east  =W 72 20.000
    routing.forbiddenAreas[1].west  =W 73 50.000

For performance reasons you should keep the amount of forbidden areas small. It is recommended to start without forbidden areas first and only add them, if needed.

## Location formats

Locations like the start and end point of your weather routing are defined in latitudes and longitudes. The configuration of the routing app supports a variaty of formats. In order to avoid encoding problems it is recommended not to use special characters like ° or ' or ".

Example: The starting position ```N 37° 29' 7.152" W 25° 21' 34.8984"``` should be specified in the following ways:

   # DMS (degrees, minutes, seconds)
   routing.start.latlon = N 37 29 7.152 W 25 21 34.8984
   
   # DDM (degrees, decimal minutes)
   routing.start.latlon = N 37 29.1192 W 25 21.58164
   
   # DD (decimal degrees)
   routing.start.latlon = N 37.485320  W 25.359694
   routing.start.latlon = 37.485320 -25.359694

For single latitudes and longitudes, like used in the definition of forbidden areas and boundary boxes, the same formates can be used.

   routing.forbiddenAreas[1].name  =Long Island
   routing.forbiddenAreas[1].north =N 45 00.000
   routing.forbiddenAreas[1].south =N 40 40.000
   routing.forbiddenAreas[1].east  =W 72 20.000
   routing.forbiddenAreas[1].west  =W 73 50.000
   
## Time format and time zones

All times used in this program are UTC times.

The time format is ```yyyy-mm-ddThh:mm:ss```. Example:

   routing.start.date          =2020-08-20T12:25:00