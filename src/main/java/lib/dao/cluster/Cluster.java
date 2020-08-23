package lib.dao.cluster;

import lib.igc.Point;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Cluster implements Iterable<Point> {
    private List<Point> data;

    public Cluster() {
        data = new ArrayList<>();
    }

    public void add(Point p) {
        data.add(p);
    }

    public float minDistance(Cluster c) {
        float dist = Float.MAX_VALUE;

        for (Point p1 : data) {
            for(Point p2 : c) {
                dist = Math.min(dist, p1.distance(p2));
            }
        }

        return dist;
    }

    public float distance(Cluster c) {
        return minDistance(c);
    }

    public void merge(Cluster c) {
        for(Point p : c) {
            data.add(p);
        }
    }

    @Override
    public Iterator<Point> iterator() {
        return data.iterator();
    }
}
