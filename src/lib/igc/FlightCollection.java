package lib.igc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class FlightCollection implements Iterable<Flight> {
    private ArrayList<Flight> flights;

    public FlightCollection(String... paths) throws IOException {
        flights = new ArrayList<>(paths.length);
        for(String p : paths) {
            flights.add(new Flight(p));
        }
    }

    public FlightCollection(FlightCollection f) {
        flights = new ArrayList<>(f.flights.size());

        for(Flight fl : f) {
            flights.add(new Flight(fl));
        }
    }

    public FlightCollection() {
        flights = new ArrayList<>();
    }

    public void addFlight(Flight f) {
        flights.add(f);
    }

    public Point getMin() {
        Point p = new Point(flights.get(0).getMin());

        for(int i = 0; i < flights.size(); i++) {
            Point tmp = flights.get(i).getMin();
            Point.min(p, tmp);
        }

        return p;
    }

    public Point getMax() {
        Point p = new Point(flights.get(0).getMax());

        for(int i = 0; i < flights.size(); i++) {
            Point tmp = flights.get(i).getMax();
            Point.max(p, tmp);
        }

        return p;
    }

    public StandardizePair standardize() {
        StandardizePair sdp = new StandardizePair();
        Point m = getMin();

        sdp.setMin(m);

        for(Flight f : flights) {
            f.positives(m);
        }

        m = getMax();
        sdp.setMax(m);

        for(Flight f : flights) {
            f.standardize(m);
        }

        return sdp;
    }

    public void standardize(StandardizePair sdp) {
        for(Flight f : flights) {
            f.positives(sdp.getMin());
        }

        for(Flight f : flights) {
            f.standardize(sdp.getMax());
        }
    }

    public FlightCollection findThermalsAsFlightCollection() {
        FlightCollection res = new FlightCollection();

        for(Flight f : flights) {
            for(Flight th : f.findThermals()) {
                res.addFlight(th);
            }
        }

        return res;
    }

    public ArrayList<Point> findThermals() {
        ArrayList<Point> res = new ArrayList<>();

        for(Flight f : findThermalsAsFlightCollection()) {
            res.add(f.averagePos());
        }

        boolean modified = true;
        while (modified) {
            modified = false;

            for(int i = 0; i < res.size(); i++) {
                Point p1 = res.get(i);

                for(int j = i + 1; j < res.size(); j++) {
                    Point p2 = res.get(j);

                    if(p1.distance(p2) < 150) {
                        p1.setX((p1.getX() + p2.getX()) / 2);
                        p1.setY((p1.getY() + p2.getY()) / 2);
                        p1.setAlt((p1.getAlt() + p2.getAlt()) / 2);

                        res.remove(j);
                        j--;
                        modified = true;
                    }
                }
            }
        }

        return res;
    }

    @Override
    public Iterator<Flight> iterator() {
        return flights.iterator();
    }
}
