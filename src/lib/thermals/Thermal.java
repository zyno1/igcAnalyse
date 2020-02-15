package lib.thermals;

import lib.igc.Point;

public class Thermal {
    private Point pos;
    private Point min;
    private Point max;
    private float climbRate;
    private int count;

    public Thermal(Point pos, Point min, Point max, float climbRate) {
        this.pos = pos;
        this.min = min;
        this.max = max;
        this.climbRate = climbRate;
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
    }

    public void merge(Thermal t) {
        Point.min(min, t.min);
        Point.max(max, t.max);

        int total = count + t.count;

        climbRate = (climbRate * count + t.climbRate * t.count) / total;

        pos.x = (pos.x * count + t.pos.x * t.count) / total;
        pos.y = (pos.y * count + t.pos.y * t.count) / total;
        pos.alt = (pos.alt * count + t.pos.alt * t.count) / total;

        count = total;
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
        str.append("\",,FR,");

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
        str.append("m,15,,,,\"");
        str.append(climbRate + "");
        str.append("\"");

        return str.toString();
    }
}
