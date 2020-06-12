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

import lib.igc.Flight;
import lib.igc.Point;

import java.util.ArrayList;
import java.util.Iterator;

public class ThermalCollection implements Iterable<Thermal> {
    ArrayList<Thermal> thermals;

    private static final float MERGE_MAX_DIST = 200;

    public ThermalCollection() {
        thermals = new ArrayList<>();
    }

    public void addThermal(Flight f) {
        Thermal t = new Thermal(f.averagePos(), f.getMin(), f.getMax(), f.climbRate());
        addThermal(t);
    }

    public void addThermal(Thermal t) {
        while(t != null) {
            int posMin = 0;
            float distMin = Float.MAX_VALUE;

            for (int i = 0; i < thermals.size(); i++) {
                Thermal j = thermals.get(i);
                float tmp = j.getPos().distance(t.getPos());

                if (tmp < distMin) {
                    posMin = i;
                    distMin = tmp;
                }
            }

            //this modification may not give the best results
            //but the execution time seems to be divided by 2 on my computer with ~1600 files
            //with ~2200 files I only win 1m out of 3m
            synchronized (this) {
                if(thermals.size() == 0 || (posMin < thermals.size() && thermals.get(posMin).getPos().distance(t.getPos()) == distMin)) {
                    if (posMin < thermals.size() && distMin < MERGE_MAX_DIST) {
                        //Thermal i = thermals.get(posMin);
                        Thermal i = thermals.remove(posMin);

                        i.merge(t);
                        t = i;
                    } else {
                        thermals.add(t);
                        t = null;
                    }
                }
            }
        }
    }

    public synchronized void appendThermal(Thermal t) {
        thermals.add(t);
    }

    public synchronized void mergeExisting() {
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

    public synchronized void sort() {
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

    public synchronized void filter(int min) {
        thermals.removeIf(thermal -> thermal.getCount() < min);
    }

    @Override
    public Iterator<Thermal> iterator() {
        return thermals.iterator();
    }
}