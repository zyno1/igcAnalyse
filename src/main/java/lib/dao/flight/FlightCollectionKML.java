package lib.dao.flight;

import lib.igc.Flight;
import lib.igc.FlightCollection;
import lib.igc.Point;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FlightCollectionKML implements FlightCollectionDAO {
    @Override
    public FlightCollection load(String path) throws IOException, ClassNotFoundException {
        return null;
    }

    @Override
    public void save(FlightCollection fc, String path) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(path));

        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        out.write("<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n");

        out.write("<Folder>\n<name>Flight Data</name>\n<Document>");

        int i = 0;
        for(Flight f : fc) {
            out.write("<Placemark><name>Flight" + i + "</name><LineString><tessellate>1</tessellate>");
            out.write("<altitudeMode>absolute</altitudeMode><coordinates>\n");

            for (Point p : f) {
                out.write(p.x + "," + p.y + "," + p.alt + "\n");
            }

            out.write("</coordinates></LineString></Placemark>\n");
            i++;
        }

        out.write("</Document></Folder>\n</kml>");

        out.flush();
        out.close();
    }

    public static void main(String[] argc) throws IOException, ClassNotFoundException {
        Flight f = new Flight("NetCoupe2020_19219.igc");
        FlightCollection fc = new FlightCollection();
        fc.addFlight(f);

        FlightCollectionDAO dao = new FlightCollectionKML();
        dao.save(fc, "res.kml");
    }
}
