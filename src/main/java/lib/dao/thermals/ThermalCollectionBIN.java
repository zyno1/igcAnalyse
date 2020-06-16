package lib.dao.thermals;

import lib.thermals.ThermalCollection;

import java.io.*;

public class ThermalCollectionBIN implements ThermalCollectionDAO {
    @Override
    public ThermalCollection load(String path) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(path));

        return (ThermalCollection) in.readObject();
    }

    @Override
    public void save(ThermalCollection tc, String path) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path));

        out.writeObject(tc);

        out.flush();
        out.close();
    }
}
