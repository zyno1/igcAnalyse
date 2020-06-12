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

import lib.dao.thermals.ThermalCollectionCUP;
import lib.dao.thermals.ThermalCollectionDAO;
import lib.dao.thermals.ThermalCollectionKML;
import lib.igc.Flight;
import lib.thermals.ThermalCollection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    private static int THREAD_COUNT = 1;
    private static String FOLDER = "data/";;
    private static int MAX_COUNT = -1;
    private static int MIN = -1;
    private static String CUP_OUT = "";
    private static String CUP_IN = "";
    private static String KML_OUT = "";

    private static ThermalCollection tc;


    private static void loadOptions(String[] arg) {
        for(int i = 0; i < arg.length; i++) {
            String tmp = arg[i];

            if(tmp.equals("-h") || tmp.equals("--help")) {
                StringBuilder str = new StringBuilder();

                str.append("-h\n");
                str.append("--help             display help screen\n");
                str.append("--threads <int>    set the number of threads to use\n");
                str.append("-i <str>           set the path that contains the flight logs\n");
                str.append("--max-count <int>  set the maximum number of thermals to keep\n");
                str.append("--min <int>        set the minimum amount of times a thermal has been found to be kept\n");
                str.append("--cup <str>        set the output file for the cup file (if not set no cup file will be written\n");
                str.append("--kml <str>        set the output file for the kml file (if not set no kml file will be written\n");
                str.append("--load <str>       expects a path to a cup file containing thermals. This has to\n" +
                           "                   be a file written by this program or else it will fail\n" +
                           "                   (note: errors will not be handled well)\n" +
                           "                   So if you use this option be careful only to use non-modified cup\n" +
                           "                   files written only by this program\n");

                System.out.println(str.toString());
                System.exit(0);
            }
            else if(tmp.equals("--threads")) {
                THREAD_COUNT = Integer.parseInt(arg[++i]);
            }
            else if(tmp.equals("--max-count")) {
                MAX_COUNT = Integer.parseInt(arg[++i]);
            }
            else if(tmp.equals("--min")) {
                MIN = Integer.parseInt(arg[++i]);
            }
            else if(tmp.equals("-i")) {
                FOLDER = arg[++i];
            }
            else if(tmp.equals("--cup")) {
                CUP_OUT = arg[++i];
            }
            else if(tmp.equals("--kml")) {
                KML_OUT = arg[++i];
            }
            else if(tmp.equals("--load")) {
                CUP_IN = arg[++i];
            }
        }
    }

    private static ArrayList<String> findAllFiles(String path) {
        ArrayList<String> res = new ArrayList<>();

        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        if(listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile() && file.getAbsoluteFile().toString().contains(".igc")) {
                    res.add(file.getAbsolutePath());
                }
                else if(file.isDirectory()) {
                    res.addAll(findAllFiles(file.getAbsolutePath()));
                }
            }
        }
        else {
            System.out.println(path + " : not found");
        }

        return res;
    }

    private static void operate() {
        final long start_time = System.currentTimeMillis();

        ArrayList<String> igcPaths = findAllFiles(FOLDER);


        if(igcPaths.size() == 0) {
            System.err.println("Could find any igc files");
            System.exit(1);
        }

        if(!CUP_IN.equals("")) {
            ThermalCollectionDAO dao = new ThermalCollectionCUP();
            try {
                tc = dao.load(CUP_IN);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("error: failed to load file: " + CUP_IN);
            }
        }
        else {
            tc = new ThermalCollection();
        }

        AtomicInteger co = new AtomicInteger(0);
        Iterator<String> it = igcPaths.iterator();
        final int size = igcPaths.size();

        System.out.println("Analyzing " + size + " files:");

        Runnable r = () -> {
            String f = null;
            synchronized (it) {
                if(it.hasNext()) {
                    f = it.next();
                }
            }

            while (f != null) {
                try {
                    for(Flight t : (new Flight(f)).findThermals()) {
                        synchronized (tc) {
                            tc.addThermal(t);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("failed to load file: " + f);
                }
                System.out.print("\r                              \r" + co.incrementAndGet() + "/" + size);
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

        Thread[] threads = null;
        if(THREAD_COUNT > 1) {
            threads = new Thread[THREAD_COUNT - 1];

            for(int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(r);
                threads[i].start();
            }
        }

        r.run();

        if(threads != null) {
            for (Thread t : threads) {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("\nThermals found: " + tc.size());
        System.out.println("Doing some final merges");

        tc.mergeExisting();

        System.out.println("Thermals still present after merging: " + tc.size());
        System.out.println("Filtering");

        int i = 1;
        if(i < MIN) {
            i = MIN;
            tc.filter(i);
        }

        while (tc.size() > MAX_COUNT && MAX_COUNT != -1) {
            tc.filter(i++);
        }

        if(!CUP_OUT.equals("")) {
            ThermalCollectionDAO dao = new ThermalCollectionCUP();
            try {
                dao.save(tc, CUP_OUT);
                System.out.println("Done, result written to " + CUP_OUT);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error writing cup file: " + CUP_OUT);
            }
        }

        if(!KML_OUT.equals("")) {
            ThermalCollectionDAO dao = new ThermalCollectionKML();
            try {
                dao.save(tc, KML_OUT);
                System.out.println("Done, result written to " + KML_OUT);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error writing kml file: " + KML_OUT);
            }
        }

        System.out.println("Thermals found: " + tc.size());
        System.out.println("min: " + tc.getMin());
        System.out.println("max: " + tc.getMax());

        final long time = System.currentTimeMillis() - start_time;
        final long s = time / 1000 % 60;
        final long m = time / 1000 / 60 % 60;
        final long h = time / 1000 / 60 / 60;

        System.out.print("time needed: ");
        if(h != 0) {
            System.out.print(h + "h");
        }
        if(h != 0 || m != 0) {
            System.out.print(m + "m");
        }
        if(h != 0 || m != 0 || s != 0) {
            System.out.print(s + "s");
        }
    }

    public static void main(String[] arg) {
        loadOptions(arg);

        //long time = System.currentTimeMillis();

        operate();

        //time = System.currentTimeMillis() - time;
        //System.out.println(time / 60000.0);
    }
}
