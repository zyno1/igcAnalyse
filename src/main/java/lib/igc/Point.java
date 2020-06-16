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
    public float x;
    public float y;
    public float alt;

    public int time;

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

    public static Point fromJSON(JSONObject obj) {
        Point p = new Point(0, 0, 0);

        p.x = obj.getFloat("x");
        p.y = obj.getFloat("y");
        p.alt = obj.getFloat("alt");
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
        Point p1 = new Point(5, 50, 0);
        Point p2 = new Point(3, 58, 0);

        System.out.println(p1.distance(p2));
    }
}
