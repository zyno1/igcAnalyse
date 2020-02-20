package lib.wind;

public class Entry {
    private String date;
    private String location;
    private int altitude;
    private float speed;
    private float direction;

    public Entry(String line) {
        String[] split = line.split(";");

        date = split[0];
        location = split[1];
        altitude = Integer.parseInt(split[2]);
        speed = Float.parseFloat(split[3]);
        direction = Float.parseFloat(split[4]);
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public int getAltitude() {
        return altitude;
    }

    public float getSpeed() {
        return speed;
    }

    public float getDirection() {
        return direction;
    }
}
