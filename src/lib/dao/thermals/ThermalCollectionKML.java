package lib.dao.thermals;

import lib.thermals.Thermal;
import lib.thermals.ThermalCollection;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ThermalCollectionKML implements ThermalCollectionDAO {

    public enum Type {
        Cylinder,
        Placemark,
    }

    private Type type = Type.Placemark;

    @Override
    public ThermalCollection load(String path) throws IOException {
        throw new IOException();
        //return null;
    }

    public String thermalToCylinder(Thermal t) {
        StringBuilder str = new StringBuilder();

        str.append("<Placemark><name>");
        str.append("T");
        str.append("</name><description>\n");
        str.append("Thermal " + t.getCount() + "\n");
        str.append(t.getDescription().replace("; ", "\n"));
        str.append("\n</description>\n");

        str.append("<Model><altitudeMode>absolute</altitudeMode>\n<Location>");
        str.append("<longitude>" + t.getPos().x + "</longitude>");
        str.append("<latitude>" + t.getPos().y + "</latitude>");
        str.append("<altitude>" + t.getMin().alt + "</altitude>");
        str.append("</Location>\n<Scale><x>100</x><y>100</y><z>");
        str.append((t.getMax().alt - t.getMin().alt) + "</z>\n");
        str.append("</Scale>\n<Link><href>cylinder.dae</href></Link>\n</Model>");

        str.append("</Placemark>");

        return str.toString();
    }

    public String thermalToPlacemark(Thermal t) {
        StringBuilder str = new StringBuilder();

        str.append("<Placemark><name>");
        str.append("T");
        str.append("</name><description>\n");
        str.append("Thermal " + t.getCount() + "\n");
        str.append(t.getDescription().replace("; ", "\n"));
        str.append("\n</description><Point><coordinates>\n");
        str.append(t.getPos().x + ", " + t.getPos().y + ", 0");
        str.append("\n</coordinates></Point></Placemark>");

        return str.toString();
    }

    @Override
    public void save(ThermalCollection tc, String path) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(path));

        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        out.write("<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n<Document>\n");

        for(Thermal t : tc) {
            if(type == Type.Placemark) {
                out.write(thermalToPlacemark(t));
            }
            else if(type == Type.Cylinder) {
                out.write(thermalToCylinder(t));
            }

            out.write("\n");
        }

        out.write("</Document>\n</kml>");

        out.flush();
        out.close();
    }

    public static void main(String[] args) throws IOException {
        ThermalCollectionDAO cup = new ThermalCollectionCUP();
        ThermalCollectionDAO kml = new ThermalCollectionKML();

        ThermalCollection tc = cup.load("res.cup");
        kml.save(tc, "res.kml");
    }
}
