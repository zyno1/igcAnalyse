package lib.thermals;

import lib.igc.Point;

public class Thermal {
    private Point pos;
    private int count;

    public Thermal(Point pos) {
        this.pos = pos;
        count = 1;
    }

    public Thermal(String line) {
        pos = new Point(0,0,0);
        count = 0;

        String[] fields = line.split(",");
        String name = fields[0];
        String lat = fields[3];
        String lon = fields[4];
        String alt = fields[5];

        name = name.substring("Thermal ".length() + 1, name.length() - 1);
        count = Integer.valueOf(name);

        float tmp = Integer.valueOf(lat.substring(0, 2));
        tmp += Float.valueOf(lat.substring(2, 8)) / 60.0;

        if(lat.contains("S")) {
            tmp = -tmp;
        }

        pos.setY(tmp);

        tmp = Integer.valueOf(lon.substring(0, 3));
        tmp += Float.valueOf(lon.substring(3, 9)) / 60;

        if(lon.contains("W")) {
            tmp = -tmp;
        }

        pos.setX(tmp);

        tmp = Float.valueOf(alt.substring(0, alt.length() - 1));
        pos.setAlt(tmp);
    }

    public Point getPos() {
        return pos;
    }

    public void setPos(Point pos) {
        this.pos = pos;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String toCUP() {
        StringBuilder str = new StringBuilder();

        str.append("\"Thermal ");
        str.append(count);
        str.append("\",\"Th\",FR,");

        float lat = pos.getY();
        String dlat = "N";
        if(lat < 0) {
            dlat = "S";
            lat = -lat;
        }
        float lon = pos.getX();
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

        str.append(pos.getAlt());
        str.append("m,0,,,,\"\"");

        return str.toString();
    }

    public static void main(String[] args) {
        Thermal t = new Thermal(new Point(14.17445f, 1.123f, 100.1234450f));
        System.out.println(t.toCUP());

        String line = "\"Thermal 1\",\"Th\",FR,0107.380N,01410.466E,100.12344m,0,,,,\"\"";

        t = new Thermal(line);
        System.out.println(t.toCUP());
    }
}
