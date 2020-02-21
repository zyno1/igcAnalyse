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


    private static void loadOptions(String[] arg) {
        for(int i = 0; i < arg.length; i++) {
            String tmp = arg[i];

            if(tmp.equals("-h") || tmp.equals("--help")) {
                StringBuilder str = new StringBuilder();

                str.append("-h\n");
                str.append("--help             display help screen\n");
                str.append("--threads <n>      set the number of threads to use\n");
                str.append("-i <s>             set the path that contains the flight logs\n");
                str.append("--max-count <n>    set the maximum number of thermals to keep\n");
                str.append("--min <n>          set the minimum amount of times a thermal has been found to be kept\n");

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
        ArrayList<String> igcPaths = findAllFiles(FOLDER);


        if(igcPaths.size() == 0) {
            System.err.println("Could find any igc files");
            System.exit(1);
        }

        ThermalCollection tc = new ThermalCollection();

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

        tc.mergeExisting();

        int i = 1;
        if(i < MIN) {
            i = MIN;
            tc.filter(i);
        }

        while (tc.size() > MAX_COUNT && MAX_COUNT != -1) {
            tc.filter(i++);
        }

        ThermalCollectionDAO dao = new ThermalCollectionCUP();
        try {
            dao.save(tc, "res.cup");
        } catch (IOException e) {
            e.printStackTrace();
        }

        dao = new ThermalCollectionKML();
        try {
            dao.save(tc, "res.kml");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("\nDone, result written to res.cup");

        System.out.println("Thermals count: " + tc.size());
        System.out.println("min: " + tc.getMin());
        System.out.println("max: " + tc.getMax());
    }

    public static void main(String[] arg) {
        loadOptions(arg);

        //long time = System.currentTimeMillis();

        operate();

        //time = System.currentTimeMillis() - time;
        //System.out.println(time / 60000.0);
    }
}
