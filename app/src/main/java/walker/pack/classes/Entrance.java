package walker.pack.classes;

/**
 * Created by s214108503 on 2017/09/10.
 */

public class Entrance {
    public Double X, Y, Latitude, Longitude;
    public Integer Building, Floor;
    public String Door;
    public Entrance(Integer building, Integer floor, String door, Double x, Double y){
        this.Building = building;
        this.Floor = floor;
        this.Door = door;
        this.X = x;
        this.Y = y;
    }
    public Entrance(Integer building, Integer floor, String door, Double x, Double y, Double latitude, Double longitude){
        this.Building = building;
        this.Floor = floor;
        this.Door = door;
        this.X = x;
        this.Y = y;
        this.Latitude = latitude;
        this.Longitude = longitude;
    }
}
