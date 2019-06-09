package lib.igc;

public class Point {
    private float x;
    private float y;
    private float alt;

    public Point(float x, float y, float alt) {
        this.x = x;
        this.y = y;
        this.alt = alt;
    }

    public Point(Point p) {
        x = p.x;
        y = p.y;
        alt = p.alt;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getAlt() {
        return alt;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setAlt(float alt) {
        this.alt = alt;
    }

    @Override
    public String toString() {
        return "Point{" +
                "x = " + x +
                ", y = " + y +
                ", alt = " + alt +
                '}';
    }
}
