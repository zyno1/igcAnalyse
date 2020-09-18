package lib.dao.heatmap;

import lib.heatmap.HeatMap;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class HeatMapJPG implements HeatMapDAO {
    private static final double MIN_VAL = -5;
    private static final double MAX_VAL = 5;

    @Override
    public HeatMap load(String path) throws IOException, ClassNotFoundException {
        return null;
    }

    @Override
    public void save(HeatMap c, String path) throws IOException {
        BufferedImage img = new BufferedImage(c.getW(), c.getH(), BufferedImage.TYPE_INT_ARGB);

        for(int j = 0; j < c.getH(); j++) {
            for(int i = 0; i < c.getW(); i++) {
                if(c.get(i, j) == null) {
                    img.setRGB(i, j, 0);
                    continue;
                }

                int nb = c.get(i, j).getNb();

                if(nb < 10) {
                    img.setRGB(i, j, 0);
                }
                else {
                    double v = c.get(i, j).getVal();
                    v = Math.max(MIN_VAL, Math.min(MAX_VAL, v));

                    int argb = 255 << 24;

                    if (v > 0.5) {
                        argb = argb | ((int) (v * 255 / MAX_VAL) << 8);
                        img.setRGB(i, j, argb);
                    } else {
                        //argb = argb | (255);
                        //argb = argb | ((int) (Math.abs(v) * 255 / MIN_VAL) << 16);
                        img.setRGB(i, j, 0);
                    }
                }
            }
        }

        File out = new File(path);
        ImageIO.write(img, "png", out);
    }
}
