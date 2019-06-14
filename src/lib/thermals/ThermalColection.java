package lib.thermals;

import lib.igc.Point;
import lib.igc.StandardizePair;

import java.util.ArrayList;
import java.util.Iterator;

public class ThermalColection implements Iterable<Thermal> {
    ArrayList<Thermal> thermals;

    public ThermalColection() {
        thermals = new ArrayList<>();
    }

    public void addThermal(Point p) {
        addThermal(new Thermal(p));
    }

    public void addThermal(Thermal t) {
        for (Thermal i : this) {
            if(i.getPos().distance(t.getPos()) < 150) {
                Point p = i.getPos();
                p.setX((p.getX() + t.getPos().getX()) / 2);
                p.setY((p.getY() + t.getPos().getY()) / 2);
                p.setAlt((p.getAlt() + t.getPos().getAlt()) / 2);

                i.setCount(i.getCount() + t.getCount());
                return;
            }
        }
        thermals.add(t);
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

    @Override
    public Iterator<Thermal> iterator() {
        return thermals.iterator();
    }
}