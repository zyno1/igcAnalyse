package lib.dao.thermals;

import lib.igc.Point;
import lib.thermals.Thermal;
import lib.thermals.ThermalCollection;

import java.io.*;

public class ThermalCollectionCUP implements ThermalCollectionDAO {
    private static final String CUP_HEADER = "name,code,country,lat,lon,elev,style,rwydir,rwylen,freq,desc";

    private Thermal loadThermalFromLine(String line) {
        Point pos = new Point(0,0,0);
        int count = 0;

        String[] fields = line.split(",");
        String name = fields[0];
        String lat = fields[3];
        String lon = fields[4];
        String alt = fields[5];
        String[] description = fields[fields.length - 1].split("; ");

        name = name.substring("Thermal ".length() + 1, name.length() - 1);
        count = Integer.parseInt(name);

        float tmp = Integer.parseInt(lat.substring(0, 2));
        tmp += Float.parseFloat(lat.substring(2, 8)) / 60.0;

        if(lat.contains("S")) {
            tmp = -tmp;
        }

        pos.setY(tmp);

        tmp = Integer.parseInt(lon.substring(0, 3));
        tmp += Float.parseFloat(lon.substring(3, 9)) / 60;

        if(lon.contains("W")) {
            tmp = -tmp;
        }

        pos.setX(tmp);

        tmp = Float.parseFloat(alt.substring(0, alt.length() - 1));
        pos.setAlt(tmp);

        Point min = new Point(Integer.MAX_VALUE, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
        Point max = new Point(Integer.MIN_VALUE, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
        float climbRate = 0;

        for (String i : description) {
            if(i.contains("alt")) {
                String[] split = i.split("-");
                min.alt = Float.parseFloat(split[0].replaceAll("[^0-9\\.]", ""));
                max.alt = Float.parseFloat(split[1].replaceAll("[^0-9\\.]", ""));
            }
            else if(i.contains("time")) {
                String[] split = i.split("-");
                String[] st = split[0].toUpperCase().split("H");
                String[] et = split[1].toUpperCase().split("H");

                min.time = 3600 * Integer.parseInt(st[0].replaceAll("[^0-9]", "")) + 60 * Integer.parseInt(st[1].replaceAll("[^0-9]", ""));
                max.time = 3600 * Integer.parseInt(et[0].replaceAll("[^0-9]", "")) + 60 * Integer.parseInt(et[1].replaceAll("[^0-9]", ""));
            }
            else if(i.contains("speed")) {
                i = i.replaceAll("[^0-9\\.]", "");
                climbRate = Float.parseFloat(i);
            }
        }

        Thermal res = new Thermal(pos, min, max, climbRate);
        res.setCount(count);
        return res;
    }

    private String thermalToCup(Thermal t) {
        StringBuilder str = new StringBuilder();

        str.append("\"Thermal ");
        str.append(t.getCount());
        str.append("\",,FR,");

        float lat = t.getPos().getY();
        String dlat = "N";
        if(lat < 0) {
            dlat = "S";
            lat = -lat;
        }
        float lon = t.getPos().getX();
        String dlon = "E";
        if(lon < 0) {
            dlon = "W";
            lon = -lon;
        }

        str.append(String.format("%02d", (int)lat));

        lat -= (int)lat;
        lat *= 60;

        str.append(String.format("%02d.", (int)lat));

        lat -= (int)lat;

        while (lat < 100 && lat != 0) {
            lat *= 10;
        }
        str.append(String.format("%03d", (int)lat));
        str.append(dlat);
        str.append(",");

        str.append(String.format("%03d", (int)lon));

        lon -= (int)lon;
        lon *= 60;

        str.append(String.format("%02d.", (int)lon));

        lon -= (int)lon;

        while (lon < 100 && lon != 0) {
            lon *= 10;
        }
        str.append(String.format("%03d", (int)lon));
        str.append(dlon);
        str.append(",");

        str.append(t.getPos().getAlt());
        str.append("m,15,,,,\"");

        str.append(t.getDescription());

        str.append("\"");

        return str.toString();
    }

    @Override
    public ThermalCollection load(String path) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(path));

        String line;
        ThermalCollection tc = new ThermalCollection();

        while ((line = in.readLine()) != null) {
            if(!line.equals(CUP_HEADER) && line.length() != 0) {
                tc.appendThermal(loadThermalFromLine(line));
            }
        }

        in.close();

        return tc;
    }

    @Override
    public void save(ThermalCollection tc, String path) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(path));

        out.write(CUP_HEADER);
        out.write("\n");

        for(Thermal t : tc) {
            out.write(thermalToCup(t));
            out.write("\n");
        }

        out.flush();
        out.close();
    }
}
