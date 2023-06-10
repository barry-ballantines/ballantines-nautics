package ballantines.nautics.routing.app.parsers;

import ballantines.nautics.units.LatLon;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GPXParser {

  private File gpx;

  public GPXParser(File gpx) {
    this.gpx = gpx;
  }

  public List<LatLon> parse() {
    try {
      SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
      LatLonHandler handler = new LatLonHandler();
      parser.parse(gpx, handler);
      return handler.getWaypoints();
    } catch (Throwable ex) {
      throw new RuntimeException("Failed to parse GPX file: " + gpx, ex);
    }
  }

  private static class LatLonHandler extends DefaultHandler {

    private static final String ELEMENT_TRK = "trk";
    private static final String ELEMENT_TRKPT = "trkpt";

    private static final String ELEMENT_RTE = "rte";
    private static final String ELEMENT_RTEPT = "rtept";

    private static final String ATTRIBUTE_LAT = "lat";
    private static final String ATTRIBUTE_LON = "lon";


    private List<LatLon> waypoints = new ArrayList<>();

    private boolean skip = false;

    public List<LatLon> getWaypoints() {
      return waypoints;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if (skip) return;
      if (ELEMENT_TRKPT.equals(qName) || ELEMENT_TRKPT.equals(localName)
          || ELEMENT_RTEPT.equals(qName) || ELEMENT_RTEPT.equals(localName)) {
        storeWaypoint(attributes);
      }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
      if (skip) return;
      if (ELEMENT_TRK.equals(qName) || ELEMENT_RTE.equals(qName)
          || ELEMENT_TRK.equals(localName) || ELEMENT_RTE.equals(localName)) {
        skip = true;
      }
    }

    protected void storeWaypoint(Attributes attributes) {
      double lat = Double.parseDouble(attributes.getValue(ATTRIBUTE_LAT));
      double lon = Double.parseDouble(attributes.getValue(ATTRIBUTE_LON));
      this.waypoints.add(new LatLon(lat, lon));
    }
  }
}
