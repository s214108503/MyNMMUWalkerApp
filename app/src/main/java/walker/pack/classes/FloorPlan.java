package walker.pack.classes;

/**
 * Created by Olebogeng Malope on 7/17/2017.
 */

public class FloorPlan {
    private String Floor_Level; //pk
    private String Building_Number; //pk,fk

    private String URL_To_Plan;

    public FloorPlan(String floor_Level, String building_Number, String URL_To_Plan) {
        Floor_Level = floor_Level;
        Building_Number = building_Number;
        this.URL_To_Plan = URL_To_Plan;
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

    public String getURL_To_Plan() {
        return URL_To_Plan;
    }

    public void setURL_To_Plan(String URL_To_Plan) {
        this.URL_To_Plan = URL_To_Plan;
    }
}
