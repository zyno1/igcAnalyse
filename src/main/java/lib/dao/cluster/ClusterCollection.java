package lib.dao.cluster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class ClusterCollection implements Iterable<Cluster> {
    private Collection<Cluster> data;

    public ClusterCollection() {
        data = new ArrayList<>();
    }

    public void add(Cluster c) {
        data.add(c);
    }

    @Override
    public Iterator<Cluster> iterator() {
        return data.iterator();
    }
}
