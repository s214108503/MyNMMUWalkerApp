package walker.pack;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.PriorityQueue;

import TurtlePackage.PlanNode;
import walker.pack.classes.AStarAlgorithm;
import walker.pack.classes.Cell;
import walker.pack.comparators.CellComparator;


public class TripSetupActivity extends AppCompatActivity {
    public static final int SET_CURRENT_LOCATION_REQUEST = 1;
    public static final int SET_DESTINATION_LOCATION_REQUEST = 2;
    public static final int SET_EXTERNAL_STORAGE_REQUEST = 3;

    public static String[] PERMISSIONS_STORAGE = {android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public Cell[][] grid;

    public static PlanNode STARTING_NODE;
    public static PlanNode DESTINATION_NODE;

    public PlanNode[][] start_floor_plan_nodes, end_floor_plan_nodes;

    public static ArrayList<Cell> start_path, end_path;

    public static int MAIN_W, MAIN_H, CELL_SIZE, X_MAX, Y_MAX;

    private static String start_id, end_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_setup);

        Button btn_unknown_current  = (Button) findViewById(R.id.btn_unknown_current);
        Button btn_unknown_destination = (Button) findViewById(R.id.btn_unknown_destination);

        btn_unknown_current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pick_current_location_intent = new Intent(getBaseContext(), DirectoryActivity.class);
                pick_current_location_intent.putExtra("request_code", SET_CURRENT_LOCATION_REQUEST);
                startActivityForResult(pick_current_location_intent, SET_CURRENT_LOCATION_REQUEST);
            }
        });

        btn_unknown_destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pick_destination_location_intent = new Intent(getBaseContext(), DirectoryActivity.class);
                pick_destination_location_intent.putExtra("request_code", SET_DESTINATION_LOCATION_REQUEST);
                startActivityForResult(pick_destination_location_intent, SET_DESTINATION_LOCATION_REQUEST);
            }
        });
        verifyStoragePermissions(this);

        start_path = new ArrayList<>();
        end_path = new ArrayList<>();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // TODO load floor plans once only (at the end) and display a progress bar

        // Check which request we're responding to
        if (requestCode == SET_CURRENT_LOCATION_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                String data_model = data.getStringExtra("data_model");
                start_id = data.getStringExtra("current_location");
                switch (data_model){
                    case "staff":
                    case "venue":
                        objectRetrievalMethod(true, start_id.substring(0, start_id.lastIndexOf("_")));
                        STARTING_NODE = findNode(start_id, start_floor_plan_nodes);
                        break;
                    case "poi":
                        break;
                    default:
                        Toast.makeText(this, "Invalid location data passed", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        } else if (requestCode == SET_DESTINATION_LOCATION_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                String data_model = data.getStringExtra("data_model");
                String id = data.getStringExtra("destination_location");
                assert STARTING_NODE != null;
                switch (data_model){
                    case "staff":
                    case "venue":
                        // Check if nodes are in same building
                        if (STARTING_NODE.getNode_ID().split("_")[0].equals(id.split("_")[0])){
                            // Check if nodes are on same floor level
                            if (STARTING_NODE.getNode_ID().split("_")[1].equals(id.split("_")[1])){
                                // There is no need to load floor plan for destination
                                DESTINATION_NODE = findNode(id, start_floor_plan_nodes);
                                getDirections2(STARTING_NODE, DESTINATION_NODE, start_floor_plan_nodes, start_path);
                                Intent intent = new Intent(getBaseContext(), IndoorMapActivity.class);
                                intent.putExtra("end_floor", false);
                                intent.putExtra("plan_id", id);
                                startActivity(intent);
                                //finish();
                            } else {
                                // Load floor plan for destination
                                objectRetrievalMethod(false, id.substring(0, id.lastIndexOf("_")));
                                // Find node on destination node floor level plan
                                DESTINATION_NODE = findNode(id, end_floor_plan_nodes);
                                // Get directions from starting node to stairs
                                getDirections2(STARTING_NODE, findNearestStairs(start_floor_plan_nodes), start_floor_plan_nodes, start_path);
                                // Get directions from stairs to destination
                                getDirections2(findNearestStairs(end_floor_plan_nodes), DESTINATION_NODE, end_floor_plan_nodes, end_path);
                                Intent intent = new Intent(getBaseContext(), IndoorMapActivity.class);
                                intent.putExtra("end_floor", true);
                                intent.putExtra("plan_id", id);
                                startActivity(intent);
                                //finish();
                            }
                        }
                        break;
                    case "poi":
                        break;
                    default:
                        Toast.makeText(this, "Invalid location data passed", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        } else if (resultCode == RESULT_CANCELED){
            STARTING_NODE = null;
            DESTINATION_NODE = null;
            start_path.clear();
            end_path.clear();
        }

    }

    public void verifyStoragePermissions(Activity activity){
        int permission = ActivityCompat.checkSelfPermission(activity,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,
                    PERMISSIONS_STORAGE, SET_EXTERNAL_STORAGE_REQUEST);
        }
    }

    public void GetDirections(){

        grid = new Cell[X_MAX][Y_MAX];

        AStarAlgorithm a_star = new AStarAlgorithm(grid);

        a_star.open = new PriorityQueue<>(X_MAX*Y_MAX,new CellComparator());
        a_star.closed = new boolean[X_MAX][Y_MAX];

        a_star.setStartingCell(STARTING_NODE);
        a_star.setEndingCell(DESTINATION_NODE);

        for(int x = 0; x < X_MAX; x++) {
            for (int y = 0; y < Y_MAX; y++) {

                a_star.grid[x][y] = new Cell(x, y);
                a_star.grid[x][y].setH_cost(Math.abs(x - DESTINATION_NODE.getX())+Math.abs(y - DESTINATION_NODE.getY()));

                PlanNode cur = start_floor_plan_nodes[x][y];
                if(cur.getNode_Type().equals("BLOCKED"))
                    a_star.setCellAsBlocked(cur); // set blocked cells to null
            }
        }

        a_star.grid[a_star.start_node.getX()][a_star.start_node.getY()].setFinal_cost(0);

        a_star.runAStar();

        start_path = new ArrayList<>();

        if(a_star.closed[a_star.end_node.getX()][a_star.end_node.getY()]){
            // trace back the path
            Cell cur = a_star.grid[a_star.end_node.getX()][a_star.end_node.getY()];
            start_path.add(cur);
            while(cur.getAncestor() != null){
                cur = cur.getAncestor();
                start_path.add(cur);
            }
            System.out.println();
        }else System.out.println("No possible start_path");

        // nullifies variables of a star class
        a_star.clear();
    }

    // caters for nodes to STAIRS
    public void getDirections2(PlanNode start, PlanNode end, PlanNode[][] plan_to_Search, ArrayList<Cell> path_to_populate){

        grid = new Cell[X_MAX][Y_MAX];

        AStarAlgorithm a_star = new AStarAlgorithm(grid);

        a_star.open = new PriorityQueue<>(X_MAX*Y_MAX,new CellComparator());
        a_star.closed = new boolean[X_MAX][Y_MAX];

        a_star.setStartingCell(start);
        a_star.setEndingCell(end);

        for(int x = 0; x < X_MAX; x++) {
            for (int y = 0; y < Y_MAX; y++) {
                a_star.grid[x][y] = new Cell(x, y);
                a_star.grid[x][y].setH_cost(Math.abs(x - end.getX())+Math.abs(y - end.getY()));

                PlanNode cur = plan_to_Search[x][y];
                if(cur.getNode_Type().equals("BLOCKED"))
                    a_star.setCellAsBlocked(cur); // set blocked cells to null
            }
        }

        a_star.grid[a_star.start_node.getX()][a_star.start_node.getY()].setFinal_cost(0);

        a_star.runAStar();

        if(a_star.closed[a_star.end_node.getX()][a_star.end_node.getY()]){
            // trace back the path
            Cell cur = a_star.grid[a_star.end_node.getX()][a_star.end_node.getY()];
            path_to_populate.add(cur);
            while(cur.getAncestor() != null){
                cur = cur.getAncestor();
                path_to_populate.add(cur);
            }
        } else
            Toast.makeText(this, "No possible path", Toast.LENGTH_SHORT).show();

        // nullifies variables of a star class
        a_star.clear();
    }


    private PlanNode findNode(String node_id, PlanNode[][] plan_to_Search){
        PlanNode cur;
        for(int x = 0; x < X_MAX; x++)
            for(int y = 0; y < Y_MAX; y++) {
                cur = plan_to_Search[x][y];
                if (!cur.getNode_Type().equals("BLOCKED") && !cur.getNode_Type().equals("WALKABLE"))
                    if (cur.getNode_ID().equals(node_id))
                        return cur;
            }
        return null;
    }

    // Given a floor plan, searches for stairs for user to change floor levels
    private PlanNode findNearestStairs(PlanNode[][] plan_to_Search){
        PlanNode cur;
            for (int x = 0; x < X_MAX; x++) {
                for (int y = 0; y < Y_MAX; y++) {
                    cur = plan_to_Search[x][y];
                    if (cur.getNode_Type().equals("STAIRS"))
                        return cur;
                }
            }

        return null;
    }

    private void objectRetrievalMethod(boolean isStart, String floor_plan_name) {
        String file = "/storage/emulated/0/NMMUWalker/b"+floor_plan_name+".dat";
        try {
            ObjectInputStream input = new ObjectInputStream(new FileInputStream(file));
            if (isStart) {
                MAIN_W = input.readInt();
                MAIN_H = input.readInt();
                CELL_SIZE = input.readInt();
                start_floor_plan_nodes = (PlanNode[][]) input.readUnshared();

                X_MAX = MAIN_W/CELL_SIZE;
                Y_MAX = MAIN_H/CELL_SIZE;
            }
            else {
                MAIN_W = input.readInt();
                MAIN_H = input.readInt();
                CELL_SIZE = input.readInt();
                end_floor_plan_nodes = (PlanNode[][]) input.readUnshared();
            }

        }  catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }


}
