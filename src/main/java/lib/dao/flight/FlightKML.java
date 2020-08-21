package lib.dao.flight;

import lib.igc.Flight;
import lib.igc.Point;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FlightKML implements FlightDAO {
    @Override
    public Flight load(String path) throws IOException, ClassNotFoundException {
        return null;
    }

    @Override
    public void save(Flight f, String path) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(path));

        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        out.write("<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n");

        out.write("<Folder>\n<name>Flight Data</name>\n");

        out.write("<Document><Placemark><name>Flight</name><LineString><tessellate>1</tessellate>");
        out.write("<altitudeMode>absolute</altitudeMode><coordinates>\n");

        for(Point p : f) {
            out.write(p.x + "," + p.y + "," + p.alt + "\n");
        }

        out.write("</coordinates></LineString></Placemark>");

        out.write("</Document></Folder>\n</kml>");

        out.flush();
        out.close();
    }

    public static void main(String[] argc) throws IOException, ClassNotFoundException {
        Flight f = new Flight("NetCoupe2020_19219.igc");

        FlightDAO dao = new FlightKML();
        dao.save(f, "res.kml");
    }
}
