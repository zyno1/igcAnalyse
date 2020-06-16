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

package lib.thermals;

import lib.igc.Point;
import org.json.JSONObject;

import java.io.Serializable;

public class Thermal implements Serializable {
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

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();

        obj.put("pos", pos.toJSON());
        obj.put("min", min.toJSON());
        obj.put("max", max.toJSON());
        obj.put("climbRate", climbRate);
        obj.put("count", count);

        return obj;
    }

    public static Thermal fromJSON(JSONObject obj) {
        Thermal t = new Thermal(null, null, null, 0);

        t.pos = Point.fromJSON(obj.getJSONObject("pos"));
        t.min = Point.fromJSON(obj.getJSONObject("min"));
        t.max = Point.fromJSON(obj.getJSONObject("max"));
        t.climbRate = obj.getFloat("climbRate");
        t.count = obj.getInt("count");

        return t;
    }

    public Point getMin() {
        return min;
    }

    public Point getMax() {
        return max;
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

    public String getDescription() {
        StringBuilder str = new StringBuilder();
        int minH = min.time / 3600;
        int minM = (min.time / 60) % 60;

        int maxH = max.time / 3600;
        int maxM = (max.time / 60) % 60;

        float minAlt = min.alt;
        float maxAlt = max.alt;

        float cr = (float)Math.floor(climbRate * 10) / 10;

        str.append("alt: ").append(minAlt).append(" - ").append(maxAlt).append("; ");
        str.append("time: ").append(minH).append("H").append(minM).append(" - ").append(maxH).append("H").append(maxM).append("; ");
        str.append("speed: ").append(cr).append(" m/s; ");

        return str.toString();
    }
}
