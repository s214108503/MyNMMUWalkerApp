package walker.pack.classes;

import java.io.Serializable;

/**
 * Created by Olebogeng Malope on 7/17/2017.
 */

public class QRCode implements Serializable {
    private String QR_ID; //pk
    private String Building_Number; //fk
    private String Description, Image_URL;
    private Double Latitude, Longitude;
    private Integer Floor_Level;

    public QRCode(String QR_ID, String building_Number, String description, String image_URL, Double latitude, Double longitude, int floor_Level) {
        this.QR_ID = QR_ID;
        Building_Number = building_Number;
        Description = description;
        Image_URL = image_URL;
        Latitude = latitude;
        Longitude = longitude;
        Floor_Level = floor_Level;
    }


    public String getQR_ID() {
        return QR_ID;
    }

    public void setQR_ID(String QR_ID) {
        this.QR_ID = QR_ID;
    }

    public String getBuilding_Number() {
        return Building_Number;
    }

    public void setBuilding_Number(String building_Number) {
        Building_Number = building_Number;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getImage_URL() {
        return Image_URL;
    }

    public void setImage_URL(String image_URL) {
        Image_URL = image_URL;
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

    public int getFloor_Level() {
        return Floor_Level;
    }

    public void setFloor_Level(int floor_Level) {
        Floor_Level = floor_Level;
    }

    public String determineWhereQRCodeIs(){
        if (Floor_Level != null)
            return "indoor";
        else
        if (!Building_Number.isEmpty())
            return "building";
        else
        if (Latitude != null && Longitude != null)
            return "outdoor";
        return "";
    }
}
