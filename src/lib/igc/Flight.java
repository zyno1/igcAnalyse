package lib.igc;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Flight implements Iterable<Point> {
    private ArrayList<Point> flight;

    public Flight(String file) throws IOException {
        flight = new ArrayList<>();

        BufferedReader bf = new BufferedReader(new FileReader(new File(file)));

        String line;
        while ((line = bf.readLine()) != null) {
            if(line.charAt(0) == 'B') {
                String y = line.substring(7, 15);
                String x = line.substring(15, 24);
                String alt = line.substring(25, 30);

                flight.add(new Point(extractPos(x), extractPos(y), extractAlt(alt)));
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

    public Point standardDistribution() {
        double x = 0;
        double y = 0;
        double alt = 0;

        Point average = averagePos();

        for(Point p : flight) {
            x += Math.pow(p.getX() - average.getX(), 2) / flight.size();
            y += Math.pow(p.getY() - average.getY(), 2) / flight.size();
            alt += Math.pow(p.getAlt() - average.getAlt(), 2) / flight.size();
        }

        return new Point((float)Math.sqrt(x), (float)Math.sqrt(y), (float)Math.sqrt(alt));
    }

    private float extractPos(String l) {
        float res;
        float coeff = 1;
        if(l.contains("N") || l.contains("S")) {
            if(l.contains("S")) {
                coeff = -1;
            }

            res = Float.valueOf(l.substring(0,2));
            res += Float.valueOf(l.substring(2, l.length() - 1)) / 60000;
        }
        else {
            if(l.contains("W")) {
                coeff = -1;
            }

            res = Float.valueOf(l.substring(0,3));
            res += Float.valueOf(l.substring(3, l.length() - 1)) / 60000;
        }

        return res * coeff;
    }

    private float extractAlt(String l) {
        return Float.valueOf(l);
    }

    public Flight standardize() {
        Flight f = new Flight();
        f.flight.ensureCapacity(flight.size());

        Point average = averagePos();
        Point sd = standardDistribution();

        for(Point p : flight) {
            float x = (p.getX() - average.getX()) / sd.getX();
            float y = (p.getY() - average.getY()) / sd.getY();
            float alt = (p.getAlt() - average.getAlt()) / sd.getAlt();
            f.flight.add(new Point(x, y, alt));
        }

        return f;
    }

    public String toCSV() {
        StringBuilder res = new StringBuilder();

        res.append("x,y,alt\n");
        for(Point p : flight) {
            res.append(p.getX());
            res.append(",");
            res.append(p.getY());
            res.append(",");
            res.append(p.getAlt());
            res.append("\n");
        }

        return res.toString();
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
            if(p.getX() > tmp.getX()) {
                p.setX(tmp.getX());
            }
            if(p.getY() > tmp.getY()) {
                p.setY(tmp.getY());
            }
            if(p.getAlt() > tmp.getAlt()) {
                p.setAlt(tmp.getAlt());
            }
        }

        return p;
    }

    public Point getMax() {
        Point p = new Point(flight.get(0));

        for(int i = 0; i < flight.size(); i++) {
            Point tmp = flight.get(i);
            if(p.getX() < tmp.getX()) {
                p.setX(tmp.getX());
            }
            if(p.getY() < tmp.getY()) {
                p.setY(tmp.getY());
            }
            if(p.getAlt() < tmp.getAlt()) {
                p.setAlt(tmp.getAlt());
            }
        }

        return p;
    }

    public Flight getDrawable() {
        Flight res = new Flight(this);

        Point min = res.getMin();

        res.positives(min);

        Point max = res.getMax();

        res.standardize(max);

        return res;
    }

    public void positives(Point min) {
        for(Point p : this) {
            p.setX((p.getX() - min.getX()));
            p.setY((p.getY() - min.getY()));
            p.setAlt((p.getAlt() - min.getAlt()));
        }
    }

    public void standardize(Point max) {
        for(Point p : this) {
            p.setX((p.getX() / max.getX()));
            p.setY((p.getY() / max.getY()));
            p.setAlt((p.getAlt() / max.getAlt()));
        }
    }

    @Override
    public Iterator<Point> iterator() {
        return flight.iterator();
    }
}
