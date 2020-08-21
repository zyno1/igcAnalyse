package lib.dao.flight;

import lib.igc.Flight;

import java.io.IOException;

public interface FlightDAO {
    Flight load(String path) throws IOException, ClassNotFoundException;
    void save(Flight f, String path) throws IOException;
}
