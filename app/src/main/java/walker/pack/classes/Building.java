package walker.pack.classes;

import java.io.Serializable;

/**
 * Created by Olebogeng Malope on 7/17/2017.
 */

public class Building implements Serializable {
    private String Building_Number; //pk
    private String Second_Name;
    private Double Latitude, Longitude;

    public Building(String building_Number, String second_Name, Double latitude, Double longitude) {
        Building_Number = building_Number;
        Second_Name = second_Name;
        Latitude = latitude;
        Longitude = longitude;
    }

    public String getBuilding_Number() {
        return Building_Number;
    }

    public void setBuilding_Number(String building_Number) {
        Building_Number = building_Number;
    }

    public String getSecond_Name() {
        return Second_Name;
    }

    public void setSecond_Name(String second_Name) {
        Second_Name = second_Name;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }
}
