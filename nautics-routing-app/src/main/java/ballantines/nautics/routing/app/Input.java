package ballantines.nautics.routing.app;

import ballantines.nautics.units.LatLon;
import ballantines.nautics.units.LatLonFormat;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;
import java.io.File;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Scanner;
import java.util.function.Supplier;

public class Input {

  private Scanner scanner = new Scanner(System.in);
  private PrintStream out = System.out;
  private Locale locale = Locale.US;


  public Supplier<File> readFile(String prompt, boolean mustExist) {

    return () -> {
      Supplier<String> supplier = readLine(prompt);
      File file = null;
      while (file==null) {
        String in = supplier.get();
        file = new File(in);
        if (mustExist && !file.exists()) {
          System.out.println("The file " + file + " does not exist! Please specify an existing file!");
          file = null;
        }
      }
      return file;
    };
  }

  public Supplier<LocalDateTime> readDateTime(String prompt) {

    return () -> {
      Supplier<String> supplier = readLine(prompt);
      String in = supplier.get();
      return LocalDateTime.parse(in);
    };
  }

  public Supplier<LatLon> readLatLon(String prompt) {

    return () -> {
      Supplier<String> supplier = readLine(prompt);
      String in = supplier.get();
      return LatLonFormat.parse(in);
    };
  }

  public <Q extends Quantity<Q>> Supplier<Quantity<Q>> readQuantity(String prompt, Unit<Q> unit) {
    return () -> {
      double in = readDouble("Simulation period (h): ").get();
      return Quantities.getQuantity(in, unit);
    };
  }

  public Supplier<Double> readDouble(String prompt) {
    out.print(prompt);
    return () -> scanner.nextDouble();
  }

  public Supplier<String> readLine(String prompt) {
    out.print(prompt);
    return () -> scanner.nextLine();
  }

  public PrintStream out() {
    return this.out;
  }

  public void println() {
    out.println();
  }

  public void println(String line) {
    out.println(line);
  }

  public void printf(String format, Object... params) {
    out.printf(locale, format, params);
  }
}
