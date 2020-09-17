package lib.cluster;

import lib.igc.Flight;
import lib.igc.FlightCollection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ClusterCollection implements Iterable<Cluster> {
    private static final double MAX_MERGE_DISTANCE = 300f;

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
        for(int i = data.size() - 1; i >= 0; i--) {
            /*if(data.get(i).avgBaseDistanceLowerThan(c, MAX_MERGE_DISTANCE)) {
                Cluster tmp = data.remove(i);
                c.merge(tmp);
            }*/

            if(data.get(i).minDistanceLowerThan(c, MAX_MERGE_DISTANCE)) {
                c.merge(data.remove(i));
            }
        }

        data.add(c);
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

    public void filterBySize(int maxNb) {
        while (size() > maxNb) {
            int posMin = 0;
            int minSize = data.get(posMin).size();

            for(int i = 1; i < size(); i++) {
                if(data.get(i).size() < minSize) {
                    minSize = data.get(i).size();
                    posMin = i;
                }
            }

            data.remove(posMin);
        }
    }
}
