package lib.dao.cluster;

import lib.cluster.Cluster;
import lib.cluster.ClusterCollection;
import lib.dao.flight.FlightCollectionDAO;
import lib.dao.flight.FlightCollectionKML;
import lib.igc.Flight;
import lib.igc.FlightCollection;
import lib.igc.Point;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ClusterCollectionKML implements ClusterCollectionDAO {

    @Override
    public ClusterCollection load(String path) throws IOException, ClassNotFoundException {
        return null;
    }

    @Override
    public void save(ClusterCollection cc, String path) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(path));

        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        out.write("<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n");

        out.write("<Folder>\n<name>Cluster Data</name>\n<Document>");

        int i = 0;
        for(Cluster c : cc) {
            out.write("<Placemark><name>Cluster " + i + "</name><Polygon><tessellate>1</tessellate>");
            out.write("<altitudeMode>absolute</altitudeMode><outerBoundaryIs><LinearRing><coordinates>\n");

            Point avg = c.getAvgPos();

            List<Point> hull = c.convexHull();
            for (Point p : hull) {
                out.write(p.lon + "," + p.lat + "," + avg.alt + "\n");
            }

            out.write(hull.get(0).lon + "," + hull.get(0).lat + "," + avg.alt + "\n");

            out.write("</coordinates></LinearRing></outerBoundaryIs></Polygon></Placemark>\n");
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
        dao.save(f.findClimbingPaths(), "res_flight.kml");

        ClusterCollectionDAO cdao = new ClusterCollectionKML();
        cdao.save(new ClusterCollection(f.findClimbingPaths()), "res_poly.kml");
    }
}
