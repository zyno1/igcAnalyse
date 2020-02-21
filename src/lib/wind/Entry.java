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

package lib.wind;

public class Entry {
    private String date;
    private String location;
    private int altitude;
    private float speed;
    private float direction;

    public Entry(String line) {
        String[] split = line.split(";");

        date = split[0];
        location = split[1];
        altitude = Integer.parseInt(split[2]);
        speed = Float.parseFloat(split[3]);
        direction = Float.parseFloat(split[4]);
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public int getAltitude() {
        return altitude;
    }

    public float getSpeed() {
        return speed;
    }

    public float getDirection() {
        return direction;
    }
}
