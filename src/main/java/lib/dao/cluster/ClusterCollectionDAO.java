package lib.dao.cluster;

import lib.cluster.ClusterCollection;
import lib.igc.FlightCollection;

import java.io.IOException;

public interface ClusterCollectionDAO {
    ClusterCollection load(String path) throws IOException, ClassNotFoundException;
    void save(ClusterCollection c, String path) throws IOException;
}
