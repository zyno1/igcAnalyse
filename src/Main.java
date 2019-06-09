import lib.igc.Flight;
import view.MainFrame;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] argc) {
        try {
            Flight tmp = new Flight("data/NetCoupe2019_8606.igc");
            //Flight tmp = new Flight("data/NetCoupe2019_8643.igc");
            System.out.println(tmp.getDrawable());

            //System.out.println(tmp.standardize().getMin());

            //FileWriter res = new FileWriter(new File("res.csv"));
            //res.write(tmp.toCSV());
            //res.flush();
            //res.close();

            //System.out.println(tmp.averagePos());
            //System.out.println(tmp.standardDistribution());

            new MainFrame(tmp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
