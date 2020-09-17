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
    public double lon; //long x
    public double lat; //lat y
    public double alt;

    public int time;

    public Point(double lon, double lat, double alt) {
        this.lon = lon;
        this.lat = lat;
        this.alt = alt;
        time = 0;
    }

    public Point(int time, double x, double y, double alt) {
        this.lon = x;
        this.lat = y;
        this.alt = alt;
        this.time = time;
    }

    public Point(Point p) {
        lon = p.lon;
        lat = p.lat;
        alt = p.alt;
        time = p.time;
    }

    public static Point fromJSON(JSONObject obj) {
        Point p = new Point(0, 0, 0);

        p.lon = obj.getDouble("x");
        p.lat = obj.getDouble("y");
        p.alt = obj.getDouble("alt");
        p.time = obj.getInt("time");

        return p;
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("x", lon);
        obj.put("y", lat);
        obj.put("alt", alt);
        obj.put("time", time);

        return obj;
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

    public double getAlt() {
        return alt;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setAlt(double alt) {
        this.alt = alt;
    }

    public double distance(Point p) {
        double R = 6371e3;
        double p1 = Math.toRadians(lat);
        double p2 = Math.toRadians(p.lat);
        double dp = Math.toRadians(p.lat - lat);
        double dl = Math.toRadians(p.lon - lon);

        double a = Math.sin(dp / 2) * Math.sin(dp / 2) +
                Math.cos(p1) * Math.cos(p2) * Math.sin(dl / 2) * Math.sin(dl / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    public double bearing(Point p) {
        double tmp_x = Math.cos(p.lat * Math.PI / 180) * Math.sin((p.lon - lon) * Math.PI / 180);
        double tmp_y = Math.cos(lat * Math.PI / 180) * Math.sin(p.lat * Math.PI / 180) - Math.sin(lat * Math.PI / 180) * Math.cos(p.lat * Math.PI / 180) * Math.cos((p.lon - lon) * Math.PI / 180);

        double res = Math.atan2(tmp_x, tmp_y) * 180 / Math.PI;
        //while (res < 0) {
        //    res += 360;
        //}
        return res;
    }

    public static Point average(Point... p) {
        Point res = new Point(0, 0, 0);

        for(Point tmp : p) {
            res.setLon(res.getLon() + tmp.getLon() / p.length);
            res.setLat(res.getLat() + tmp.getLat() / p.length);
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
            res.setLon(Math.max(res.getLon(), point.getLon()));
            res.setLat(Math.max(res.getLat(), point.getLat()));
            res.setAlt(Math.max(res.getAlt(), point.getAlt()));
            res.setTime(Math.max(res.getTime(), point.getTime()));
        }
    }

    public static void min(Point res, Point... in) {
        for (Point point : in) {
            res.setLon(Math.min(res.getLon(), point.getLon()));
            res.setLat(Math.min(res.getLat(), point.getLat()));
            res.setAlt(Math.min(res.getAlt(), point.getAlt()));
            res.setTime(Math.min(res.getTime(), point.getTime()));
        }
    }

    @Override
    public String toString() {
        return "Point{" +
                "x = " + lon +
                ", y = " + lat +
                ", alt = " + alt +
                '}';
    }

    public static void main(String[] args) {
        Point p1 = new Point(-94.581213f, 39.099912f, 0);
        Point p2 = new Point(-90.200203f, 38.627089f, 0);

        System.out.println(p1.distance(p2));
        System.out.println(p1.bearing(p2));
        System.out.println(p2.bearing(p1));
        System.out.println(p2.bearing(p1) - p1.bearing(p2));
    }
}
