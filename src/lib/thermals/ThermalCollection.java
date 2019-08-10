package lib.thermals;

import lib.igc.Point;
import lib.igc.StandardizePair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Predicate;

public class ThermalCollection implements Iterable<Thermal> {
    ArrayList<Thermal> thermals;

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

    public void addThermal(Point p) {
        addThermal(new Thermal(p));
    }

    public void addThermal(Thermal t) {
        for (Thermal i : this) {
            if(i.getPos().distance(t.getPos()) < 150) {
                Point p = i.getPos();

                int ic = i.getCount();
                int tc = t.getCount();

                float xi = i.getPos().getX() * ic;
                float yi = i.getPos().getY() * ic;
                float alti = i.getPos().getAlt() * ic;

                float xt = t.getPos().getX() * tc;
                float yt = t.getPos().getY() * tc;
                float altt = t.getPos().getAlt() * tc;

                float x = (xi + xt) / (ic + tc);
                float y = (yi + yt) / (ic + tc);
                float alt = (alti + altt) / (ic +  tc);

                p.setX(x);
                p.setY(y);
                p.setAlt(alt);

                i.setCount(ic + tc);


                return;
            }
        }
        thermals.add(t);
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

    public void addThermals(Thermal... t) {
        for(Thermal i : t) {
            addThermal(i);
        }
    }

    public void addThermals(Point... p) {
        for(Point i : p) {
            addThermal(new Thermal(i));
        }
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
        thermals.removeIf(new Predicate<Thermal>() {
            @Override
            public boolean test(Thermal thermal) {
                return thermal.getCount() < min;
            }
        });
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
