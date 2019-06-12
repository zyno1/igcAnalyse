package view;

import lib.igc.Flight;
import lib.igc.FlightCollection;
import lib.igc.Point;

import javax.swing.*;
import java.awt.*;

public class DrawPanel extends JPanel {
    private FlightCollection fc;

    public DrawPanel(FlightCollection fc) {
        this.fc = fc;

        //setSize(900,900);
        setPreferredSize(new Dimension(900,900));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.WHITE);
        g.fillRect(0,0, getWidth(), getHeight());

        int cx = 50;
        int cy = 50;

        int coeff = Math.min(getWidth() - cx - 50, getHeight() - cy - 50);

        int z = 0;
        int toFind = 3;

        Color[] c = {Color.BLACK, Color.GREEN, Color.RED, Color.BLUE, Color.GRAY, Color.CYAN, Color.MAGENTA, Color.ORANGE, Color.PINK};
        int current = 0;

        Point old = null;
        FlightCollection tmp = new FlightCollection(fc);
        tmp.standardize();
        for(Flight f : tmp) {
            g.setColor(c[current]);
            current++;
            current %= c.length;
            for (Point p : f) {
                if (old != null) {
                    g.drawLine((int) (p.getX() * coeff + cx), getHeight() - (int) (p.getY() * coeff + cy), (int) (old.getX() * coeff + cx), getHeight() - (int) (old.getY() * coeff + cy));
                }
                old = p;
            }
        }
    }
}
