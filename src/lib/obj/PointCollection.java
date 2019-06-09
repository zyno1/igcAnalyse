package lib.obj;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class PointCollection {
    private ArrayList<Point> points;
    private ArrayList<int[]> surfaces;

    public PointCollection() {
        points = new ArrayList<>();
        surfaces = new ArrayList<>();
    }

    public int addPoint(Point p) {
        if(points.contains(p)) {
            return points.indexOf(p);
        }
        points.add(p);
        return points.size() - 1;
    }

    public int addPoint(float x, float y, float z) {
        Point p = new Point(x, y, z);
        return addPoint(p);
    }

    public void addSurface(int... s) {
        surfaces.add(s);
    }

    public void addSurface(Point... p) {
        int[] s = new int[p.length];

        for(int i = 0; i < p.length; i++) {
            s[i] = addPoint(p[i]);
        }

        addSurface(s);
    }

    public void writeToFile(String name) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(name));

        for(Point p : points) {
            bw.write("v ");
            bw.write(p.getX() + " ");
            bw.write(p.getY() + " ");
            bw.write(p.getZ() + "\n");
        }

        for(int[] s : surfaces) {
            bw.write("f ");
            for(int i = 0; i < s.length; i++) {
                if(i < s.length - 1) {
                    bw.write((s[i] + 1) + " ");
                }
                else {
                    bw.write((s[i] + 1) + "\n");
                }
            }
        }

        bw.flush();
        bw.close();
    }

    public static void main(String[] args) {
        PointCollection pc = new PointCollection();

        pc.addSurface(new Point(0, 0, 0), new Point(0, 0, 1), new Point(0, 1, 1), new Point(0, 1, 0));
        pc.addSurface(new Point(0, 0, 0), new Point(0, 0, 1), new Point(1, 1, 1), new Point(1, 1, 0));

        try {
            pc.writeToFile("data.obj");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
