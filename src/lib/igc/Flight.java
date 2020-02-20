package lib.igc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class Flight implements Iterable<Point> {
    private ArrayList<Point> flight;

    private float maxDist = 300;
    private float minHeight = 700;
    private int minDuration = 90;
    private float minClimbRate = 1f;

    public Flight(String file) throws IOException {
        flight = new ArrayList<>();

        BufferedReader bf = new BufferedReader(new FileReader(new File(file)));

        String line;
        while ((line = bf.readLine()) != null) {
            if(line.length() != 0 && line.charAt(0) == 'B') {

                String h = line.substring(1, 3);
                String m = line.substring(3, 5);
                String s = line.substring(5, 7);

                int time = Integer.parseInt(h) * 3600 + Integer.parseInt(m) * 60 + Integer.parseInt(s);

                String y = line.substring(7, 15);
                String x = line.substring(15, 24);
                String alt = line.substring(25, 30);
                //String alt = line.substring(30, 35);

                flight.add(new Point(time, extractPos(x), extractPos(y), extractAlt(alt)));
            }
        }
    }

    public Flight() {
        this.flight = new ArrayList<>();
    }

    public Flight(Flight f) {
        flight = new ArrayList<>(f.flight.size());

        for(Point p : f.flight) {
            flight.add(new Point(p));
        }
    }

    public void addPoint(Point p) {
        flight.add(p);
    }

    private void insertPoint(Point p) {
        int i;
        for(i = (flight.size() - 1); i >= 0 && p.getTime() < flight.get(i).getTime(); i--) {
        }
        if(i + 1 < flight.size()) {
            flight.add(i + 1, p);
        }
        else {
            flight.add(p);
        }
    }

    public void merge(Flight f) {
        for(Point p : f) {
            insertPoint(p);
        }
    }

    private void appendFlight(Flight f) {
        for(Point p : f) {
            addPoint(p);
        }
    }

    public Point averagePos() {
        float x = 0;
        float y = 0;
        float alt = 0;

        for(Point p : flight) {
            x += p.getX() / flight.size();
            y += p.getY() / flight.size();
            alt += p.getAlt() / flight.size();
        }

        return new Point(x, y, alt);
    }

    private static float extractPos(String l) {
        float res;
        float coeff = 1;
        if(l.contains("N") || l.contains("S")) {
            if(l.contains("S")) {
                coeff = -1;
            }

            res = Float.parseFloat(l.substring(0,2));
            res += Float.parseFloat(l.substring(2, l.length() - 1)) / 60000;
        }
        else {
            if(l.contains("W")) {
                coeff = -1;
            }

            res = Float.parseFloat(l.substring(0,3));
            res += Float.parseFloat(l.substring(3, l.length() - 1)) / 60000;
        }

        return res * coeff;
    }

    private static float extractAlt(String l) {
        return Float.parseFloat(l);
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();

        for(Point p : flight) {
            res.append(p.toString());
            res.append("\n");
        }

        return res.toString();
    }

    public Point getMin() {
        Point p = new Point(flight.get(0));

        for(int i = 0; i < flight.size(); i++) {
            Point tmp = flight.get(i);
            Point.min(p, tmp);
        }

        return p;
    }

    public Point getMax() {
        Point p = new Point(flight.get(0));

        for(int i = 0; i < flight.size(); i++) {
            Point tmp = flight.get(i);
            Point.max(p, tmp);
        }

        return p;
    }

    public ArrayList<Flight> findThermals() {
        ArrayList<Flight> res = new ArrayList<>();

        for (Point p : this) {
            Flight tmp = new Flight();
            tmp.addPoint(new Point(p));
            res.add(tmp);
        }

        boolean modified = true;

        while(modified) {
            modified = false;

            for(int i = 0; i < res.size() - 1; i++) {
                Flight f1 = res.get(i);
                Flight f2 = res.get(i + 1);

                if(f2.averagePos().distance(f1.averagePos()) < maxDist) {
                    //f1.addFlight(f2);
                    f1.appendFlight(f2);
                    res.remove(i + 1);
                    modified = true;
                }
            }
        }

        res.removeIf(points -> points.getMin().getAlt() < minHeight || points.duration() < minDuration || points.climbRate() < minClimbRate);

        return res;
    }

    public int size() {
        return flight.size();
    }

    public float climbRate() {
        float res;

        Point min = flight.get(0);
        Point max = flight.get(flight.size() - 1);

        res = max.getAlt() - min.getAlt();
        res /= max.getTime() - min.getTime();

        return res;
    }

    public int duration() {
        int res;

        Point min = flight.get(0);
        Point max = flight.get(flight.size() - 1);

        res = max.getTime() - min.getTime();

        return res;
    }

    @Override
    public Iterator<Point> iterator() {
        return flight.iterator();
    }
}
