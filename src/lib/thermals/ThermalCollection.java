package lib.thermals;

import lib.igc.Flight;
import lib.igc.Point;
import lib.igc.StandardizePair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Predicate;

public class ThermalCollection implements Iterable<Thermal> {
    ArrayList<Thermal> thermals;

    private static final float MERGE_MAX_DIST = 200;

    public ThermalCollection() {
        thermals = new ArrayList<>();
    }

    public ThermalCollection(String data) {
        String[] splitted = data.split("\n");

        //"name, code, country, lat, lon, elev, style, rwydir, rwylen, freq, desc"

        String line = splitted[0];
        if(!line.contains("name") && !line.contains("code") && !line.contains("country") && !line.contains("lat") && !line.contains("lon") && !line.contains("elev")) {
            thermals.add(new Thermal(line));
        }

        for(int i = 1; i < splitted.length; i++) {
            thermals.add(new Thermal(splitted[i]));
        }
    }

    public void addThermal(Flight f) {
        Thermal t = new Thermal(f.averagePos(), f.getMin(), f.getMax(), f.climbRate());
        addThermal(t);
    }

    public void addThermal(Thermal t) {
        int posMin = 0;
        float distMin = Float.MAX_VALUE;

        for(int i = 0; i < thermals.size(); i++) {
            Thermal j = thermals.get(i);
            float tmp = j.getPos().distance(t.getPos());

            if(tmp < distMin) {
                posMin = i;
                distMin = tmp;
            }
        }

        if(posMin < thermals.size() && distMin < MERGE_MAX_DIST) {
            Thermal i = thermals.get(posMin);

            i.merge(t);
        }
        else {
            thermals.add(t);
        }
    }

    public void mergeExisting() {
        boolean modified = true;

        while (modified) {
            modified = false;

            for(int i = 0; i < thermals.size() - 1; i++) {
                Thermal ti = thermals.get(i);

                float distMin = Float.MAX_VALUE;
                int posMin = -1;

                for(int j = i + 1; j < thermals.size(); j++) {
                    Thermal tj = thermals.get(j);
                    float dist = ti.getPos().distance(tj.getPos());
                    if(dist < distMin) {
                        posMin = j;
                        distMin = dist;
                    }
                }

                if(posMin != -1 && distMin < MERGE_MAX_DIST) {
                    ti.merge(thermals.get(posMin));
                    thermals.remove(posMin);
                    modified = true;
                }
            }
        }
    }

    public int size() {
        return thermals.size();
    }

    public int getMin() {
        int i = Integer.MAX_VALUE;

        for(Thermal t : thermals) {
            i = Math.min(t.getCount(), i);
        }

        return i;
    }

    public int getMax() {
        int i = Integer.MIN_VALUE;

        for(Thermal t : thermals) {
            i = Math.max(t.getCount(), i);
        }

        return i;
    }

    public void sort() {
        if(thermals.size() > 2) {
            Point start = thermals.get(0).getPos();

            for(int i = 1; i < thermals.size(); i++) {
                int posMin = i;
                for(int j = i + 1; j < thermals.size(); j++) {
                    if(start.distance(thermals.get(posMin).getPos()) > start.distance(thermals.get(j).getPos())) {
                        posMin = j;
                    }
                }

                if(posMin != i) {
                    Thermal t = thermals.get(posMin);
                    thermals.set(posMin, thermals.get(i));
                    thermals.set(i, t);
                }
                start = thermals.get(i).getPos();
            }
        }
    }

    public void standardize(StandardizePair sdp) {
        for(Thermal t : this) {
            Point p = t.getPos();
            p.setX((p.getX() - sdp.getMin().getX()));
            p.setY((p.getY() - sdp.getMin().getY()));
            p.setAlt((p.getAlt() - sdp.getMin().getAlt()));
        }

        for(Thermal t : this) {
            Point p = t.getPos();
            p.setX((p.getX() / sdp.getMax().getX()));
            p.setY((p.getY() / sdp.getMax().getY()));
            p.setAlt((p.getAlt() / sdp.getMax().getAlt()));
        }
    }

    public void filter(int min) {
        thermals.removeIf(thermal -> thermal.getCount() < min);
    }

    public String toCUP() {
        StringBuilder str = new StringBuilder();

        str.append("name,code,country,lat,lon,elev,style,rwydir,rwylen,freq,desc");

        for(Thermal t : this) {
            str.append("\n");
            str.append(t.toCUP());
        }

        return str.toString();
    }

    @Override
    public Iterator<Thermal> iterator() {
        return thermals.iterator();
    }
}
