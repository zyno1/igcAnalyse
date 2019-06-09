package view;

import lib.igc.Flight;
import lib.igc.Point;

import javax.swing.*;
import java.awt.*;

public class DrawPanel extends JPanel {
    private Flight flight;

    public DrawPanel(Flight flight) {
        this.flight = flight;

        //setSize(900,900);
        setPreferredSize(new Dimension(900,900));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.WHITE);
        g.fillRect(0,0, getWidth(), getHeight());

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;

        cx = cy = 50;

        int coeff = Math.min(getWidth() - cx - 50, getHeight() - cy - 50);

        int z = 0;
        int toFind = 10;

        Point old = null;
        for(Point p : flight.getDrawable()) {
            g.setColor(Color.BLACK);
            if(old != null) {
                if(old.getAlt() < p.getAlt()) {
                    if(z == toFind) {
                        g.setColor(Color.GREEN);
                    }
                    else {
                        z += toFind / Math.abs(toFind);
                    }
                }
                else if(old.getAlt() > p.getAlt()) {
                    if(z == -toFind) {
                        g.setColor(Color.RED);
                    }
                    else {
                        z -= toFind / Math.abs(toFind);
                    }
                }
                g.drawLine((int)(p.getX() * coeff + cx), getHeight() - (int)(p.getY() * coeff + cy), (int)(old.getX() * coeff + cx), getHeight() - (int)(old.getY() * coeff + cy));
            }
            else {
                g.drawLine((int)(p.getX() * coeff + cx), getHeight() - (int)(p.getY() * coeff + cy), (int)(p.getX() * coeff + cx), getHeight() - (int)(p.getY() * coeff + cy));
            }
            old = p;
        }
    }
}
