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

    public void standardize() {
        Point m = getMin();

        for(Flight f : flights) {
            f.positives(m);
        }

        m = getMax();

        for(Flight f : flights) {
            f.standardize(m);
        }
    }

    @Override
    public Iterator<Flight> iterator() {
        return flights.iterator();
    }
}
