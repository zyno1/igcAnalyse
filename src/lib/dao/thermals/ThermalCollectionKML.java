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
        All,
    }

    private Type type = Type.All;

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
        str.append("</Location>\n<Scale><x>300</x><y>300</y><z>");
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
        out.write("<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n");

        out.write("<Folder>\n<name>Flight Data</name>\n");

        if(type == Type.Placemark || type == Type.All) {
            out.write("<Document>\n<name>Placemarks</name>\n");
            for (Thermal t : tc) {
                out.write(thermalToPlacemark(t));
                out.write("\n");
            }
            out.write("</Document>\n");
        }

        if(type == Type.Cylinder || type == Type.All) {
            out.write("<Document>\n<name>Cylinders</name>\n");
            for (Thermal t : tc) {
                out.write(thermalToCylinder(t));
                out.write("\n");
            }
            out.write("</Document>\n");
        }

        out.write("</Folder>\n</kml>");

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
