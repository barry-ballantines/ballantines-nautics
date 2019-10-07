package ballantines.nautics.routing.polar;

import ballantines.nautics.routing.polar.internal.SingleWindSpeedPolar;
import ballantines.nautics.units.PolarVector;

import java.io.*;

import tec.units.ri.quantity.Quantities;

/**
 * A parser for reading polar files (.pol).
 * 
 * The files, used by this PolarParser need to have the following file format
 * 
 * <pre>
 * pol	TwaUp	Bsp0	TwaUp	BspUp	Twa1  Bsp1  ...
 * 4    30    2.47	46.2	4.07	50    4.34
 * 6    30    3.72	44.3	5.60	50    6.09
 * 8    30    4.67	42.1	6.49	50    7.17
 * 10   30    5.31	40.6	6.87	45    7.28
 * 12   30    5.66	39.4	7.04	45    7.51
 * 14   30    5.84	38.8	7.13	45    7.64
 * ...
 * </pre>
 The format is line-based, data are separated by tabs.
 
 The first line is used to check the file format. The first column of the first line is ignored, but it is expected, 
 that the following data headers start with "Twa", followed by "Bsp" and so on.
 
 Any following line, not starting with a number, is ignored.
 * 
 * @author mbuse
 */
public class PolarParser {

  public Polar parsePolar(File file) throws IOException {
    FileReader reader = new FileReader(file);
    return parsePolar(reader);
  }
  
  public Polar parsePolar(Reader reader) throws IOException {
    BufferedReader bufferedReader = new BufferedReader(reader);
    String headerLine = bufferedReader.readLine();
    checkFileFormat(headerLine);
    
    DefaultPolar polar = new DefaultPolar();
    String line = bufferedReader.readLine();
    do {
      parseLine(polar, line);
      line = bufferedReader.readLine();
    } while(line!=null);
    
    return polar;
  }
  
  private void parseLine(DefaultPolar polar, String line) throws PolarParserException {
    if (line==null) {
      throw new PolarParserException("File contains no data.");
    }
    
    String[] data = line.split("\t");
    String twsString = data[0].trim();
    if (data.length< 3 || !isNumeric(twsString)) {
      // skip line...
      return;
    }
    try {
      double tws = Double.parseDouble(twsString);
      SingleWindSpeedPolar record = createTWARecord(polar, tws);
      double twa =0;
      double bsp =0;
      for (int col=2; col<data.length; col+=2) {
        twa = Double.parseDouble(data[col-1].trim());
        bsp = Double.parseDouble(data[col].trim());
        addTWAEntry(record, twa, bsp);
      }
      if (twa!=180) {
        throw new PolarParserException("Data for TWS=" + tws + " is incomplete. Last entry is for TWA=" + twa+".");
      }
    } catch (NumberFormatException nfex) {
      throw new PolarParserException("Cannot parse line '" + line + "'.", nfex);
    }
  }
  
  private SingleWindSpeedPolar createTWARecord(DefaultPolar polar, double tws) throws PolarParserException {
    try {
      return polar.newTWS(Quantities.getQuantity(tws, DefaultPolar.WIND_SPEED_UNIT));
    } catch (Exception ex) {
      throw new PolarParserException("Error for entry TWS="+tws+".", ex);
    }
  }
  
  private void addTWAEntry(SingleWindSpeedPolar record, double twa, double bsp) throws PolarParserException {
    try {
      record.add(PolarVector.create(bsp, DefaultPolar.BOAT_SPEED_UNIT, twa, DefaultPolar.ANGLE_UNIT));
    } catch(Exception ex) {
      throw new PolarParserException("Error for Entry: TWS="+record.getTrueWindSpeed()+", TWA="+twa+", BSP="+bsp+".", ex);
    }
  }
  
  private void checkFileFormat(String headerLine) throws PolarParserException {
    String[] columns = headerLine.split("\t");
    if (columns.length < 3) {
      throw new PolarParserException("Not enough data columns.");
    }
    for (int col=2; col<columns.length; col+=2) {
      String twa = columns[col-1];
      String bsp = columns[col];
      if (!twa.startsWith("Twa")) {
        throw new PolarParserException("Errpr in column " + (col-1) + ": \"Twa\" expected, but found \"" + twa + "\".");
      }
      if (!bsp.startsWith("Bs")) {
        throw new PolarParserException("Errpr in column " + (col) + ": \"Bsp\" expected, but found \"" + bsp + "\".");
      }
    }
    
  }
  
  public static class PolarParserException extends IOException {
    
    public PolarParserException(String message) {
      super(message);
    }
    
    public PolarParserException(String message, Throwable ex) {
      super(message, ex);
    }
  }
  
  public static boolean isNumeric(String str){
    return str.matches("\\d*(\\.\\d+)?");
  }
}
