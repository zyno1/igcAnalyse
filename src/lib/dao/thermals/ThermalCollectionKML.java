package lib.dao.thermals;

import lib.thermals.Thermal;
import lib.thermals.ThermalCollection;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ThermalCollectionKML implements ThermalCollectionDAO {
    @Override
    public ThermalCollection load(String path) throws IOException {
        throw new IOException();
        //return null;
    }

    @Override
    public void save(ThermalCollection tc, String path) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(path));

        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        out.write("<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n<Document>\n");

        for(Thermal t : tc) {
            out.write("<Placemark><name>");
            out.write("T");
            out.write("</name><description>\n");
            out.write("Thermal " + t.getCount() + "\n");
            out.write(t.getDescription().replace("; ", "\n"));
            out.write("\n</description><Point><coordinates>\n");
            out.write(t.getPos().x + ", " + t.getPos().y + ", 0");
            out.write("\n</coordinates></Point></Placemark>\n");
        }

        out.write("</Document>\n</kml>");

        out.flush();
        out.close();
    }
}
