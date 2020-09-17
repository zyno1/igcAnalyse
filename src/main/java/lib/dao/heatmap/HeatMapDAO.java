package lib.dao.heatmap;

import lib.heatmap.HeatMap;

import java.io.IOException;

public interface HeatMapDAO {
    HeatMap load(String path) throws IOException, ClassNotFoundException;
    void save(HeatMap c, String path) throws IOException;
}
