package lib.dao.thermals;

import lib.thermals.Thermal;
import lib.thermals.ThermalCollection;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ThermalCollectionJSON implements ThermalCollectionDAO {
    @Override
    public ThermalCollection load(String path) throws IOException {
        String text = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);

        JSONObject obj = new JSONObject(text);

        JSONArray list = (JSONArray)obj.get("thermals");

        ThermalCollection tc = new ThermalCollection();

        for (Object o : list) {
            JSONObject tmp = (JSONObject) o;

            Thermal t = Thermal.fromJSON(tmp);
            tc.addThermal(t);
        }

        return tc;
    }

    @Override
    public void save(ThermalCollection tc, String path) throws IOException {
        JSONObject res = new JSONObject();
        JSONArray list = new JSONArray();

        for (Thermal t : tc) {
            list.put(t.toJSON());
        }

        res.put("thermals", list);

        FileWriter out = new FileWriter(path);
        out.write(res.toString());
        out.flush();
        out.close();
    }
}
