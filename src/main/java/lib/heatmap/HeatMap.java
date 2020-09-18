package lib.heatmap;

import lib.igc.Point;

public class HeatMap {
    private static final double MAX_DISTANCE = 50;
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
        int x = (int) Math.ceil((p.lon - min.lon) * dpkm / 1000.0);
        int y = (int) Math.ceil((p.lat - min.lat) * dpkm / 1000.0);

        /*Point p2 = new Point(0, 0, 0);

        int minX = x;
        double tmp = 0;
        p2.lat = p.lat;
        while (tmp <= MAX_DISTANCE) {
            double dlon = minX * 1000.0 / dpkm;

            p2.lon = dlon + min.lon;
            tmp = p2.distance(p);
            minX--;
        }

        int minY = y;
        tmp = 0;
        p2.lon = p.lon;
        while (tmp <= MAX_DISTANCE) {
            double dlat = minY * 1000.0 / dpkm;

            p2.lat = dlat + min.lat;
            tmp = p2.distance(p);
            minY--;
        }

        for(int j = minY; j <= y + y - minY; j++) {
            for (int i = minX; i < x + x - minX; i++) {
                double dlon = i * 1000.0 / dpkm;
                double dlat = j * 1000.0 / dpkm;

                p2.lat = dlat + min.lat;
                p2.lon = dlon + min.lon;
                if (p2.distance(p) <= MAX_DISTANCE && i >= 0 && i < w && j >= 0 && j < h) {
                    if(map[h - 1 - j][i] == null) {
                        map[h - 1 - j][i] = new Cell();
                    }
                    map[h - 1 - j][i].add(v);
                }
            }
        }*/

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
