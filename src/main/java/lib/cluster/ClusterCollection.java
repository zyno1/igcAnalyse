package lib.cluster;

import lib.igc.Flight;
import lib.igc.FlightCollection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ClusterCollection implements Iterable<Cluster> {
    private static final double MAX_MERGE_DISTANCE = 150f;

    private List<Cluster> data;

    public ClusterCollection() {
        data = new ArrayList<>();
    }

    public ClusterCollection(FlightCollection fc) {
        data = new ArrayList<>(fc.size());

        for(Flight f : fc) {
            //data.add(new Cluster(f));
            add(new Cluster(f));
        }
    }

    public void append(Cluster c) {
        data.add(c);
    }

    public Stream<Cluster> parallelStream() {
        return data.parallelStream();
    }

    public Stream<Cluster> stream() {
        return data.stream();
    }

    public void add(Cluster c) {
//        double dist = Double.MAX_VALUE;
//        int posMin = -1;
//
//        for(int i = 0; i < data.size(); i++) {
//            double tmp = get(i).distance(c);
//
//            if(tmp < dist) {
//                dist = tmp;
//                posMin = i;
//            }
//        }
//
//        if(posMin != -1 && dist <= MAX_MERGE_DISTANCE) {
//            get(posMin).merge(c);
//        }
//        else {
//            append(c);
//        }

        Cluster min = data.parallelStream().reduce(null, (c1, c2) -> {
            double dist1 = Double.POSITIVE_INFINITY;
            if(c1 != null) {
                dist1 = c1.distance(c);
            }

            double dist2 = Double.POSITIVE_INFINITY;
            if(c2 != null) {
                dist2 = c2.distance(c);
            }

            if(dist1 <= dist2) {
                if(dist1 <= MAX_MERGE_DISTANCE) {
                    return c1;
                }
                return null;
            }
            else if(dist2 <= MAX_MERGE_DISTANCE) {
                return c2;
            }
            return null;
        });

        if(min != null) {
            min.merge(c);
        }
        else {
            append(c);
        }
    }

    public Cluster get(int i) {
        return data.get(i);
    }

    public void merge(int i1, int i2) {
        Cluster c1 = get(i1);
        Cluster c2 = get(i2);

        c1.merge(c2);

        data.set(Math.min(i1, i2), c1);
        data.remove(Math.max(i1, i2));
    }

    @Override
    public Iterator<Cluster> iterator() {
        return data.iterator();
    }

    public int size() {
        return data.size();
    }
}
