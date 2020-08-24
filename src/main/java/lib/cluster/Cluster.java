package lib.cluster;

import lib.igc.Flight;
import lib.igc.Point;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Cluster implements Iterable<Point> {
    private List<Point> data;

    private Point avgPos;

    public Cluster() {
        data = new ArrayList<>();
        avgPos = new Point(0, 0, 0);
    }

    public Cluster(Flight f) {
        data = new ArrayList<>(f.size());
        avgPos = new Point(0, 0, 0);

        for(Point p : f) {
            data.add(p);
        }

        calcAvgPos();
    }

    private void calcAvgPos() {
        avgPos.x = 0;
        avgPos.y = 0;
        avgPos.alt = 0;
        avgPos.time = 0;

        for(Point p : this) {
            avgPos.x += p.x;
            avgPos.y += p.y;
            avgPos.alt += p.alt;
            avgPos.time += p.time;
        }

        avgPos.x /= size();
        avgPos.y /= size();
        avgPos.alt /= size();
        avgPos.time /= size();
    }

    public boolean add(Point p) {
        return data.add(p);
    }

    public double minDistance(Cluster c) {
        double dist = Double.MAX_VALUE;

        //for (Point p1 : data) {
        //    for(Point p2 : c) {
        //        dist = Math.min(dist, p1.distance(p2));
        //    }
        //}

        dist = data.parallelStream().map(p -> {
            return c.data.parallelStream().map(p::distance).reduce(Math::min).get();
        }).reduce(Math::min).get();

        return dist;
    }

    public double distance(Cluster c) {
        //return minDistance(c);
        return avgPos.distance(c.avgPos);
    }

    public void merge(Cluster c) {
        avgPos.x = (avgPos.x * size() + c.avgPos.x * c.size()) / (size() + c.size());
        avgPos.y = (avgPos.y * size() + c.avgPos.y * c.size()) / (size() + c.size());
        avgPos.alt = (avgPos.alt * size() + c.avgPos.alt * c.size()) / (size() + c.size());
        avgPos.time = (avgPos.time * size() + c.avgPos.time * c.size()) / (size() + c.size());

        for(Point p : c) {
            data.add(p);
        }
    }

    public int size() {
        return data.size();
    }

    public Point get(int i) {
        return data.get(i);
    }

    @Override
    public Iterator<Point> iterator() {
        return data.iterator();
    }
}
