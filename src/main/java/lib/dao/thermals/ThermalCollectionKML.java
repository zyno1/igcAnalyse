/*
    igcAnalyse
    Copyright (C) 2020  Olivier Zeyen

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

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

    private final Type type = Type.All;

    @Override
    public ThermalCollection load(String path) throws IOException {
        throw new IOException();
        //return null;
    }

    public String thermalToCylinder(Thermal t) {
        final String str = "<Placemark><name>" +
                "T" +
                "</name><description>\n" +
                "Thermal " + t.getCount() + "\n" +
                t.getDescription().replace("; ", "\n") +
                "\n</description>\n" +
                "<Model><altitudeMode>absolute</altitudeMode>\n<Location>" +
                "<longitude>" + t.getPos().x + "</longitude>" +
                "<latitude>" + t.getPos().y + "</latitude>" +
                "<altitude>" + t.getMin().alt + "</altitude>" +
                "</Location>\n<Scale><x>300</x><y>300</y><z>" +
                (t.getMax().alt - t.getMin().alt) + "</z>\n" +
                "</Scale>\n<Link><href>cylinder.dae</href></Link>\n</Model>" +
                "</Placemark>";
        return str;
    }

    public String thermalToPlacemark(Thermal t) {
        final String str = "<Placemark><name>" +
                "T" +
                "</name><description>\n" +
                "Thermal " + t.getCount() + "\n" +
                t.getDescription().replace("; ", "\n") +
                "\n</description><Point><coordinates>\n" +
                t.getPos().x + ", " + t.getPos().y + ", 0" +
                "\n</coordinates></Point></Placemark>";
        return str;
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

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ThermalCollectionDAO cup = new ThermalCollectionCUP();
        ThermalCollectionDAO kml = new ThermalCollectionKML();

        ThermalCollection tc = cup.load("res.cup");

        kml.save(tc, "res.kml");
    }
}
