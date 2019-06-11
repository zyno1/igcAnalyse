import lib.igc.Flight;
import lib.igc.Point;
import lib.obj.PointCollection;
import view.MainFrame;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] argc) {
        try {
            //Flight tmp = new Flight("data/NetCoupe2019_8606.igc");
            //Flight tmp = new Flight("data/NetCoupe2019_8643.igc");
            Flight tmp = new Flight("data/2019-06-01-XCS-AAA-02.igc");
            ArrayList<Flight> thermals = tmp.findThermals();

            Point m = tmp.getMin();

            tmp.positives(m);
            for(Flight f : thermals) {
                f.positives(m);
            }

            m = tmp.getMax();

            tmp.standardize(m);
            for(Flight f : thermals) {
                f.standardize(m);
            }

            PointCollection pc = new PointCollection();
            pc.addFlight(tmp);
            pc.writeToFile("data.obj");

            pc = new PointCollection();
            for(Flight f : thermals) {
                pc.addFlight(f);
            }
            pc.writeToFile("thermals.obj");

            new MainFrame(tmp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
