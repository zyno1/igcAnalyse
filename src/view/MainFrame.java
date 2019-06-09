package view;

import lib.igc.Flight;

import javax.swing.*;

public class MainFrame extends JFrame {
    public MainFrame(Flight f) {
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        add(new DrawPanel(f));
        pack();
    }
}
