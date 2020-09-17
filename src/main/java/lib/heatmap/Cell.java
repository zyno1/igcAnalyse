package lib.heatmap;

public class Cell {
    private double val;
    private int nb;

    public Cell() {
        val = 0;
        nb = 0;
    }

    public synchronized void add(double v) {
        val = ((val * nb) + v) / (nb + 1);
        nb++;
    }

    public double getVal() {
        return val;
    }

    public int getNb() {
        return nb;
    }
}
