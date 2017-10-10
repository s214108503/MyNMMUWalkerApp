package walker.pack;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mapzen.tangram.LngLat;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.TimerTask;

import TurtlePackage.PlanNode;
import walker.pack.classes.AStarAlgorithm;
import walker.pack.classes.Building;
import walker.pack.classes.Cell;
import walker.pack.classes.Entrance;
import walker.pack.classes.POI;
import walker.pack.classes.QRCode;
import walker.pack.comparators.CellComparator;
import walker.pack.interfaces.CancelTripListener;


public class TripSetupActivity extends AppCompatActivity implements CancelTripListener {

    @Override
    public void onCancelTripListener() {
        recreate();
    }

    public interface onCurrentLocationUpdate {
        void onCurrentLocationUpdate();
    }

    static onCurrentLocationUpdate current_location_listener;
    static CancelTripListener tripListener;

    public static final int SET_CURRENT_LOCATION_REQUEST = 1;
    public static final int SET_DESTINATION_LOCATION_REQUEST = 2;
    public static final int SET_EXTERNAL_STORAGE_REQUEST = 3;
    public static final int START_INDOOR_ACTIVITY = 4;
    public static final int START_OUTDOOR_ACTIVITY = 5;

    public static String[] PERMISSIONS_STORAGE = {android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static Cell[][] grid;
    Context context;
    // Indoor location
    public static PlanNode STARTING_NODE;
    public static PlanNode DESTINATION_NODE;
    public static PlanNode stairs1, stairs2;
    // Building location
    private static Building STARTING_BUILDING;
    private static Building DESTINATION_BUILDING;
    // Outdoor location
    private static LngLat STARTING_OUTDOOR_LOCATION;
    private static LngLat DESTINATION_OUTDOOR_LOCATION;
    // Floor plan
    public static PlanNode[][] start_floor_plan_nodes, end_floor_plan_nodes;
    // Path storage
    private static ArrayList<Cell> start_path, end_path;
    // Screen sizes
    private static int MAIN_W, MAIN_H, CELL_SIZE, X_MAX, Y_MAX;
    // local things
    public static String start_id, end_id;
    private static String start_model, end_model;
    // Views
    static Button btn_unknown_current, btn_unknown_destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_setup);

        btn_unknown_current = (Button) findViewById(R.id.btn_unknown_current);
        btn_unknown_destination = (Button) findViewById(R.id.btn_unknown_destination);
        TripSetupActivity.btn_unknown_destination.setEnabled(false);

