import lib.igc.Flight;
import lib.igc.FlightCollection;
import lib.igc.Point;
import lib.igc.StandardizePair;
import lib.obj.PointCollection;
import lib.thermals.ThermalColection;
import view.MainFrame;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Predicate;

public class Main {
    public static void main(String[] argc) {
        try {
            FlightCollection tmp = new FlightCollection("data/NetCoupe2019_8643.igc", "data/NetCoupe2019_8606.igc");
            //FlightCollection tmp = new FlightCollection("data/2019-06-01-XCS-AAA-01.igc", "data/2019-06-01-XCS-AAA-02.igc");
            //Flight tmp = new Flight("data/NetCoupe2019_8643.igc");
            //Flight tmp = new Flight("data/2019-06-01-XCS-AAA-02.igc");
            ArrayList<Point> th = tmp.findThermals();
            ThermalColection thc = new ThermalColection();
            thc.addThermals(th.toArray(new Point[0]));
            thc.sort();

            StandardizePair sdp = tmp.standardize();
            thc.standardize(sdp);

            PointCollection pc = new PointCollection();
            pc.addFlightCollection(tmp);
            pc.writeToFile("data.obj");

            pc = new PointCollection();
            pc.addThermalCollection(thc);
            pc.writeToFile("thermals.obj");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
