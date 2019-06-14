package lib.thermals;

import lib.igc.Point;

public class Thermal {
    private Point pos;
    private int count;

    public Thermal(Point pos) {
        this.pos = pos;
        count = 1;
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
}