        btn_unknown_current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pick_current_location_intent = new Intent(getBaseContext(), DirectoryActivity.class);
                pick_current_location_intent.putExtra("request_code", SET_CURRENT_LOCATION_REQUEST);
                startActivityForResult(pick_current_location_intent, SET_CURRENT_LOCATION_REQUEST);
                TripSetupActivity.btn_unknown_destination.setEnabled(true);
            }
        });

        btn_unknown_destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btn_unknown_current.getTextColors().getDefaultColor() == Color.GREEN) {
                    Intent pick_destination_location_intent = new Intent(getBaseContext(), DirectoryActivity.class);
                    pick_destination_location_intent.putExtra("request_code", SET_DESTINATION_LOCATION_REQUEST);
                    startActivityForResult(pick_destination_location_intent, SET_DESTINATION_LOCATION_REQUEST);
                } else {
                    Toast.makeText(context, "Specify current location first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        verifyStoragePermissions(this);
        start_path = new ArrayList<>();
        end_path = new ArrayList<>();

        current_location_listener = new onCurrentLocationUpdate() {
            @Override
            public void onCurrentLocationUpdate() {
                final Thread t1 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        start_floor_plan_nodes = null;
                        Log.e("TripAct", "Starting thread 1");
                        PrepDepartureMethod();
                    }
                });
                t1.start();
                final Thread t2 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("TripAct", "Starting thread 2");
                        end_floor_plan_nodes = null;
                        try {
                            Thread.sleep(6000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            PrepDestinationMethod();
                        }

                    }
                });
                try {
                    t1.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                t2.start();


            }
        };
        tripListener = new TripSetupActivity();
        context = getBaseContext();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // TODO cater for loading POI plans/maps
        // Check which request we're responding to
        if (requestCode == SET_CURRENT_LOCATION_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                start_model = data.getStringExtra("data_model");
                start_id = data.getStringExtra("current_location");

                //PrepDepartureMethod();
                btn_unknown_current.setTextColor(Color.GREEN);
            }
        } else if (requestCode == SET_DESTINATION_LOCATION_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                end_model = data.getStringExtra("data_model");
                end_id = data.getStringExtra("destination_location");
                //assert STARTING_NODE != null;
                //PrepDestinationMethod();
                new AlertDialog.Builder(TripSetupActivity.this)
                        .setTitle("Confirmation")
                        .setMessage("Current location:\t"+start_id+"\nDestination location:\t"+end_id)
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                btn_unknown_current.setTextColor(Color.WHITE);
                                btn_unknown_destination.setTextColor(Color.WHITE);
                                TripSetupActivity.btn_unknown_destination.setEnabled(false);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                btn_unknown_current.setTextColor(Color.WHITE);
                                btn_unknown_destination.setTextColor(Color.WHITE);
                                TripSetupActivity.btn_unknown_destination.setEnabled(false);
                            }
                        })
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                PrepDepartureMethod();
                                current_location_listener.onCurrentLocationUpdate();
                            }
                        })
                        .create()
                        .show();
                btn_unknown_destination.setTextColor(Color.GREEN);
            }
        } else if (requestCode == START_INDOOR_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                btn_unknown_current.setTextColor(Color.WHITE);
                btn_unknown_destination.setTextColor(Color.WHITE);
                current_location_listener.onCurrentLocationUpdate();
            } else {
                btn_unknown_current.setTextColor(Color.WHITE);
                btn_unknown_destination.setTextColor(Color.WHITE);
                Toast.makeText(context, "Indoor map result: Canceled", Toast.LENGTH_SHORT).show();
                TripSetupActivity.btn_unknown_destination.setEnabled(false);
            }
        } else if (requestCode == START_OUTDOOR_ACTIVITY){
            if (resultCode == RESULT_OK) {
                btn_unknown_current.setTextColor(Color.WHITE);
                btn_unknown_destination.setTextColor(Color.WHITE);
                current_location_listener.onCurrentLocationUpdate();
            } else {
                btn_unknown_current.setTextColor(Color.WHITE);
                btn_unknown_destination.setTextColor(Color.WHITE);
                Toast.makeText(context, "Outdoor result: Canceled", Toast.LENGTH_SHORT).show();
                TripSetupActivity.btn_unknown_destination.setEnabled(false);
            }
        }
        else if (resultCode == RESULT_CANCELED) {
            STARTING_NODE = null;
            DESTINATION_NODE = null;
            start_path.clear();
            end_path.clear();
        }
    }

    public AsyncTask<Void, Integer, Boolean> PrepDepartureMethod() {
        switch (start_model) {
            case "staff":
            case "venue":
                return new AsyncTask<Void, Integer, Boolean>() {
                    ProgressDialog progress;

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress = ProgressDialog.show(TripSetupActivity.this, "Loading Map",
                                        "This will take a few seconds", true);
                            }
                        });

                    }

                    @Override
                    protected Boolean doInBackground(Void... voids) {
                        try {
                            objectRetrievalMethod(true, start_id.split("_")[0]+"_"+start_id.split("_")[1]);
                            STARTING_NODE = findNode(start_id, start_floor_plan_nodes);
                        } catch (ClassNotFoundException | IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                        return true;
                    }

                    @Override
                    protected void onPostExecute(Boolean floor_retrieved) {
                        super.onPostExecute(floor_retrieved);
                        progress.dismiss();
                    }
                }.execute();
            case "poi":
                // Determine if POI is building, venue or qr code
                final POI temp_poi = HomeActivity.db.getPOI(start_id);
                switch (temp_poi.determinePOIKind()) {
                    case "venue":
                        return new AsyncTask<Void, Integer, Boolean>() {
                            ProgressDialog progress;

                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progress = ProgressDialog.show(TripSetupActivity.this, "Loading Map",
                                                "This will take a few seconds", true);
                                    }
                                });

                            }

                            @Override
                            protected Boolean doInBackground(Void... voids) {
                                try {
                                    objectRetrievalMethod(true, start_id.split("_")[0]+"_"+start_id.split("_")[1]);
                                    STARTING_NODE = findNode(start_id, start_floor_plan_nodes);
                                } catch (ClassNotFoundException | IOException e) {
                                    e.printStackTrace();
                                    return null;
                                }
                                return true;
                            }

                            @Override
                            protected void onPostExecute(Boolean floor_retrieved) {
                                super.onPostExecute(floor_retrieved);
                                if (!floor_retrieved)
                                    Toast.makeText(context, "Floor Plan not loaded", Toast.LENGTH_SHORT).show();
                                progress.dismiss();
                            }
                        }.execute();
                    case "building":
                        return new AsyncTask<Void, Integer, Boolean>() {
                            @Override
                            protected Boolean doInBackground(Void... voids) {
                                STARTING_BUILDING = HomeActivity.db.getBuilding(temp_poi.getBuilding_Number());
                                return true;
                            }
                        }.execute();
                    case "qr_code":
                        final QRCode temp_qr = HomeActivity.db.getQRCode(temp_poi.getQR_ID());
                        switch (temp_qr.determineWhereQRCodeIs()) {
                            case "indoor":
                                return new AsyncTask<Void, Integer, Boolean>() {
                                    ProgressDialog progress;

                                    @Override
                                    protected void onPreExecute() {
                                        super.onPreExecute();
                                        progress = ProgressDialog.show(TripSetupActivity.this, "Loading Map",
                                                "This will take a few seconds", true);
                                    }

                                    @Override
                                    protected Boolean doInBackground(Void... voids) {
                                        try {
                                            objectRetrievalMethod(true, temp_qr.getBuilding_Number()+"_"+temp_qr.getFloor_Level());
                                            STARTING_NODE = start_floor_plan_nodes[temp_qr.getLatitude().intValue()][temp_qr.getLongitude().intValue()];
                                        } catch (ClassNotFoundException | IOException e) {
                                            e.printStackTrace();
                                            return null;
                                        }
                                        return true;
                                    }

                                    @Override
                                    protected void onPostExecute(Boolean floor_retrieved) {
                                        super.onPostExecute(floor_retrieved);
                                        if (!floor_retrieved)
                                            Toast.makeText(context, "Floor Plan not loaded", Toast.LENGTH_SHORT).show();
                                    }
                                }.execute();
                            case "building":
                                return new AsyncTask<Void, Integer, Boolean>() {
                                    @Override
                                    protected Boolean doInBackground(Void... voids) {
                                        STARTING_BUILDING = HomeActivity.db.getBuilding(temp_qr.getBuilding_Number());
                                        return true;
                                    }
                                }.execute();
                            case "outdoor":
                                return new AsyncTask<Void, Integer, Boolean>() {
                                    @Override
                                    protected Boolean doInBackground(Void... voids) {
                                        STARTING_OUTDOOR_LOCATION = new LngLat(temp_qr.getLongitude(), temp_qr.getLatitude());
                                        return true;
                                    }
                                }.execute();
                            default:
                                Toast.makeText(this, "POI couldn't be found", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        Toast.makeText(this, "POI couldn't be found", Toast.LENGTH_SHORT).show();
                }
                break;
            case "building":
                return new AsyncTask<Void, Integer, Boolean>() {

                    @Override
                    protected Boolean doInBackground(Void... voids) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "Model: Building", Toast.LENGTH_SHORT).show();
                            }
                        });
                        //start_id = DESTINATION_BUILDING.getBuilding_Number()+"_00";
                        return true;
                    }
                }.execute();
            default:
                // could be building
                if (STARTING_BUILDING == null)
                    Toast.makeText(this, "Invalid location data passed", Toast.LENGTH_SHORT).show();
                return new AsyncTask<Void, Integer, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... voids) {
                        return true;
                    }
                }.execute();

        }
        return null;
    }

    private AsyncTask<Void, Integer, Boolean> PrepDestinationMethod() {
        switch (end_model) {
            case "staff":
            case "venue":
                if (STARTING_NODE != null) {
                    // Check if nodes are in same building
                    if (STARTING_NODE.getNode_ID().split("_")[0].equals(end_id.split("_")[0])) {
                        // Check if nodes are on same floor level
                        if (STARTING_NODE.getNode_ID().split("_")[1].equals(end_id.split("_")[1])) {
                            // There is no need to load floor plan for destination
                            return new AsyncTask<Void, Integer, Boolean>() {
                                ProgressDialog progress;

                                @Override
                                protected void onPreExecute() {
                                    super.onPreExecute();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progress = ProgressDialog.show(TripSetupActivity.this, "Loading Map",
                                                    "This will take a few seconds", true);
                                        }
                                    });

                                }

                                @Override
                                protected Boolean doInBackground(Void... voids) {
                                    DESTINATION_NODE = findNode(end_id, start_floor_plan_nodes);
                                    getDirections2(STARTING_NODE, DESTINATION_NODE, start_floor_plan_nodes, start_path);
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Boolean aBoolean) {
                                    super.onPostExecute(aBoolean);
                                    Intent intent = new Intent(getBaseContext(), IndoorMapActivity.class);
                                    intent.putExtra("end_floor", false);
                                    intent.putExtra("start_id", start_id);
                                    intent.putExtra("end_id", end_id);
                                    progress.dismiss();
                                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); //flag
                                    startActivityForResult(intent, START_INDOOR_ACTIVITY);
                                }
                            }.execute();
                        } else {
                            return new AsyncTask<Void, Integer, Boolean>() {
                                ProgressDialog progress;

                                @Override
                                protected void onPreExecute() {
                                    super.onPreExecute();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progress = ProgressDialog.show(TripSetupActivity.this, "Loading Map",
                                                    "This will take a few seconds", true);
                                        }
                                    });

                                }

                                @Override
                                protected Boolean doInBackground(Void... voids) {
                                    // Load floor plan for destination
                                    try {
                                        objectRetrievalMethod(false, end_id.split("_")[0]+"_"+end_id.split("_")[1]);
                                        // Find node on destination node floor level plan
                                        DESTINATION_NODE = findNode(end_id, end_floor_plan_nodes);
                                        // Get directions from starting node to stairs
                                        stairs1 = findNodeByType("STAIRS", start_floor_plan_nodes);
                                        getDirections2(STARTING_NODE, stairs1, start_floor_plan_nodes, start_path);
                                        // Get directions from stairs to destination
                                        stairs2 = findNodeByType("STAIRS", end_floor_plan_nodes);
                                        getDirections2(stairs2, DESTINATION_NODE, end_floor_plan_nodes, end_path);
                                        Intent intent = new Intent(getBaseContext(), IndoorMapActivity.class);
                                        intent.putExtra("end_floor", true);
                                        intent.putExtra("start_id", start_id);
                                        intent.putExtra("end_id", end_id);
                                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); //flag
                                        startActivityForResult(intent, START_INDOOR_ACTIVITY);

                                    } catch (ClassNotFoundException | IOException e) {
                                        e.printStackTrace();
                                        return false;
                                    }
                                    return true;
                                }

                                @Override
                                protected void onPostExecute(Boolean floor_plan_loaded) {
                                    super.onPostExecute(floor_plan_loaded);
                                    progress.dismiss();
                                }
                            }.execute();
                        }
                    } else {
                        // venue not in same building
                        if (!start_model.equals("building")) {
                            if (STARTING_NODE != null) {
                                // Check if node is on first floor
                                if (STARTING_NODE.getNode_ID().split("_")[1].equals("01")) {
                                    //ENTRANCE
                                    return new AsyncTask<Void, Integer, Boolean>() {
                                        ProgressDialog progress;

                                        @Override
                                        protected void onPreExecute() {
                                            super.onPreExecute();
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progress = ProgressDialog.show(TripSetupActivity.this, "Loading floor plan",
                                                            "This will take a few seconds", true);
                                                }
                                            });

                                        }

                                        @Override
                                        protected Boolean doInBackground(Void... voids) {
                                            DESTINATION_NODE = findNodeByType("ENTRANCE", start_floor_plan_nodes);
                                            getDirections2(STARTING_NODE, DESTINATION_NODE, start_floor_plan_nodes, start_path);
                                            STARTING_BUILDING = HomeActivity.db.getBuilding(STARTING_NODE.getNode_ID().split("_")[0]);
                                            DESTINATION_BUILDING = HomeActivity.db.getBuilding(end_id.split("_")[1]);
                                            return null;
                                        }

                                        @Override
                                        protected void onPostExecute(Boolean aBoolean) {
                                            super.onPostExecute(aBoolean);
                                            Intent intent = new Intent(getBaseContext(), IndoorMapActivity.class);
                                            intent.putExtra("end_floor", false);
                                            intent.putExtra("start_id", start_id);
                                            intent.putExtra("end_id", end_id);
                                            progress.dismiss();
                                            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); //flag
                                            startActivityForResult(intent, START_INDOOR_ACTIVITY);
                                        }
                                    }.execute();
                                } else {
                                    // Starting node is not on same floor as exit/entrance.
                                    // Need to take user to nearest stairs, then take user to exit
                                    return new AsyncTask<Void, Integer, Boolean>() {
                                        ProgressDialog progress;

                                        @Override
                                        protected void onPreExecute() {
                                            super.onPreExecute();
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progress = ProgressDialog.show(TripSetupActivity.this, "Loading Map",
                                                            "This will take a few seconds", true);
                                                }
                                            });
                                        }

                                        @Override
                                        protected Boolean doInBackground(Void... voids) {
                                            // Load floor plan for destination
                                            try {
                                                String s = start_id.substring(0, start_id.indexOf("_")) + "_00";
                                                objectRetrievalMethod(false, s);
                                                // Find entrance
                                                DESTINATION_NODE = findNodeByType("ENTRANCE", end_floor_plan_nodes);
                                                // Get directions from starting node to stairs
                                                stairs1 = findNodeByType("STAIRS", start_floor_plan_nodes);
                                                getDirections2(STARTING_NODE, stairs1, start_floor_plan_nodes, start_path);
                                                // Get directions from stairs to destination
                                                getDirections2((stairs2 = findNodeByType("STAIRS", end_floor_plan_nodes)), DESTINATION_NODE, end_floor_plan_nodes, end_path);
                                                // TODO try getting user's location using gps
                                                // Specify starting building for use in outdoor activity
                                                STARTING_BUILDING = HomeActivity.db.getBuilding(STARTING_NODE.getNode_ID().split("_")[0]);
                                                // Specify ending building for use in outdoor activity
                                                DESTINATION_BUILDING = HomeActivity.db.getBuilding(end_id.split("_")[0]);

                                            } catch (ClassNotFoundException | IOException e) {
                                                e.printStackTrace();
                                                return false;
                                            }
                                            return true;
                                        }

                                        @Override
                                        protected void onPostExecute(Boolean floor_plan_loaded) {
                                            super.onPostExecute(floor_plan_loaded);
                                            if (floor_plan_loaded) {
                                                Intent intent = new Intent(getBaseContext(), IndoorMapActivity.class);
                                                intent.putExtra("end_floor", true);
                                                intent.putExtra("start_id", start_id);
                                                intent.putExtra("stairs1", stairs1.getNode_ID());
                                                intent.putExtra("stairs2", stairs2.getNode_ID());
                                                intent.putExtra("end_id", DESTINATION_NODE.getNode_ID());
                                                Toast.makeText(context, DESTINATION_NODE.getNode_ID(), Toast.LENGTH_SHORT).show();
                                                intent.putExtra("final_end_id", end_id);
                                                progress.dismiss();
                                                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); //flag
                                                startActivityForResult(intent, START_INDOOR_ACTIVITY);
                                            }
                                        }
                                    }.execute();
                                }
                            }
                        } else {
                            return new AsyncTask<Void, Integer, Boolean>() {
                                ProgressDialog progress;
                                @Override
                                protected void onPreExecute() {
                                    super.onPreExecute();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progress = ProgressDialog.show(TripSetupActivity.this, "Loading Map",
                                                    "This will take a few seconds", true);
                                        }
                                    });
                                }
                                @Override
                                protected Boolean doInBackground(Void... voids) {
                                    // exit to entrance to final destination
                                    DESTINATION_BUILDING = HomeActivity.db.getBuilding(end_id.split("_")[0]);
                                    updateDestinationLocation();
                                    return true;
                                }
                                @Override
                                protected void onPostExecute(Boolean floor_plan_loaded) {
                                    super.onPostExecute(floor_plan_loaded);
                                    Intent intent = new Intent(TripSetupActivity.this, OutdoorMapActivity.class);
                                    intent.putExtra("is_building", true);

                                    start_model = "venue";
                                    start_id = end_id.split("_")[0]+"_00";
                                    intent.putExtra("start_id", start_id);
                                    intent.putExtra("end_id", end_id);

                                    startActivityForResult(intent, START_OUTDOOR_ACTIVITY);
                                    progress.dismiss();
                                }
                            }.execute();
                        }
                    }
                } else {
                    // Outdoor to indoor
                    return new AsyncTask<Void, Integer, Boolean>() {
                        ProgressDialog progress;
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progress = ProgressDialog.show(TripSetupActivity.this, "Loading Map",
                                            "This will take a few seconds", true);
                                }
                            });
                        }

                        @Override
                        protected Boolean doInBackground(Void... voids) {
                            DESTINATION_BUILDING = HomeActivity.db.getBuilding(end_id.split("_")[0]);
                            updateDestinationLocation();
                            Intent intent = new Intent(context, OutdoorMapActivity.class);
                            intent.putExtra("is_building", true);

                            start_model = "venue";
                            start_id = end_id.split("_")[0]+"_00";

                            try {
                                objectRetrievalMethod(true, start_id.split("_")[0]+"_"+start_id.split("_")[1]);
                            } catch (ClassNotFoundException | IOException e) {
                                e.printStackTrace();
                            }
                            if (Integer.valueOf(DESTINATION_BUILDING.getBuilding_Number())==35){
                                STARTING_NODE = findNodeByType("ENTRANCE", start_floor_plan_nodes);
                                start_id = STARTING_NODE.getNode_ID();
                            } else {
                                STARTING_NODE = findNode(start_id, start_floor_plan_nodes);
                            }

                            intent.putExtra("start_id", start_id);
                            intent.putExtra("end_id", end_id);

                            startActivityForResult(intent, START_OUTDOOR_ACTIVITY);
                            return true;
                        }

                        @Override
                        protected void onPostExecute(Boolean aBoolean) {
                            super.onPostExecute(aBoolean);
                            progress.dismiss();
                        }
                    }.execute();

                }
                break;
            case "poi":
                // Determine if POI is building, venue or qr code
                final POI temp_poi = HomeActivity.db.getPOI(end_id);
                switch (temp_poi.determinePOIKind()) {
                    case "venue":
                        if (STARTING_NODE != null) {
                            // Check if nodes are in same building
                            if (STARTING_NODE.getNode_ID().split("_")[0].equals(temp_poi.getBuilding_Number())) {
                                // Check if nodes are on same floor level
                                if (STARTING_NODE.getNode_ID().split("_")[1].equals(temp_poi.getFloor_Level())) {
                                    return new AsyncTask<Void, Integer, Boolean>() {
                                        ProgressDialog progress;
                                        @Override
                                        protected void onPreExecute() {
                                            super.onPreExecute();
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progress = ProgressDialog.show(TripSetupActivity.this, "Loading Map",
                                                            "This will take a few seconds", true);
                                                }
                                            });

                                        }
                                        @Override
                                        protected Boolean doInBackground(Void... voids) {
                                            DESTINATION_NODE = findNode(temp_poi.getBuildingFloorDoorID(), start_floor_plan_nodes);
                                            getDirections2(STARTING_NODE, DESTINATION_NODE, start_floor_plan_nodes, start_path);
                                            return null;
                                        }
                                        @Override
                                        protected void onPostExecute(Boolean aBoolean) {
                                            super.onPostExecute(aBoolean);
                                            Intent intent = new Intent(getBaseContext(), IndoorMapActivity.class);
                                            intent.putExtra("end_floor", false);
                                            intent.putExtra("start_id", start_id);
                                            intent.putExtra("end_id", temp_poi.getBuildingFloorDoorID());
                                            progress.dismiss();
                                            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); //flag
                                            startActivityForResult(intent, START_INDOOR_ACTIVITY);
                                        }
                                    }.execute();
                                } else {
                                    return new AsyncTask<Void, Integer, Boolean>() {
                                        ProgressDialog progress;
                                        @Override
                                        protected void onPreExecute() {
                                            super.onPreExecute();
                                            progress = ProgressDialog.show(TripSetupActivity.this, "Loading Map",
                                                    "This will take a few seconds", true);
                                        }
                                        @Override
                                        protected Boolean doInBackground(Void... voids) {
                                            // Load floor plan for destination
                                            try {
                                                objectRetrievalMethod(false, temp_poi.getBuildingFloorDoorID().substring(0, temp_poi.getBuildingFloorDoorID().lastIndexOf("_")));

                                                // Find node on destination node floor level plan
                                                DESTINATION_NODE = findNode(temp_poi.getBuildingFloorDoorID(), end_floor_plan_nodes);
                                                // Get directions from starting node to stairs
                                                getDirections2(STARTING_NODE, (stairs1 = findNodeByType("STAIRS", start_floor_plan_nodes)), start_floor_plan_nodes, start_path);
                                                // Get directions from stairs to destination
                                                getDirections2((stairs2 = findNodeByType("STAIRS", end_floor_plan_nodes)), DESTINATION_NODE, end_floor_plan_nodes, end_path);
                                            } catch (ClassNotFoundException | IOException e) {
                                                e.printStackTrace();
                                                return false;
                                            }
                                            return true;
                                        }
                                        @Override
                                        protected void onPostExecute(Boolean floor_plan_loaded) {
                                            super.onPostExecute(floor_plan_loaded);
                                            if (floor_plan_loaded) {

                                                Intent intent = new Intent(getBaseContext(), IndoorMapActivity.class);
                                                intent.putExtra("end_floor", true);
                                                intent.putExtra("start_id", start_id);
                                                intent.putExtra("end_id", temp_poi.getBuildingFloorDoorID());
                                                progress.dismiss();
                                                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); //flag
                                                startActivityForResult(intent, START_INDOOR_ACTIVITY);
                                            }
                                        }
                                    }.execute();
                                }
                            }
                        }
                        break;
                    case "building":
                        // Outdoor navigation
                        if (STARTING_BUILDING != null) {
                            return new AsyncTask<Void, Integer, Boolean>() {
                                @Override
                                protected Boolean doInBackground(Void... voids) {
                                    DESTINATION_BUILDING = HomeActivity.db.getBuilding(temp_poi.getBuilding_Number());
                                    updateDestinationLocation();
                                    Intent intent = new Intent(context, OutdoorMapActivity.class);
                                    intent.putExtra("is_building", true);
                                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); //flag
                                    startActivityForResult(intent, START_OUTDOOR_ACTIVITY);
                                    return true;
                                }
                            }.execute();

                        } else // Indoor to outdoor
                            if (STARTING_NODE != null) {
                                // Check if node is on ground floor
                                if (STARTING_NODE.getNode_ID().split("_")[1].equals("00")) {
                                    //ENTRANCE
                                    return new AsyncTask<Void, Integer, Boolean>() {
                                        ProgressDialog progress;
                                        @Override
                                        protected void onPreExecute() {
                                            super.onPreExecute();
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progress = ProgressDialog.show(TripSetupActivity.this, "Loading floor plan",
                                                            "This will take a few seconds", true);
                                                }
                                            });

                                        }
                                        @Override
                                        protected Boolean doInBackground(Void... voids) {
                                            DESTINATION_NODE = findNodeByType("ENTRANCE", start_floor_plan_nodes);
                                            getDirections2(STARTING_NODE, DESTINATION_NODE, start_floor_plan_nodes, start_path);
                                            STARTING_BUILDING = HomeActivity.db.getBuilding(STARTING_NODE.getNode_ID().split("_")[0]);
                                            DESTINATION_BUILDING = HomeActivity.db.getBuilding(temp_poi.getBuilding_Number());
                                            updateDestinationLocation();
                                            return true;
                                        }
                                        @Override
                                        protected void onPostExecute(Boolean aBoolean) {
                                            super.onPostExecute(aBoolean);
                                            Intent intent = new Intent(getBaseContext(), IndoorMapActivity.class);
                                            intent.putExtra("end_floor", false);
                                            intent.putExtra("start_id", start_id);
                                            intent.putExtra("end_id", temp_poi.getBuildingFloorDoorID());
                                            progress.dismiss();
                                            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); //flag
                                            startActivityForResult(intent, START_INDOOR_ACTIVITY);
                                        }
                                    }.execute();
                                } else {
                                    // Starting node is not on ground floor. Need to take user to nearest stairs, then take user to exit once they're on ground floor
                                    return new AsyncTask<Void, Integer, Boolean>() {
                                        ProgressDialog progress;
                                        @Override
                                        protected void onPreExecute() {
                                            super.onPreExecute();
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progress = ProgressDialog.show(TripSetupActivity.this, "Loading Map",
                                                            "This will take a few seconds", true);
                                                }
                                            });
                                        }
                                        @Override
                                        protected Boolean doInBackground(Void... voids) {
                                            // Load floor plan for destination
                                            try {
                                                String s = start_id.substring(0, start_id.indexOf("_")) + "_00";
                                                objectRetrievalMethod(false, s);
                                                // Find entrance
                                                DESTINATION_NODE = findNodeByType("ENTRANCE", end_floor_plan_nodes);
                                                // Get directions from starting node to stairs
                                                getDirections2(STARTING_NODE, (stairs1 = findNodeByType("STAIRS", start_floor_plan_nodes)), start_floor_plan_nodes, start_path);
                                                // Get directions from stairs to destination
                                                getDirections2((stairs2 = findNodeByType("STAIRS", end_floor_plan_nodes)), DESTINATION_NODE, end_floor_plan_nodes, end_path);
                                                // TODO try getting user's location using gps
                                                // Specify starting building for use in outdoor activity
                                                STARTING_BUILDING = HomeActivity.db.getBuilding(STARTING_NODE.getNode_ID().split("_")[0]);
                                                // Specify ending building for use in outdoor activity
                                                DESTINATION_BUILDING = HomeActivity.db.getBuilding(temp_poi.getBuilding_Number());

                                            } catch (ClassNotFoundException | IOException e) {
                                                e.printStackTrace();
                                                return false;
                                            }
                                            return true;
                                        }
                                        @Override
                                        protected void onPostExecute(Boolean floor_plan_loaded) {
                                            super.onPostExecute(floor_plan_loaded);
                                            if (floor_plan_loaded) {
                                                Intent intent = new Intent(getBaseContext(), IndoorMapActivity.class);
                                                intent.putExtra("end_floor", true);
                                                intent.putExtra("start_id", start_id);
                                                intent.putExtra("end_id", start_id.substring(0, start_id.indexOf("_")) + "_00");
                                                progress.dismiss();
                                                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); //flag
                                                startActivityForResult(intent, START_INDOOR_ACTIVITY);
                                            }
                                        }
                                    }.execute();
                                }
                            }
                        break;
                    case "qr_code":
                        final QRCode temp_qr = HomeActivity.db.getQRCode(temp_poi.getQR_ID());
                        // Determine whether the qr_code is indoor, a building or is outdoor
                        switch (temp_qr.determineWhereQRCodeIs()) {
                            case "indoor":
                                // update end_id
                                end_id = temp_qr.getBuilding_Number()+"_"+temp_qr.getFloor_Level();
                                // TODO finish off this piece of code
                                // check if node is indoor
                                if (STARTING_NODE != null) {
                                    // Check if nodes are in same building
                                    if (STARTING_NODE.getNode_ID().split("_")[0].equals(end_id.split("_")[0])) {
                                        // Check if nodes are on same floor level
                                        if (STARTING_NODE.getNode_ID().split("_")[1].equals(end_id.split("_")[1])) {
                                            // There is no need to load floor plan for destination
                                            return new AsyncTask<Void, Integer, Boolean>() {
                                                ProgressDialog progress;

                                                @Override
                                                protected void onPreExecute() {
                                                    super.onPreExecute();
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            progress = ProgressDialog.show(TripSetupActivity.this, "Loading Map",
                                                                    "This will take a few seconds", true);
                                                        }
                                                    });

                                                }

                                                @Override
                                                protected Boolean doInBackground(Void... voids) {
                                                    // use position of qr code to find
                                                    DESTINATION_NODE = findNodeByXandY(temp_qr.getLatitude(), temp_qr.getLongitude(), start_floor_plan_nodes);
                                                    getDirections2(STARTING_NODE, DESTINATION_NODE, start_floor_plan_nodes, start_path);
                                                    return null;
                                                }

                                                @Override
                                                protected void onPostExecute(Boolean aBoolean) {
                                                    super.onPostExecute(aBoolean);
                                                    Intent intent = new Intent(getBaseContext(), IndoorMapActivity.class);
                                                    intent.putExtra("end_floor", false);
                                                    intent.putExtra("start_id", start_id);
                                                    intent.putExtra("end_id", end_id);
                                                    progress.dismiss();
                                                    startActivityForResult(intent, START_INDOOR_ACTIVITY);
                                                }
                                            }.execute();
                                        } else {
                                            return new AsyncTask<Void, Integer, Boolean>() {
                                                ProgressDialog progress;

                                                @Override
                                                protected void onPreExecute() {
                                                    super.onPreExecute();
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            progress = ProgressDialog.show(TripSetupActivity.this, "Loading Map",
                                                                    "This will take a few seconds", true);
                                                        }
                                                    });

                                                }

                                                @Override
                                                protected Boolean doInBackground(Void... voids) {
                                                    // Load floor plan for destination
                                                    try {
                                                        objectRetrievalMethod(false, end_id);

                                                        // Find node on destination node floor level plan
                                                        DESTINATION_NODE = findNodeByXandY(temp_qr.getLatitude(), temp_qr.getLongitude(), end_floor_plan_nodes);
                                                        // Get directions from starting node to stairs
                                                        stairs1 = findNodeByType("STAIRS", start_floor_plan_nodes);
                                                        getDirections2(STARTING_NODE, stairs1, start_floor_plan_nodes, start_path);
                                                        // Get directions from stairs to destination
                                                        stairs2 = findNodeByType("STAIRS", end_floor_plan_nodes);
                                                        getDirections2(stairs2, DESTINATION_NODE, end_floor_plan_nodes, end_path);
                                                        Intent intent = new Intent(getBaseContext(), IndoorMapActivity.class);
                                                        intent.putExtra("end_floor", true);
                                                        intent.putExtra("start_id", start_id);
                                                        intent.putExtra("end_id", end_id);
                                                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); //flag
                                                        startActivityForResult(intent, START_INDOOR_ACTIVITY);

                                                    } catch (ClassNotFoundException | IOException e) {
                                                        e.printStackTrace();
                                                        return false;
                                                    }
                                                    return true;
                                                }

                                                @Override
                                                protected void onPostExecute(Boolean floor_plan_loaded) {
                                                    super.onPostExecute(floor_plan_loaded);
                                                    progress.dismiss();
                                                }
                                            }.execute();
                                        }
                                    } else {
                                        // venue not in same building
                                        if (!start_model.equals("building")) {
                                            if (STARTING_NODE != null) {
                                                // Check if node is on first floor
                                                if (STARTING_NODE.getNode_ID().split("_")[1].equals("01")) {
                                                    //ENTRANCE
                                                    return new AsyncTask<Void, Integer, Boolean>() {
                                                        ProgressDialog progress;

                                                        @Override
                                                        protected void onPreExecute() {
                                                            super.onPreExecute();
                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    progress = ProgressDialog.show(TripSetupActivity.this, "Loading floor plan",
                                                                            "This will take a few seconds", true);
                                                                }
                                                            });

                                                        }

                                                        @Override
                                                        protected Boolean doInBackground(Void... voids) {
                                                            DESTINATION_NODE = findNodeByType("ENTRANCE", start_floor_plan_nodes);
                                                            getDirections2(STARTING_NODE, DESTINATION_NODE, start_floor_plan_nodes, start_path);
                                                            STARTING_BUILDING = HomeActivity.db.getBuilding(STARTING_NODE.getNode_ID().split("_")[0]);
                                                            DESTINATION_BUILDING = HomeActivity.db.getBuilding(end_id.split("_")[1]);
                                                            return null;
                                                        }

                                                        @Override
                                                        protected void onPostExecute(Boolean aBoolean) {
                                                            super.onPostExecute(aBoolean);
                                                            Intent intent = new Intent(getBaseContext(), IndoorMapActivity.class);
                                                            intent.putExtra("end_floor", false);
                                                            intent.putExtra("start_id", start_id);
                                                            intent.putExtra("end_id", end_id);
                                                            progress.dismiss();
                                                            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); //flag
                                                            startActivityForResult(intent, START_INDOOR_ACTIVITY);
                                                        }
                                                    }.execute();
                                                } else {
                                                    // Starting node is not on same floor as exit/entrance.
                                                    // Need to take user to nearest stairs, then take user to exit
                                                    return new AsyncTask<Void, Integer, Boolean>() {
                                                        ProgressDialog progress;

                                                        @Override
                                                        protected void onPreExecute() {
                                                            super.onPreExecute();
                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    progress = ProgressDialog.show(TripSetupActivity.this, "Loading Map",
                                                                            "This will take a few seconds", true);
                                                                }
                                                            });
                                                        }

                                                        @Override
                                                        protected Boolean doInBackground(Void... voids) {
                                                            // Load floor plan for destination
                                                            try {
                                                                String s = start_id.substring(0, start_id.indexOf("_")) + "_01";
                                                                objectRetrievalMethod(false, s);
                                                                // Find entrance
                                                                DESTINATION_NODE = findNodeByType("ENTRANCE", end_floor_plan_nodes);
                                                                // Get directions from starting node to stairs
                                                                stairs1 = findNodeByType("STAIRS", start_floor_plan_nodes);
                                                                getDirections2(STARTING_NODE, stairs1, start_floor_plan_nodes, start_path);
                                                                // Get directions from stairs to destination
                                                                getDirections2((stairs2 = findNodeByType("STAIRS", end_floor_plan_nodes)), DESTINATION_NODE, end_floor_plan_nodes, end_path);
                                                                // TODO try getting user's location using gps
                                                                // Specify starting building for use in outdoor activity
                                                                STARTING_BUILDING = HomeActivity.db.getBuilding(STARTING_NODE.getNode_ID().split("_")[0]);
                                                                // Specify ending building for use in outdoor activity
                                                                DESTINATION_BUILDING = HomeActivity.db.getBuilding(end_id.split("_")[0]);

                                                            } catch (ClassNotFoundException | IOException e) {
                                                                e.printStackTrace();
                                                                return false;
                                                            }
                                                            return true;
                                                        }

                                                        @Override
                                                        protected void onPostExecute(Boolean floor_plan_loaded) {
                                                            super.onPostExecute(floor_plan_loaded);
                                                            if (floor_plan_loaded) {
                                                                Intent intent = new Intent(getBaseContext(), IndoorMapActivity.class);
                                                                intent.putExtra("end_floor", true);
                                                                intent.putExtra("start_id", start_id);
                                                                intent.putExtra("stairs1", stairs1.getNode_ID());
                                                                intent.putExtra("stairs2", stairs2.getNode_ID());
                                                                intent.putExtra("end_id", DESTINATION_NODE.getNode_ID());
                                                                Toast.makeText(context, DESTINATION_NODE.getNode_ID(), Toast.LENGTH_SHORT).show();
                                                                intent.putExtra("final_end_id", end_id);
                                                                progress.dismiss();
                                                                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); //flag
                                                                startActivityForResult(intent, START_INDOOR_ACTIVITY);
                                                            }
                                                        }
                                                    }.execute();
                                                }
                                            }
                                        } else {
                                            return new AsyncTask<Void, Integer, Boolean>() {
                                                ProgressDialog progress;
                                                @Override
                                                protected void onPreExecute() {
                                                    super.onPreExecute();
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            progress = ProgressDialog.show(TripSetupActivity.this, "Loading Map",
                                                                    "This will take a few seconds", true);
                                                        }
                                                    });
                                                }
                                                @Override
                                                protected Boolean doInBackground(Void... voids) {
                                                    // exit to entrance to final destination
                                                    DESTINATION_BUILDING = HomeActivity.db.getBuilding(temp_qr.getBuilding_Number());
                                                    //updateDestinationLocation();
                                                    return true;
                                                }
                                                @Override
                                                protected void onPostExecute(Boolean floor_plan_loaded) {
                                                    super.onPostExecute(floor_plan_loaded);
                                                    Intent intent = new Intent(TripSetupActivity.this, OutdoorMapActivity.class);
                                                    intent.putExtra("is_building", true);
                                                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); //flag
                                                    startActivity(intent);
                                                    progress.dismiss();
                                                }
                                            }.execute();
                                        }
                                    }
                                } else {
                                    if (STARTING_BUILDING!=null){
                                        return new AsyncTask<Void, Integer, Boolean>() {
                                            @Override
                                            protected Boolean doInBackground(Void... voids) {
                                                DESTINATION_BUILDING = HomeActivity.db.getBuilding(temp_qr.getBuilding_Number());
                                                updateDestinationLocation();
                                                Intent intent = new Intent(context, OutdoorMapActivity.class);
                                                intent.putExtra("is_building", true);
                                                intent.putExtra("final_destination", false);

                                                try {
                                                    // load entry point of building
                                                    objectRetrievalMethod(false, temp_qr.getBuilding_Number()+"_"+"00");
                                                } catch (ClassNotFoundException | IOException e) {
                                                    e.printStackTrace();
                                                    Log.e("TripAct", "Couldn't load floor plan");
                                                }
                                                DESTINATION_NODE = findNodeByType("ENTRANCE", end_floor_plan_nodes);

                                                if (DESTINATION_NODE != null)
                                                    intent.putExtra("final_end_id",DESTINATION_NODE.getNode_ID()); // need to find final id

                                                startActivityForResult(intent, START_OUTDOOR_ACTIVITY);
                                                return true;
                                            }
                                        }.execute();
                                    }
                                }
                                break;
                            case "building":
                                if (STARTING_BUILDING != null) {
                                    return new AsyncTask<Void, Integer, Boolean>() {
                                        @Override
                                        protected Boolean doInBackground(Void... voids) {
                                            DESTINATION_BUILDING = HomeActivity.db.getBuilding(temp_qr.getBuilding_Number());
                                            updateDestinationLocation();
                                            Intent intent = new Intent(context, OutdoorMapActivity.class);
                                            intent.putExtra("is_building", true);
                                            startActivityForResult(intent, START_OUTDOOR_ACTIVITY);
                                            return true;
                                        }
                                    }.execute();
                                }
                                break;
                            case "outdoor":
                                if (STARTING_OUTDOOR_LOCATION != null){
                                    return new AsyncTask<Void, Integer, Boolean>() {
                                        @Override
                                        protected Boolean doInBackground(Void... voids) {
                                            DESTINATION_OUTDOOR_LOCATION = new LngLat(temp_qr.getLongitude(), temp_qr.getLatitude());
                                            Intent intent = new Intent(context, OutdoorMapActivity.class);
                                            intent.putExtra("is_building", false);
                                            startActivityForResult(intent, START_OUTDOOR_ACTIVITY);
                                            return true;
                                        }
                                    }.execute();
                                }
                                break;
                            default:
                                Toast.makeText(this, "POI couldn't be found", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        Toast.makeText(this, "POI couldn't be found", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                Toast.makeText(this, "Invalid location data passed", Toast.LENGTH_SHORT).show();
                break;
        }
        return null;
    }

    public static void updateDestinationLocation() {
        if (DESTINATION_BUILDING.getBuilding_Number().equals("9") || DESTINATION_BUILDING.getBuilding_Number().equals("4") ) {
            double distance = Float.MAX_VALUE;
            //Location l3 = new Location("point a");
            //Location l2 = new Location("point b");

            Double[] location3 = new Double[]{0.0, 0.0},
                    location2 = new Double[]{STARTING_BUILDING.getLatitude(), STARTING_BUILDING.getLongitude()};

        /*l2.setLatitude(STARTING_BUILDING.getLatitude());
        l2.setLongitude(STARTING_BUILDING.getLongitude());*/

            for (Entrance entrance : HomeActivity.db.getEntrances()) {
                // find nearest entrance, given starting exit
                if (entrance.Building.equals(Integer.valueOf(DESTINATION_BUILDING.getBuilding_Number()))) {
                    //Location l1 = new Location("point a");
                    //l1.setLatitude(entrance.Latitude);
                    //l1.setLongitude(entrance.Longitude);

                    Double[] location1 = new Double[]{entrance.Latitude, entrance.Longitude};

                    if (distance >= distance(location2[0], location1[0], location2[1], location1[1], 0.0, 0.)) {
                        distance = distance(location2[0], location1[0], location2[1], location1[1], 0.0, 0.);
                        location3 = location1;
                    }

                /*if (distance >= l2.distanceTo(l1)) {
                    distance = l2.distanceTo(l1);
                    l3 = l1;
                }*/
                }
            }
            // alter lat and long
        /*if (Math.round(l3.getLongitude()) != 0 || Math.round(l3.getLatitude()) != 0) {
            DESTINATION_BUILDING.setLongitude(l3.getLongitude());
            DESTINATION_BUILDING.setLatitude(l3.getLatitude());
        }*/

            if (location3[0] != 0 && location3[1] != 0) {
                DESTINATION_BUILDING.setLatitude(location3[0]);
                DESTINATION_BUILDING.setLongitude(location3[1]);
            }
        }
    }

    private static double distance(double lat1, double lat2, double lon1, double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    public void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    PERMISSIONS_STORAGE, SET_EXTERNAL_STORAGE_REQUEST);
        }
    }

    // caters for nodes to STAIRS
    public void getDirections2(PlanNode start, PlanNode end, PlanNode[][] plan_to_Search, ArrayList<Cell> path_to_populate) {

        grid = new Cell[X_MAX][Y_MAX];

        AStarAlgorithm a_star = new AStarAlgorithm(grid);

        a_star.open = new PriorityQueue<>(X_MAX * Y_MAX, new CellComparator());
        a_star.closed = new boolean[X_MAX][Y_MAX];

        a_star.setStartingCell(start);
        a_star.setEndingCell(end);

        for (int x = 0; x < X_MAX; x++) {
            for (int y = 0; y < Y_MAX; y++) {
                a_star.grid[x][y] = new Cell(x, y);
                a_star.grid[x][y].setH_cost(Math.abs(x - end.getX()) + Math.abs(y - end.getY()));

                PlanNode cur = plan_to_Search[x][y];
                if (cur.getNode_Type().equals("BLOCKED"))
                    a_star.setCellAsBlocked(cur); // set blocked cells to null
            }
        }

        a_star.grid[a_star.start_node.getX()][a_star.start_node.getY()].setFinal_cost(0);

        a_star.runAStar();

        if (a_star.closed[a_star.end_node.getX()][a_star.end_node.getY()]) {
            // trace back the path
            Cell cur = a_star.grid[a_star.end_node.getX()][a_star.end_node.getY()];
            path_to_populate.add(cur);
            while (cur.getAncestor() != null) {
                cur = cur.getAncestor();
                path_to_populate.add(cur);
            }
        } else
            Toast.makeText(this, "No possible path", Toast.LENGTH_SHORT).show();

        // nullifies variables of a star class
        a_star.clear();
    }

    private PlanNode findNode(String node_id, PlanNode[][] plan_to_Search) {
        PlanNode cur;
        for (int x = 0; x < X_MAX; x++)
            for (int y = 0; y < Y_MAX; y++) {
                cur = plan_to_Search[x][y];
                if (!cur.getNode_Type().equals("BLOCKED") && !cur.getNode_Type().equals("WALKABLE"))
                    if (cur.getNode_ID().equals(node_id))
                        return cur;
            }
        return null;
    }

    // Given a floor plan, searches for stairs for user to change floor levels
    private PlanNode findNodeByType(String node_type, PlanNode[][] plan_to_Search) {
        PlanNode cur;
        for (int x = 0; x < X_MAX; x++) {
            for (int y = 0; y < Y_MAX; y++) {
                cur = plan_to_Search[x][y];
                if (cur.getNode_Type().equals(node_type))
                    return cur;
            }
        }
        return null;
    }

    private PlanNode findNodeByXandY(double x, double y, PlanNode[][] plan_to_Search) {
        for (PlanNode[] nodes : plan_to_Search) {
            for (PlanNode node : nodes) {
                if (node.getX() == x && node.getY() == y)
                    return node;
            }
        }
        return null;
    }

    private void objectRetrievalMethod(boolean isStart, String floor_plan_name) throws ClassNotFoundException, IOException {
        String file = "/storage/emulated/0/NMMUWalker/b" + floor_plan_name + ".dat";

        ObjectInputStream input = new ObjectInputStream(new FileInputStream(file));
        if (isStart) {
            MAIN_W = input.readInt();
            MAIN_H = input.readInt();
            CELL_SIZE = input.readInt();
            start_floor_plan_nodes = (PlanNode[][]) input.readUnshared();

            X_MAX = MAIN_W / CELL_SIZE;
            Y_MAX = MAIN_H / CELL_SIZE;
        } else {
            MAIN_W = input.readInt();
            MAIN_H = input.readInt();
            CELL_SIZE = input.readInt();
            end_floor_plan_nodes = (PlanNode[][]) input.readUnshared();
        }
        input.close();
    }

    // =============================================================================================
    // Getters and setters

    public static void Clear() {
        start_path.clear();
        end_path.clear();

        STARTING_NODE = null;
        STARTING_BUILDING = null;
        STARTING_OUTDOOR_LOCATION = null;
        grid = null;
        stairs1 = null;
        stairs2 = null;
        start_model = null;
        //
        System.gc();
    }

    public static Building getStartingBuilding() {
        return STARTING_BUILDING;
    }

    public static void setStartingBuilding(Building startingBuilding) {
        STARTING_BUILDING = startingBuilding;
    }

    public static Building getDestinationBuilding() {
        return DESTINATION_BUILDING;
    }

    public static LngLat getStartingOutdoorLocation() {
        return STARTING_OUTDOOR_LOCATION;
    }

    public static LngLat getDestinationOutdoorLocation() {
        return DESTINATION_OUTDOOR_LOCATION;
    }

    public static ArrayList<Cell> getStart_path() {
        return start_path;
    }

    public static ArrayList<Cell> getEnd_path() {
        return end_path;
    }

    public static int getMainW() {
        return MAIN_W;
    }

    public static int getMainH() {
        return MAIN_H;
    }

    public static int getCellSize() {
        return CELL_SIZE;
    }

    public static void setStart_id(String start_id) {
        TripSetupActivity.start_id = start_id;
    }

    public static void setStart_model(String start_model) {
        TripSetupActivity.start_model = start_model;
    }
}
