package lib.dao.thermals;

import lib.thermals.ThermalCollection;

import java.io.IOException;

public interface ThermalCollectionDAO {
    ThermalCollection load(String path) throws IOException;
    void save(ThermalCollection tc, String path) throws IOException;

    public static void main(String[] args) throws IOException {
        ThermalCollectionDAO dao = new ThermalCollectionCUP();
        ThermalCollection tc = dao.load("res.cup");
        dao.save(tc, "res.2.cup");
    }
}
