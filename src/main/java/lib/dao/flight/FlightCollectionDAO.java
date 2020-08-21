package lib.dao.flight;

import lib.igc.Flight;
import lib.igc.FlightCollection;

import java.io.IOException;

public interface FlightCollectionDAO {
    FlightCollection load(String path) throws IOException, ClassNotFoundException;
    void save(FlightCollection f, String path) throws IOException;
}
