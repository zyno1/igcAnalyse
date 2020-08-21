/*
    igcAnalyse
    Copyright (C) 2020  Olivier Zeyen

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package lib.igc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class FlightCollection implements Iterable<Flight> {
    private final ArrayList<Flight> flights;

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

    public FlightCollection(int i) {
        flights = new ArrayList<>(i);
    }

    public FlightCollection(ArrayList<Flight> fc) {
        flights = fc;
    }

    public FlightCollection() {
        flights = new ArrayList<>();
    }

    public void addFlight(Flight f) {
        flights.add(f);
    }

    public Point getMin() {
        Point p = new Point(flights.get(0).getMin());

        for (Flight flight : flights) {
            Point tmp = flight.getMin();
            Point.min(p, tmp);
        }

        return p;
    }

    public Point getMax() {
        Point p = new Point(flights.get(0).getMax());

        for (Flight flight : flights) {
            Point tmp = flight.getMax();
            Point.max(p, tmp);
        }

        return p;
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

    public int size() {
        return flights.size();
    }

    public int index(Flight f) {
        return flights.indexOf(f);
    }

    @Override
    public Iterator<Flight> iterator() {
        return flights.iterator();
    }
}
