package lib.cluster;

import lib.igc.Flight;
import lib.igc.Point;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Cluster implements Iterable<Point> {
    private List<Point> data;

    private Point avgPos;
    private double minDist;
    private double maxDist;

    public Cluster() {
        data = new ArrayList<>();
        avgPos = new Point(0, 0, 0);
        minDist = 0;
        maxDist = 0;
    }

    public Cluster(Flight f) {
        data = new ArrayList<>(f.size());
        avgPos = new Point(0, 0, 0);

        for(Point p : f) {
            data.add(p);
        }

        calcAvgPos();
        calcMinDist();
    }

    private void calcAvgPos() {
        avgPos.lon = 0;
        avgPos.lat = 0;
        avgPos.alt = 0;
        avgPos.time = 0;

        for(Point p : this) {
            avgPos.lon += p.lon;
            avgPos.lat += p.lat;
            avgPos.alt += p.alt;
            avgPos.time += p.time;
        }

        avgPos.lon /= size();
        avgPos.lat /= size();
        avgPos.alt /= size();
        avgPos.time /= size();
    }

    private void calcMinDist() {
        minDist = data.parallelStream().map(p -> {
            return p.distance(avgPos);
        }).reduce(0.0, Math::min);
    }

    private void calcMaxDist() {
        maxDist = data.parallelStream().map(p -> {
            return p.distance(avgPos);
        }).reduce(0.0, Math::max);
    }

    public boolean add(Point p) {
        minDist = Math.min(minDist, p.distance(avgPos));
        maxDist = Math.max(maxDist, p.distance(avgPos));
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

    public boolean minDistanceLowerThan(Cluster c, double dist) {
        final double avgD = avgPos.distance(c.avgPos);

        if(avgD - c.minDist - minDist <= maxDist) return true;
        if(avgD - c.maxDist - maxDist > maxDist) return false;

        return data.parallelStream().map(p -> {
            return c.data.parallelStream()
                    .map(p2 -> {
                        return p.distance(p2) <= dist;
                    })
                    .reduce(false, (a, b) -> {
                        return a || b;
                    });
        }).reduce(false, (a, b) -> {
            return a || b;
        });
    }

    public double distance(Cluster c) {
        //return minDistance(c);
        return avgPos.distance(c.avgPos);
    }

    public double avgBaseDistance(Cluster c) {
        final double avgD = avgPos.distance(c.avgPos);

        double d1 = data.parallelStream().map(p -> {
            double b = Math.abs(avgPos.bearing(p) - avgPos.bearing(c.avgPos));
            return Math.cos(Math.toRadians(b)) * avgPos.distance(p);
        }).reduce(0.0, Math::max);

        double d2 = c.data.parallelStream().map(p -> {
            double b = Math.abs(c.avgPos.bearing(p) - c.avgPos.bearing(avgPos));
            return Math.cos(Math.toRadians(b)) * c.avgPos.distance(p);
        }).reduce(0.0, Math::max);

        return avgD - d1 - d2;
    }

    public boolean avgBaseDistanceLowerThan(Cluster c, double maxDist) {
        final double avgD = avgPos.distance(c.avgPos);

        if(avgD - c.minDist - minDist <= maxDist) return true;
        if(avgD - c.maxDist - maxDist > maxDist) return false;

        double d1 = data.parallelStream().map(p -> {
            double b = Math.abs(avgPos.bearing(p) - avgPos.bearing(c.avgPos));
            return Math.cos(Math.toRadians(b)) * avgPos.distance(p);
        }).reduce(0.0, Math::max);

        if(avgD - d1 - c.minDist <= maxDist) return true;

        double d2 = c.data.parallelStream().map(p -> {
            double b = Math.abs(c.avgPos.bearing(p) - c.avgPos.bearing(avgPos));
            return Math.cos(Math.toRadians(b)) * c.avgPos.distance(p);
        }).reduce(0.0, Math::max);

        return avgD - d1 - d2 < maxDist;
    }

    public void merge(Cluster c) {
        avgPos.lon = (avgPos.lon * size() + c.avgPos.lon * c.size()) / (size() + c.size());
        avgPos.lat = (avgPos.lat * size() + c.avgPos.lat * c.size()) / (size() + c.size());
        avgPos.alt = (avgPos.alt * size() + c.avgPos.alt * c.size()) / (size() + c.size());
        avgPos.time = (avgPos.time * size() + c.avgPos.time * c.size()) / (size() + c.size());

        data.addAll(c.data);
        calcMinDist();
    }

    public Point getAvgPos() {
        return avgPos;
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

    public List<Point> convexHull() {
        return convexHull(data);
    }

    public static List<Point> convexHull(List<Point> points) {
        List<Point> res = new ArrayList<>();

        List<Point> copy = new ArrayList<>(points.size());
        copy.addAll(points);

        Point p0 = points.get(0);

        for(Point p : copy) {
            if((p0.lat > p.lat) || (p0.lat == p.lat && p0.lon > p.lon)) {
                p0 = p;
            }
        }

        copy.remove(p0);

        Point finalP = p0;
        copy.sort((p1, p2) -> {
            double b1 = -1 * (finalP.bearing(p1) - 90);
            double b2 = -1 * (finalP.bearing(p2) - 90);

            if(b1 < b2) {
                return -1;
            }
            if(b1 == b2) {
                double d1 = finalP.distance(p1);
                double d2 = finalP.distance(p2);
                if(d1 < d2) {
                    return -1;
                }
                if(d1 == d2) {
                    return 0;
                }
                return 1;
            }
            return 1;
        });

        for(int i = 0; i < copy.size() - 1; i++) {
            if(p0.bearing(copy.get(i)) == p0.bearing(copy.get(i + 1))) {
                if(p0.distance(copy.get(i)) < p0.distance(copy.get(i + 1))) {
                    copy.remove(i);
                    i--;
                }
                else {
                    copy.remove(i + 1);
                }
            }
        }

        res.add(p0);

        for(Point p : copy) {
            while (res.size() > 1 && ccw(res.get(res.size() - 2), res.get(res.size() - 1), p) <= 0) {
                res.remove(res.size() - 1);
            }
            res.add(p);
        }
        //res.add(0, p0);
        return res;
    }

    private static int ccw(Point p1, Point p2, Point p3) {
        double b1 = -1 * (p2.bearing(p1) - 90);
        double b3 = -1 * (p2.bearing(p3) - 90);

        if(Math.abs(b1 - b3) == Math.PI || b1 == b3) {
            return 0;
        }

        if((Math.abs(b1) == Math.PI && b3 > 0) || (b1 == 0 && b3 < 0) || (b1 < 0 && b3 - b1 > 0) || (b1 > 0 && b3 - b1 < 0)) {
            return 1;
        }
        return 0;

        /*double diff = b3 - b2;

        if(diff == 0) {
            return 0;
        }
        if(diff < 0) {
            return -1;
        }
        return 1;*/

        /*if(p1.bearing(p2) == p2.bearing(p3)) {
            return 0;
        }

        //counterclockwise
        if(p1.bearing(p2) > p2.bearing(p3)) {
            return 1;
        }

        return -1;*/
    }

    public static void main(String[] args) {
        ArrayList<Integer> m = new ArrayList<>();

        for(int i = 0; i < 10; i++) {
            m.add((int) (Math.random() * 20));
        }

        System.out.println(m);

        m.sort((v1, v2) -> {
            if(v1 < v2) {
                return -1;
            }
            else if(v1.equals(v2)) {
                return 0;
            }
            return 1;
        });

        System.out.println(m);
    }
}
