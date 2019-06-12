package view;

import lib.igc.FlightCollection;

import javax.swing.*;

public class MainFrame extends JFrame {
    public MainFrame(FlightCollection fc) {
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        add(new DrawPanel(fc));
        pack();
    }
}
