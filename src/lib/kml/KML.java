package lib.kml;

import lib.thermals.Thermal;
import lib.thermals.ThermalCollection;

import java.io.FileWriter;
import java.io.IOException;

public class KML {
    private static final String header = "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n<Document>\n";
    private static final String footer = "</Document>\n</kml>";


    private StringBuilder str;

    public KML() {
        str = new StringBuilder();
        str.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        str.append(header);
    }

    public void addThermal(Thermal t) {
        str.append("<Placemark><name>");
        str.append("Thermal " + t.getCount());
        str.append("</name><description>\n");
        str.append(t.getDescription());
        str.append("\n</description><Point><coordinates>\n");
        str.append(t.getPos().x + ", " + t.getPos().y + ", 0");
        str.append("\n</coordinates></Point></Placemark>\n");
    }

    public void addThermals(ThermalCollection tc) {
        for(Thermal t : tc) {
            addThermal(t);
        }
    }

    public void writeToFile(String path) throws IOException {
        FileWriter res = new FileWriter(path);

        //res.write(header);
        res.write(str.toString());
        res.write(footer);
        res.flush();
        res.close();
    }
}
