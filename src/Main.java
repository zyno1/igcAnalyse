import lib.dao.thermals.ThermalCollectionCUP;
import lib.dao.thermals.ThermalCollectionDAO;
import lib.dao.thermals.ThermalCollectionKML;
import lib.igc.Flight;
import lib.thermals.ThermalCollection;
import lib.wind.Entry;
import lib.wind.Wind;

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

    private static String WIND_DB = null;
    private static String WIND_DATE = null;

    private static String LOCATION = null;


    private static void loadOptions(String[] arg) {
        for(int i = 0; i < arg.length; i++) {
            String tmp = arg[i];

            if(tmp.equals("-h") || tmp.equals("--help")) {
                StringBuilder str = new StringBuilder();

                str.append("-h\n");
                str.append("--help             display help screen\n");
                str.append("--threads <n>      set the number of threads to use\n");
                str.append("-i <s>             set the path that contains the flight logs\n");
                str.append("--location <s>     set the location to use\n");
                str.append("--max-count <n>    set the maximum number of thermals to keep\n");
                str.append("--min <n>          set the minimum amount of times a thermal has been found to be kept\n");
                str.append("--wind-db <s>      set the path to the wind database\n");
                str.append("--wind-date <s>     search all the dates in the database that are similar to this one\n");

                System.out.println(str.toString());
                System.exit(0);
            }
            else if(tmp.equals("--wind-db")) {
                WIND_DB = arg[++i];
            }
            else if(tmp.equals("--wind-date")) {
                WIND_DATE = arg[++i];
            }
            else if(tmp.equals("--location")) {
                LOCATION = arg[++i];
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

    private static ArrayList<String> findMatchingOnly(String path, String location, String[] dates) {
        ArrayList<String> res = new ArrayList<>();

        for(String d : dates) {
            String tmp = path + "/" + location + "/" + d + "/";
            //System.out.println(path);

            File folder = new File(tmp);
            File[] listOfFiles = folder.listFiles();

            if(listOfFiles != null) {
                for (File file : listOfFiles) {
                    if (file.isFile()) {
                        //System.out.println(file.getAbsolutePath());
                        //fc.addFlight(new Flight(file.getAbsolutePath()));
                        res.add(file.getAbsolutePath());
                    }
                }
            }
            else {
                System.out.println(path + " : not found");
            }
        }

        return res;
    }

    private static void operate() {
        ArrayList<String> igcPaths = null;

        if(WIND_DB != null && WIND_DATE != null) {
            try {
                Wind w = new Wind(WIND_DB);
                w.matchLocation(LOCATION);

                Entry[] date = w.get(LOCATION, WIND_DATE);

                if(date.length != 0) {
                    for(Entry tmp : date) {
                        w.matchDirection(tmp.getAltitude(), tmp.getDirection(), 5);
                        w.matchSpeed(tmp.getAltitude(), tmp.getSpeed(), 20);
                    }
                }

                igcPaths = findMatchingOnly(FOLDER, LOCATION, w.getDates());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            igcPaths = findAllFiles(FOLDER);
        }

        if(igcPaths == null) {
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
        operate();
    }
}
