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

import lib.dao.thermals.*;
import lib.igc.Flight;
import lib.thermals.Thermal;
import lib.thermals.ThermalCollection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Main {
    private static String FOLDER = "";
    private static int MAX_COUNT = -1;
    private static int MIN = -1;
    private static String CUP_OUT = "";
    private static String CUP_IN = "";
    private static String KML_OUT = "";

    private static String JSON_OUT = "";
    private static String JSON_IN = "";

    private static String BIN_OUT = "";
    private static String BIN_IN = "";

    private static ThermalCollection tc;


    private static void loadOptions(String[] arg) {
        for(int i = 0; i < arg.length; i++) {
            String tmp = arg[i];

            if(tmp.equals("-h") || tmp.equals("--help")) {
                StringBuilder str = new StringBuilder();

                str.append("-h\n");
                str.append("--help             display help screen\n");
                str.append("-i <str>           set the path that contains the flight logs\n");
                str.append("--max-count <int>  set the maximum number of thermals to keep\n");
                str.append("--min <int>        set the minimum amount of times a thermal has been found to be kept\n");
                str.append("--cup <str>        set the output file for the cup file (if not set no cup file will be written)\n");
                str.append("--kml <str>        set the output file for the kml file (if not set no kml file will be written)\n");
                str.append("--json <str>       set the output file for the json file (if not set no json file will be written)\n");
                str.append("--load-json <str>  set the path to load a json file\n");
                str.append("--bin <str>        set the output file for the bin file (if not set no bin file will be written)\n");
                str.append("--load-bin <str>   set the path to load a bin file\n");
                str.append("                   (the bin file format is just java serialization)\n");
                str.append("--load-cub <str>   expects a path to a cup file containing thermals. This has to\n" +
                           "                   be a file written by this program or else it will fail\n" +
                           "                   (note: errors will not be handled well)\n" +
                           "                   So if you use this option be careful only to use non-modified cup\n" +
                           "                   files written only by this program\n");

                System.out.println(str.toString());
                System.exit(0);
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
            else if(tmp.equals("--load-cub")) {
                CUP_IN = arg[++i];
            }
            else if(tmp.equals("--json")) {
                JSON_OUT = arg[++i];
            }
            else if(tmp.equals("--load-json")) {
                JSON_IN = arg[++i];
            }
            else if(tmp.equals("--bin")) {
                BIN_OUT = arg[++i];
            }
            else if(tmp.equals("--load-bin")) {
                BIN_IN = arg[++i];
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
        if(!CUP_IN.equals("")) {
            System.out.println("loading cup file");
            ThermalCollectionDAO dao = new ThermalCollectionCUP();
            try {
                tc = dao.load(CUP_IN);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                System.err.println("error: failed to load file: " + CUP_IN);
            }
        }
        else {
            tc = new ThermalCollection();
        }

        if(!JSON_IN.equals("")) {
            System.out.println("loading json file");
            ThermalCollectionDAO dao = new ThermalCollectionJSON();

            try {
                ThermalCollection tmp = dao.load(JSON_IN);
                if(tc.size() == 0) {
                    tc = tmp;
                }
                else if(tc.size() < tmp.size()) {
                    for(Thermal t : tc) {
                        tmp.addThermal(t);
                    }
                    tc = tmp;
                }
                else {
                    for(Thermal t : tmp) {
                        tc.addThermal(t);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                System.err.println("error: failed to load file: " + JSON_IN);
            }
        }

        if(!BIN_IN.equals("")) {
            System.out.println("loading bin file");
            ThermalCollectionDAO dao = new ThermanCollectionBIN();

            try {
                ThermalCollection tmp = dao.load(BIN_IN);
                if(tc.size() == 0) {
                    tc = tmp;
                }
                else if(tc.size() < tmp.size()) {
                    for(Thermal t : tc) {
                        tmp.addThermal(t);
                    }
                    tc = tmp;
                }
                else {
                    for(Thermal t : tmp) {
                        tc.addThermal(t);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                System.err.println("error: failed to load file: " + BIN_IN);
            }
        }

        if(!FOLDER.equals("")) {
            ArrayList<String> igcPaths = findAllFiles(FOLDER);

            Stream.Builder<ThermalCollection> sbtc = Stream.builder();
            sbtc.accept(tc);

            AtomicInteger co = new AtomicInteger(0);
            final int size = igcPaths.size();

            System.out.println("Analyzing " + size + " files:");

            Optional<ThermalCollection> tmp = Stream.concat(igcPaths.parallelStream().map(f -> {
                ThermalCollection res = new ThermalCollection();
                try {
                    for (Flight i : (new Flight(f)).findThermals()) {
                        res.addThermal(i);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return res;
            }), sbtc.build())
                    .reduce((c1, c2) -> {
                        for (Thermal t : c2) {
                            c1.parAddThermal(t);
                        }
                        System.out.print("\r                              \r" + co.incrementAndGet() + "/" + size);
                        return c1;
                    });

            tmp.ifPresent(thermals -> tc = thermals);
        }

        System.out.println("\nThermals found: " + tc.size());
        System.out.println("\tmin: " + tc.getMin());
        System.out.println("\tmax: " + tc.getMax());
        //System.out.println("Doing some final merges");

        /*
        technically useless because the ThermalCollection::addThermal function has been
        modified to basically replace the function
         */
        //tc.mergeExisting();

        //System.out.println("Thermals still present after merging: " + tc.size());
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

        if(!JSON_OUT.equals("")) {
            ThermalCollectionDAO dao = new ThermalCollectionJSON();

            try {
                dao.save(tc, JSON_OUT);
                System.out.println("Done, result written to " + JSON_OUT);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error writing to json file: " + JSON_OUT);
            }
        }

        if(!BIN_OUT.equals("")) {
            ThermalCollectionDAO dao = new ThermanCollectionBIN();

            try {
                dao.save(tc, BIN_OUT);
                System.out.println("Done, result written to " + BIN_OUT);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error writing to bin file: " + BIN_OUT);
            }
        }

        System.out.println("Thermals found: " + tc.size());
        System.out.println("\tmin: " + tc.getMin());
        System.out.println("\tmax: " + tc.getMax());
    }

    public static void main(String[] arg) {
        loadOptions(arg);

        final long start_time = System.currentTimeMillis();

        operate();

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
        System.out.print(s + "s\n");
    }
}
