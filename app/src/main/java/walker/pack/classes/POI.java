package walker.pack.classes;

import java.io.Serializable;

/**
 * Created by Olebogeng Malope on 7/17/2017.
 */

public class POI implements Serializable {
    private String POI_ID; //pk
    private String Door_ID, Floor_Level, Building_Number; // fk1
    private String QR_ID; // fk2

    private String Type, Description;

    public POI(String POI_ID, String door_ID, String floor_Level, String building_Number, String QR_ID, String type, String description) {
        this.POI_ID = POI_ID;
        Door_ID = door_ID;
        Floor_Level = floor_Level;
        Building_Number = building_Number;
        this.QR_ID = QR_ID;
        Type = type;
        Description = description;
    }

    public String getPOI_ID() {
        return POI_ID;
    }

    public void setPOI_ID(String POI_ID) {
        this.POI_ID = POI_ID;
    }

    public String getDoor_ID() {
        return Door_ID;
    }

    public void setDoor_ID(String door_ID) {
        Door_ID = door_ID;
    }

    public String getFloor_Level() {
        return Floor_Level;
    }

    public void setFloor_Level(String floor_Level) {
        Floor_Level = floor_Level;
    }

    public String getBuilding_Number() {
        return Building_Number;
    }

    public void setBuilding_Number(String building_Number) {
        Building_Number = building_Number;
    }

    public String getQR_ID() {
        return QR_ID;
    }

    public void setQR_ID(String QR_ID) {
        this.QR_ID = QR_ID;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
