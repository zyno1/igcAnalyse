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

package lib.igc;

import org.json.JSONObject;

import java.io.Serializable;

public class Point implements Serializable {
    public double x; //long
    public double y; //lat
    public double alt;

    public int time;

    public Point(double x, double y, double alt) {
        this.x = x;
        this.y = y;
        this.alt = alt;
        time = 0;
    }

    public Point(int time, double x, double y, double alt) {
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

    public static Point fromJSON(JSONObject obj) {
        Point p = new Point(0, 0, 0);

        p.x = obj.getDouble("x");
        p.y = obj.getDouble("y");
        p.alt = obj.getDouble("alt");
        p.time = obj.getInt("time");

        return p;
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("x", x);
        obj.put("y", y);
        obj.put("alt", alt);
        obj.put("time", time);

        return obj;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getAlt() {
        return alt;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setAlt(double alt) {
        this.alt = alt;
    }

    public double distance(Point p) {
        double R = 6371e3;
        double p1 = Math.toRadians(y);
        double p2 = Math.toRadians(p.y);
        double dp = Math.toRadians(p.y - y);
        double dl = Math.toRadians(p.x - x);

        double a = Math.sin(dp / 2) * Math.sin(dp / 2) +
                Math.cos(p1) * Math.cos(p2) * Math.sin(dl / 2) * Math.sin(dl / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (double) (R * c);
    }

    public double bearing(Point p) {
        double tmp_x = Math.cos(p.y * Math.PI / 180) * Math.sin((p.x - x) * Math.PI / 180);
        double tmp_y = Math.cos(y * Math.PI / 180) * Math.sin(p.y * Math.PI / 180) - Math.sin(y * Math.PI / 180) * Math.cos(p.y * Math.PI / 180) * Math.cos((p.x - x) * Math.PI / 180);

        return (double) Math.atan2(tmp_x, tmp_y);
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

    public static void max(Point res, Point... in) {
        for (Point point : in) {
            res.setX(Math.max(res.getX(), point.getX()));
            res.setY(Math.max(res.getY(), point.getY()));
            res.setAlt(Math.max(res.getAlt(), point.getAlt()));
            res.setTime(Math.max(res.getTime(), point.getTime()));
        }
    }

    public static void min(Point res, Point... in) {
        for (Point point : in) {
            res.setX(Math.min(res.getX(), point.getX()));
            res.setY(Math.min(res.getY(), point.getY()));
            res.setAlt(Math.min(res.getAlt(), point.getAlt()));
            res.setTime(Math.min(res.getTime(), point.getTime()));
        }
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
        Point p1 = new Point(-94.581213f, 39.099912f, 0);
        Point p2 = new Point(-90.200203f, 38.627089f, 0);

        System.out.println(p1.distance(p2));
        System.out.println(p1.bearing(p2));
    }
}
