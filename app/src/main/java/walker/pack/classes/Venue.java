package walker.pack.classes;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Olebogeng Malope on 7/17/2017.
 */

public class Venue implements Serializable {
    private String Door_ID, Floor_Number, Building_Number; //pk
    private String Type;
    private String[] Alternative_Doors;
    private Double Latitude, Longitude;

    public Venue(String door_ID, String floor_Number, String building_Number, String type, String alternative_Doors, Double latitude, Double longitude) {
        Door_ID = door_ID;
        Floor_Number = floor_Number;
        Building_Number = building_Number;
        Type = type;
        Alternative_Doors = alternative_Doors.split(",");
        Latitude = latitude;
        Longitude = longitude;
    }

    public String getDoor_ID() {
        return Door_ID;
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

    public void setDoor_ID(String door_ID) {
        Door_ID = door_ID;
    }

    public String getFloor_Number() {
        return Floor_Number;
    }

    public void setFloor_Number(String floor_Number) {
        Floor_Number = floor_Number;
    }

    public String getBuilding_Number() {
        return Building_Number;
    }

    public void setBuilding_Number(String building_Number) {
        Building_Number = building_Number;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getAlternative_Doors_String() {

        String alt = "";
        if (Alternative_Doors.length == 1)
            return Alternative_Doors[0];
        else if (Alternative_Doors.length > 1) {
            alt = Alternative_Doors[0];
            for (int x = 1; x < Alternative_Doors.length; x++)
                alt = alt.concat(","+Alternative_Doors[x]);
        }
        return alt;
    }

    public String[] getAlternative_Doors() {
        return Alternative_Doors;
    }

    public void setAlternative_Doors(String[] alternative_Doors) {
        Alternative_Doors = alternative_Doors;
    }

    public String getBuildingFloorDoorID()
    {
        return (Building_Number+"_"+Floor_Number+"_"+Door_ID);
    }
}
