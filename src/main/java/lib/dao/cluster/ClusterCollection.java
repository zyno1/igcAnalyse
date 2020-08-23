package lib.dao.cluster;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ClusterCollection implements Iterable<Cluster> {
    private static final float MAX_MERGE_DISTANCE = 150f;

    private List<Cluster> data;

    public ClusterCollection() {
        data = new ArrayList<>();
    }

    public void append(Cluster c) {
        data.add(c);
    }

    public void add(Cluster c) {
        float dist = Float.MAX_VALUE;
        int posMin = -1;

        for(int i = 0; i < data.size(); i++) {
            float tmp = get(i).distance(c);

            if(tmp < dist) {
                dist = tmp;
                posMin = i;
            }
        }

        if(posMin != -1 && dist <= MAX_MERGE_DISTANCE) {
            get(posMin).merge(c);
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
}
