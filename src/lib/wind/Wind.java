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
