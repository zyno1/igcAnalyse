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

    @Override
    public Iterator<Flight> iterator() {
        return flights.iterator();
    }
}
