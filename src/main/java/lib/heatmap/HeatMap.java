package lib.heatmap;

import lib.igc.Point;

public class HeatMap {
    private final Point min;
    private final Point max;
    private final double dpkm; // dots per kilometer

    private final Cell[][] map;
    private final int w;
    private final int h;

    public HeatMap(Point min, Point max, double dpkm) {
        double d = min.distance(max);
        double b = Math.toRadians(min.bearing(max));

        System.out.println(b);

        System.out.println(min);
        System.out.println(max);

        this.dpkm = dpkm;
        this.min = min;
        this.max = max;

        //w = (int) Math.ceil(min.distance(new Point(max.lon, min.lat, 0)) * dpkm / 1000.0);
        //h = (int) Math.ceil(min.distance(new Point(min.lon, max.lat, 0)) * dpkm / 1000.0);
        w = (int) Math.ceil((max.lon - min.lon) * dpkm / 1000.0);
        h = (int) Math.ceil((max.lat - min.lat) * dpkm / 1000.0);

        System.out.println(w + ", " + h);

        map = new Cell[h][w];
    }

    public void add(Point p, double v) {
        double d = min.distance(p);
        double b = Math.toRadians(min.bearing(p));

        int x = (int) Math.ceil((p.lon - min.lon) * dpkm / 1000.0);
        int y = (int) Math.ceil((p.lat - min.lat) * dpkm / 1000.0);

        if(x >= 0 && x < w && y >= 0 && y < h) {
            if (map[h - 1 - y][x] == null) {
                map[h - 1 - y][x] = new Cell();
            }

            map[h - 1 - y][x].add(v);
        }
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public Cell get(int x, int y) {
        return map[y][x];
    }
}
