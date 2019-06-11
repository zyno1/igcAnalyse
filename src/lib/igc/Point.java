package lib.igc;

public class Point {
    private float x;
    private float y;
    private float alt;

    private int time;

    public Point(float x, float y, float alt) {
        this.x = x;
        this.y = y;
        this.alt = alt;
        time = 0;
    }

    public Point(int time, float x, float y, float alt) {
        this.x = x;
        this.y = y;
        this.alt = alt;
        this.time = time;
    }

    public Point(Point p) {
        x = p.x;
        y = p.y;
        alt = p.alt;
        time = p.time;
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

    public float distance(Point p) {
        double R = 6371e3;
        double p1 = Math.toRadians(y);
        double p2 = Math.toRadians(p.y);
        double dp = Math.toRadians(p.y - y);
        double dl = Math.toRadians(p.x - x);

        double a = Math.sin(dp / 2) * Math.sin(dp / 2) +
                Math.cos(p1) * Math.cos(p2) * Math.sin(dl / 2) * Math.sin(dl / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (float) (R * c);
    }

    public static Point average(Point... p) {
        Point res = new Point(0, 0, 0);

        for(Point tmp : p) {
            res.setX(res.getX() + tmp.getX() / p.length);
            res.setY(res.getY() + tmp.getY() / p.length);
            res.setAlt(res.getAlt() + tmp.getAlt() / p.length);
        }

        return res;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Point{" +
                "x = " + x +
                ", y = " + y +
                ", alt = " + alt +
                '}';
    }

    public static void main(String[] args) {
        Point p1 = new Point(5, 50, 0);
        Point p2 = new Point(3, 58, 0);

        System.out.println(p1.distance(p2));
    }
}
