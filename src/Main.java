import lib.igc.Flight;
import lib.igc.FlightCollection;
import lib.igc.Point;
import lib.igc.StandardizePair;
import lib.obj.PointCollection;
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
            /*ArrayList<Flight> thermals = tmp.findClusters();

            thermals.removeIf(new Predicate<Flight>() {
                @Override
                public boolean test(Flight points) {
                    return points.getMin().getAlt() < 700 || points.duration() < 90 || points.climbRate() < 1;
                    //return false;
                }
            });

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
                //0 && 100
                //if(f.altitudeDifference() > minAltDiff && f.size() > 50 && f.getMin().getAlt() > minAlt) {
                pc.addFlight(f);
            }
            pc.writeToFile("thermals.obj");*/

            //new MainFrame(tmp);

            //FlightCollection th = tmp.findThermalsAsFlightCollection();
            ArrayList<Point> th = tmp.findThermals();

            StandardizePair sdp = tmp.standardize();
            for(Point p : th) {
                p.setX((p.getX() - sdp.getMin().getX()));
                p.setY((p.getY() - sdp.getMin().getY()));
                p.setAlt((p.getAlt() - sdp.getMin().getAlt()));
            }
            for(Point p : th) {
                p.setX((p.getX() / sdp.getMax().getX()));
                p.setY((p.getY() / sdp.getMax().getY()));
                p.setAlt((p.getAlt() / sdp.getMax().getAlt()));
            }

            PointCollection pc = new PointCollection();
            pc.addFlightCollection(tmp);
            pc.writeToFile("data.obj");

            pc = new PointCollection();
            //pc.addFlightCollection(th);

            Point old = null;
            for(Point p : th) {
                if(old != null) {
                    pc.addSurface(old, p);
                }
                old = p;
            }

            pc.writeToFile("thermals.obj");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
