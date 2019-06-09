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

            new MainFrame(tmp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
