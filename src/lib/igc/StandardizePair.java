package lib.igc;

public class StandardizePair {
    private Point min;
    private Point max;

    public StandardizePair(Point min, Point max) {
        this.min = min;
        this.max = max;
    }

    public StandardizePair() {
        min = max = null;
    }

    public Point getMin() {
        return min;
    }

    public void setMin(Point min) {
        this.min = min;
    }

    public Point getMax() {
        return max;
    }

    public void setMax(Point max) {
        this.max = max;
    }
}
