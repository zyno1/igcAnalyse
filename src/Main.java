import lib.igc.Flight;
import lib.igc.FlightCollection;
import lib.igc.Point;
import lib.igc.StandardizePair;
import lib.obj.PointCollection;
import lib.thermals.ThermalCollection;
import lib.wind.Entry;
import lib.wind.Wind;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] argc) {
        /*try {
            FlightCollection tmp = new FlightCollection("data/NetCoupe2019_8643.igc", "data/NetCoupe2019_8606.igc");
            //FlightCollection tmp = new FlightCollection("data/2019-06-01-XCS-AAA-01.igc", "data/2019-06-01-XCS-AAA-02.igc");
            //Flight tmp = new Flight("data/NetCoupe2019_8643.igc");
            //Flight tmp = new Flight("data/2019-06-01-XCS-AAA-02.igc");
            ArrayList<Point> th = tmp.findThermals();
            ThermalCollection thc = new ThermalCollection();
            thc.addThermals(th.toArray(new Point[0]));
            thc.sort();

            StandardizePair sdp = tmp.standardize();
            thc.standardize(sdp);

            PointCollection pc = new PointCollection();
            pc.addFlightCollection(tmp);
            pc.writeToFile("data.obj");

            pc = new PointCollection();
            pc.addThermalCollection(thc);
            pc.writeToFile("thermals.obj");
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        try {
            Wind w = new Wind("data/wind.txt");
            w.matchLocation("LFNC");
            /*Entry[] date = w.get("LFNC", "2019063015");

            if(date.length != 0) {
                for(Entry tmp : date) {
                    w.matchDirection(tmp.getAltitude(), tmp.getDirection(), 5);
                    w.matchSpeed(tmp.getAltitude(), tmp.getSpeed(), 20);
                }
            }*/

            System.out.println("Dates;");
            System.out.println("\t" + Arrays.toString(w.getDates()));
            System.out.println("---------------------------");

            FlightCollection fc = new FlightCollection();
            for(String d : w.getDates()) {
                String path = "data/LFNC/" + d + "/";
                //System.out.println(path);

                File folder = new File(path);
                File[] listOfFiles = folder.listFiles();

                if(listOfFiles != null) {
                    for (File file : listOfFiles) {
                        if (file.isFile()) {
                            //System.out.println(file.getAbsolutePath());
                            fc.addFlight(new Flight(file.getAbsolutePath()));
                        }
                    }
                }
                else {
                    System.out.println(path + " : not found");
                }
            }

            int i = 0;

            ThermalCollection tc = new ThermalCollection();
            Iterator<Flight> it = fc.iterator();

            AtomicInteger co = new AtomicInteger(0);

            Runnable r = () -> {
                Flight f = null;
                synchronized (it) {
                    if(it.hasNext()) {
                        f = it.next();
                    }
                }

                while (f != null) {
                    for(Flight t : f.findThermals()) {
                        synchronized (tc) {
                            tc.addThermal(t.averagePos());
                        }
                    }
                    System.out.println(co.getAndIncrement() + "/" + fc.size());
                    synchronized (it) {
                        if(it.hasNext()) {
                            f = it.next();
                        }
                        else {
                            f = null;
                        }
                    }
                }
            };

            Thread[] threads = new Thread[3];

            for (int k = 0; k < threads.length; k++) {
                threads[k] = new Thread(r);
                threads[k].start();
            }

            r.run();

            for(Thread t : threads) {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            /*for(Flight f : fc) {
                for(Flight t : f.findThermals()) {
                    tc.addThermal(t.averagePos());
                }
                System.out.println(i++ + "/" + fc.size());
            }*/

            i = 5;
            while (tc.size() > 150) {
                tc.filter(i++);
            }

            //System.out.println(tc.toCUP());
            FileWriter res = new FileWriter("res.cup");
            res.write(tc.toCUP());
            res.flush();
            res.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
