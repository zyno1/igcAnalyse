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

import java.io.*;
import java.util.ArrayList;

public class Wind {
    private ArrayList<Entry> list;

    public Wind(String path) throws IOException {
        FileReader fr = new FileReader(new File(path));
        BufferedReader in = new BufferedReader(fr);
        list = new ArrayList<>();

        String line = in.readLine();

        while ((line = in.readLine()) != null) {
            list.add(new Entry(line));
        }
    }

    public Entry[] get(String location, String date) {
        ArrayList<Entry> res = new ArrayList<>();

        for(Entry t : list) {
            if(t.getLocation().equals(location) && t.getDate().equals(date)) {
                res.add(t);
            }
        }

        return res.toArray(new Entry[0]);
    }

    public void matchLocation(String l) {
        for(int i = 0; i < list.size(); i++) {
            if(! list.get(i).getLocation().equals(l)) {
                list.remove(i);
                i--;
            }
        }
    }

    public void matchAltitude(int alt) {
        for(int i = 0; i < list.size(); i++) {
            if(list.get(i).getAltitude() != alt) {
                list.remove(i);
                i--;
            }
        }
    }

    public void matchDirection(float dir, float p) {
        for(int i = 0; i < list.size(); i++) {
            float tmp = list.get(i).getDirection();
            if(tmp >= dir - p && tmp <= dir + p) {
                list.remove(i);
                i--;
            }
        }
    }

    public void matchDirection(int alt, float dir, float p) {
        for(int i = 0; i < list.size(); i++) {
            float tmp = list.get(i).getDirection();
            if(tmp >= dir - p && tmp <= dir + p && list.get(i).getAltitude() == alt) {
                list.remove(i);
                i--;
            }
        }
    }

    public void matchSpeed(float speed, float p) {
        for(int i = 0; i < list.size(); i++) {
            float tmp = list.get(i).getSpeed();
            if(tmp >= speed - p && tmp <= speed + p) {
                list.remove(i);
                i--;
            }
        }
    }

    public void matchSpeed(int alt, float speed, float p) {
        for(int i = 0; i < list.size(); i++) {
            float tmp = list.get(i).getSpeed();
            if(tmp >= speed - p && tmp <= speed + p && list.get(i).getAltitude() == alt) {
                list.remove(i);
                i--;
            }
        }
    }

    public String[] getDates() {
        String[] res = new String[list.size()];

        for(int i = 0; i < res.length; i++) {
            res[i] = list.get(i).getDate();
            res[i] = res[i].substring(0, res[i].length() - 2);
        }

        return res;
    }
}
