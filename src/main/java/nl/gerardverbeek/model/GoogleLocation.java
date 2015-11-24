package nl.gerardverbeek.model;

/**
 * Created by gerardverbeek on 05/11/15.
 */
public class GoogleLocation extends ObjectToSend{
    String time;
    String longitude;
    String latitude;

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTime() {

        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
