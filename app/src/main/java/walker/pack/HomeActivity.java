package walker.pack;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import walker.pack.classes.Building;
import walker.pack.classes.Entrance;
import walker.pack.classes.FloorPlan;
import walker.pack.classes.POI;
import walker.pack.classes.QRCode;
import walker.pack.classes.Staff;
import walker.pack.classes.Venue;
import walker.pack.sqlitedatabase.DatabaseHelper;
import walker.pack.sqlitedatabase.HttpHandler;

/**
 * Created by s214108503 on 2017/06/20.
 */

public class HomeActivity extends AppCompatActivity {

    public static DatabaseHelper db;
    private String TAG = HomeActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ImageView img_view_home_navigate = (ImageView) findViewById(R.id.img_view_home_navigate);
        img_view_home_navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), TripSetupActivity.class);
                startActivity(intent);
            }
        });

        SharedPreferences sharedpreferences = getSharedPreferences(SplashActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        String starting_method = getIntent().getStringExtra("starting");
        if (!starting_method.equals("splash") || sharedpreferences.getBoolean("FirstTimeUser", true)){
            getBaseContext().deleteDatabase(DatabaseHelper.database_name);
            db = new DatabaseHelper(this);
            AsyncTask<Boolean, String, Boolean> dataDownloadingTask = new GetDataFromServer();
            dataDownloadingTask.execute();
        } else {
            db = new DatabaseHelper(this);
        }

        // =================================Dummy Data==============================================
        /*db.addBuilding(new Building("4", "Old Mutual Halls", -34.00861, 25.66975));
        db.addBuilding(new Building("9", "Embizweni", -34.00860, 25.66972));
        db.addBuilding(new Building("35", "Building 35", -34.00947, 25.67055));
        db.addBuilding(new Building("123", "Building 123", -34.00930, 25.67187));

        db.addVenue(new Venue("01", "00", "4", "LECTURE ROOM", "01", 77.0, 143.0));
        db.addVenue(new Venue("07", "00", "4", "LECTURE ROOM", "07", 61.0, 110.0));
        db.addVenue(new Venue("05", "00", "4", "LECTURE ROOM", "05", 128.0, 94.0));
        db.addVenue(new Venue("37", "02", "9", "OFFICE", "None", 226.0, 93.0));
        db.addVenue(new Venue("15", "02", "9", "OFFICE", "None", 102.0, 77.0));
        db.addVenue(new Venue("04", "00", "9", "OFFICE", "None", 54.0, 84.0));

        db.addStaffMember(new Staff("cschd", "37", "02", "9", "CH", "Dixie", "Lecturer",
                "Computer Sciences", "Summerstrand Campus (South)", "0415042213",
                "dixie@nmmu.ac.za", "http://cs.mandela.ac.za/CMSModules/Avatars/CMSPages/GetAvatar.aspx?avatarguid=68ff3da9-2233-4e76-b4bf-12b0b996b65b&width=72"));

        db.addStaffMember(new Staff("csdf", "15", "02", "9", "Duduetsang", "Fani", "Admin Coordinator",
                "Computer Sciences", "Summerstrand Campus (South)", "0415042530",
                "duduetsang.fani2@nmmu.ac.za", "http://cs.mandela.ac.za/CMSModules/Avatars/CMSPages/GetAvatar.aspx?avatarguid=9439757c-37b1-455a-b3ba-13c93e25b6b0&width=72"));

        db.addStaffMember(new Staff("csjg", "18", "02", "9", "Jean", "Greyling", "Associate Professor",
                "Computer Sciences", "Summerstrand Campus (South)", "+27415042081",
                "jean.greyling@mandela.ac.za", "http://cs.mandela.ac.za/CMSModules/Avatars/CMSPages/GetAvatar.aspx?avatarguid=3beca0cb-7d3d-4ab0-8c90-80ccb78a1a65"));

        // QRCode(String QR_ID, String building_Number, String description, String image_URL, Double latitude, Double longitude, int floor_Level)

        db.addQRCode(new QRCode("qr1", "9", "QR Code for telephone",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d0/QR_code_for_mobile_English_Wikipedia.svg/220px-QR_code_for_mobile_English_Wikipedia.svg.png",
                200.0, 131.0, 0));

        db.addQRCode(new QRCode("qr2", "9", "ENTRANCE",
                "?", 159.0, 82.0, 0));

        db.addQRCode(new QRCode("qr3", "9", "QR Code for telephone",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d0/QR_code_for_mobile_English_Wikipedia.svg/220px-QR_code_for_mobile_English_Wikipedia.svg.png",
                200.0, 131.0, 0));

        db.addQRCode(new QRCode("qr4", "9", "Entrance on the first floor",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d0/QR_code_for_mobile_English_Wikipedia.svg/220px-QR_code_for_mobile_English_Wikipedia.svg.png",
                40.00, 139.00, 1));

        db.addQRCode(new QRCode("qr5", "4", "Entrance linking with Embizweni",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d0/QR_code_for_mobile_English_Wikipedia.svg/220px-QR_code_for_mobile_English_Wikipedia.svg.png",
                244.00, 130.00, 1));

        db.addQRCode(new QRCode("qr6", "9", "Main entrance from taxi near taxi stop in front of main building",
                "https://drive.google.com/open?id=0B7UCy8E1aGmpMVp4WU5NWXBWaUU",
                82.00, 149.00, 0));

        //POI(String POI_ID, String door_ID, String floor_Level, String building_Number, String QR_ID, String type, String description)
        db.addPOI(new POI("poi1", "15", "02", "9", "", "COMPUTER SCIENCES", "THE GOTO PERSON"));
        db.addPOI(new POI("poi2", "", "", "9", "", "BUILDING", "Embizweni building"));
        db.addPOI(new POI("poi3", "", "", "", "qr1", "TELEPHONE", "Telkom telephone"));
        db.addPOI(new POI("poi4", "", "", "35", "", "BUILDING", "Building 35"));
        db.addPOI(new POI("poi5", "", "", "123", "", "BUILDING", "Building 123"));

        //============Start dummy data===========
        //db.addBuilding(new Building("99", "Dummy Building 9", -34.00869, 25.66931));
        db.addBuilding(new Building("99", "Dummy Building 9", -34.00882, 25.66948));
        db.addBuilding(new Building("98", "Dummy Building ", -34.00869, 25.66917));
        db.addPOI(new POI("poi6", "", "", "99", "", "BUILDING", "Dummy Building 9"));
        db.addPOI(new POI("poi7", "", "", "98", "", "BUILDING", "Dummy Building 4"));
        //============End dummy data=============*/

        /*db.addFavStaff(db.getStaffMember("cschd"));
        db.addFavVenue(db.getVenue("37", "02", "9"));
        db.addFavVenue(db.getVenue("05", "00", "4"));
        db.addFavPOI(db.getPOI("poi4"));

        db.addEntrance(new Entrance(4, 1, "0", 244.00, 130.00, -34.0086800, 25.66917)); // from 6 to 4
        db.addEntrance(new Entrance(4, 1, "0", 49.00, 127.00, -34.0086900, 25.6688600)); // from 9 to 4
        db.addEntrance(new Entrance(9, 1, "0", 40.0, 139.0, -34.0086900, 25.669230)); // from 4 to 9
        db.addEntrance(new Entrance(9, 0, "0", 159.00, 82.00, -34.0084600, 25.6697800));
        db.addEntrance(new Entrance(9, 0, "74", 82.00, 149.00, -34.00882, 25.66947));*/

        /*db.addEntrance(new Entrance(35, 0, "0", 82.00, 149.00, -34.00874, 25.66947));
        db.addEntrance(new Entrance(35, 0, "0", 82.00, 149.00, -34.00874, 25.66947));*/

        // =========================================================================================
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User chose a menu item
        switch (item.getItemId()) {
            case R.id.menu_item_directory:
                // Take user to directory screen
                Toast.makeText(this, "Directory Clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, DirectoryActivity.class);
                intent.putExtra("opening_activity", "home");
                startActivity(intent);
                return true;
            case R.id.menu_item_cms:
                // Take user to CMS home page by launching browser
                startActivity(new Intent(this, CMSActivity.class));
                //Toast.makeText(this, "CMS clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_item_user_settings:
                // Take user to user settings screen
                //Toast.makeText(this, "User Settings clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_more_menu, menu);
        return true;
    }

    private class GetDataFromServer extends  AsyncTask<Boolean, String, Boolean>{
        ProgressDialog progress;
        public ArrayList<Venue> venues = new ArrayList<>();
        public ArrayList<Staff> staff_members = new ArrayList<>();
        public ArrayList<QRCode> qrCodes = new ArrayList<>();
        public ArrayList<FloorPlan> floor_plans = new ArrayList<>();
        public ArrayList<Building> buildings = new ArrayList<>();
        public ArrayList<POI> pois = new ArrayList<>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = ProgressDialog.show(HomeActivity.this, "Downloading Data",
                    "This will take a few seconds", true);
        }
        @Override
        protected Boolean doInBackground(Boolean... booleen) {
            HttpHandler helper = new HttpHandler();
            if (getBuildingsSeconds(helper)){
                for (Building building: buildings)
                    db.addBuilding(building);
            } else return false;
            /*if (getFloorPlansSecond(helper))
                *//*for (int i = 0; i < 1; i++){
                    FloorPlan floorPlan = floor_plans.get(i);

                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(floorPlan.getURL_To_Plan()));
                    request.setDescription("Downloading floor plan: "+(i+1)+" from drive");
                    request.setTitle("Downloading plan"+(i+1));

                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    //request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "b"+floorPlan.getBuilding_Number()+"_"+floorPlan.getFloor_Level()+".dat");
                    request.setDestinationInExternalPublicDir("/NMMUWalker", "b"+floorPlan.getBuilding_Number()+"_"+floorPlan.getFloor_Level()+".dat");

                    // get download service and enqueue file
                    DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    manager.enqueue(request);
                }*//*
            else return false;*/
            if (getQRCodesSeconds(helper))
                for (QRCode qr: qrCodes)
                    db.addQRCode(qr);
            else return false;
            if (getVenuesSecond(helper))
                for (Venue venue: venues)
                    db.addVenue(venue);
            else return false;
            if (getStaffsSecond(helper))
                for (Staff staff: staff_members)
                    db.addStaffMember(staff);
            else return false;
            if (getPOIsSecond(helper))
                for (POI poi: pois)
                    db.addPOI(poi);
            else return false;

            return true;

        }
        @Override
        protected void onPostExecute(Boolean downloadSuccess) {
            super.onPostExecute(downloadSuccess);
            progress.dismiss();
            if (downloadSuccess) {
                addLocalThings();
                Toast.makeText(HomeActivity.this, "Done downloading data", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(HomeActivity.this, "Unable to download data from server, " +
                        "make sure you're internet connection", Toast.LENGTH_SHORT).show();
        }
        @NonNull
        private Boolean getVenuesSecond(HttpHandler helper) {
            String url = "http://nmmuwalker.csdev.nmmu.ac.za/api/venuessecond";
            String json_string = helper.makeServiceCall(url);
            Log.i(TAG, "Response from url: "+json_string);
            if (json_string!=null){
                try{
                    JSONArray json_array = new JSONArray(json_string);
                    for (int i = 0; i<json_array.length(); i++){
                        JSONObject cur = json_array.getJSONObject(i);
                        String Door_ID = cur.getString("Door_ID");
                        int Floor_Level = cur.getInt("Floor_Level");
                        int Building_Number = cur.getInt("Building_Number");
                        String Type = cur.getString("Type");
                        String Alt_Doors = cur.getString("Alt_Doors");
                        if (Alt_Doors.contains("null")) Alt_Doors= "None";
                        Double x = cur.getDouble("x");
                        Double y = cur.getDouble("y");
                        venues.add(new Venue(Door_ID, "0"+String.valueOf(Floor_Level),
                                String.valueOf(Building_Number), Type, Alt_Doors, x, y));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(HomeActivity.this, "Couldn't get json from server.", Toast.LENGTH_SHORT).show();}});
                    return false;
                }

                return true;
            } else
                return false;
        }
        @NonNull
        private Boolean getStaffsSecond(HttpHandler helper) {
            String url = "http://nmmuwalker.csdev.nmmu.ac.za/api/staffssecond";
            String json_string = helper.makeServiceCall(url);
            Log.i(TAG, "Response from url: "+json_string);
            if (json_string!=null){
                try{
                    JSONArray json_array = new JSONArray(json_string);
                    for (int i = 0; i<json_array.length(); i++){
                        JSONObject cur = json_array.getJSONObject(i);
                        String Staff_ID = cur.getString("Staff_ID");
                        String Door_ID = cur.getString("Door_ID");
                        int Floor_Level = cur.getInt("Floor_Level");
                        int Building_Number = cur.getInt("Building_Number");
                        String Name = cur.getString("Name");
                        String Surname = cur.getString("Surname");
                        String Position = cur.getString("Position");
                        String Department = cur.getString("Department");
                        String Campus = cur.getString("Campus");
                        String Phone = cur.getString("Phone");
                        String Email = cur.getString("Email");
                        String Image_URL = cur.getString("Image_URL");
                        staff_members.add(new Staff(Staff_ID, Door_ID, "0"+String.valueOf(Floor_Level),String.valueOf(Building_Number), Name, Surname, Position, Department, Campus, Phone, Email, Image_URL));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(HomeActivity.this, "Couldn't get json from server.", Toast.LENGTH_SHORT).show();}});
                    return false;
                }

                return true;
            } else
                return false;
        }
        @NonNull
        private Boolean getQRCodesSeconds(HttpHandler helper) {
            String url = "http://nmmuwalker.csdev.nmmu.ac.za/api/QRCodesSeconds";
            String json_string = helper.makeServiceCall(url);
            Log.i(TAG, "Response from url: "+json_string);
            if (json_string!=null){
                try{
                    JSONArray json_array = new JSONArray(json_string);
                    for (int i = 0; i<json_array.length(); i++){
                        JSONObject cur = json_array.getJSONObject(i);
                        Integer QR_ID = cur.getInt("QR_ID"); // add "qr"
                        int Building_Number = cur.getInt("Building_Number");
                        String Description = cur.getString("Description");
                        String Image_URL = cur.getString("Image_URL");
                        int Floor = cur.getInt("Floor");
                        Double Latitude = cur.getDouble("Latitude");
                        Double Longitude = cur.getDouble("Longitude");
                        qrCodes.add(new QRCode("qr"+String.valueOf(QR_ID),String.valueOf(Building_Number), Description, Image_URL, Latitude, Longitude, Floor));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(HomeActivity.this, "Couldn't get QR Codes from server.", Toast.LENGTH_SHORT).show();}});
                    return false;
                }

                return true;
            } else
                return false;
        }
        @NonNull
        private Boolean getFloorPlansSecond(HttpHandler helper) {
            String url = "http://nmmuwalker.csdev.nmmu.ac.za/api/FloorPlansSecond";
            String json_string = helper.makeServiceCall(url);
            Log.i(TAG, "Response from url: "+json_string);
            if (json_string!=null){
                try{
                    JSONArray json_array = new JSONArray(json_string);
                    for (int i = 0; i<json_array.length(); i++){
                        JSONObject cur = json_array.getJSONObject(i);
                        int Floor_Level = cur.getInt("Floor_Level");
                        int Building_Number = cur.getInt("Building_Number");
                        String Plan_URL = cur.getString("Plan_URL");
                        floor_plans.add(new FloorPlan("0"+String.valueOf(Floor_Level), String.valueOf(Building_Number), Plan_URL));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(HomeActivity.this, "Couldn't get Floor Plans from server.", Toast.LENGTH_SHORT).show();}});
                    return false;
                }

                return true;
            } else
                return false;
        }
        @NonNull
        private Boolean getBuildingsSeconds(HttpHandler helper) {
            String url = "http://nmmuwalker.csdev.nmmu.ac.za/api/BuildingsSeconds";
            String json_string = helper.makeServiceCall(url);
            Log.i(TAG, "Response from url: "+json_string);
            if (json_string!=null){
                try{
                    JSONArray json_array = new JSONArray(json_string);
                    for (int i = 0; i<json_array.length(); i++){
                        JSONObject cur = json_array.getJSONObject(i);
                        int Building_Number = cur.getInt("Building_Number");
                        String Name = cur.getString("Name");
                        Double Latitude = cur.getDouble("Latitude");
                        Double Longitude = cur.getDouble("Longitude");
                        buildings.add(new Building(String.valueOf(Building_Number), Name, Latitude, Longitude));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(HomeActivity.this, "Couldn't get Buildings data from server.", Toast.LENGTH_SHORT).show();}});
                    return false;
                }

                return true;
            } else
                return false;
        }
        @NonNull
        private Boolean getPOIsSecond(HttpHandler helper) {
            String url = "http://nmmuwalker.csdev.nmmu.ac.za/api/POIsSecond";
            String json_string = helper.makeServiceCall(url);
            Log.i(TAG, "Response from url: "+json_string);
            if (json_string!=null){
                try{
                    JSONArray json_array = new JSONArray(json_string);
                    for (int i = 0; i<json_array.length(); i++){
                        JSONObject cur = json_array.getJSONObject(i);
                        int POI_ID = cur.getInt("POI_ID");
                        String Door_ID = cur.getString("Door_ID"); //nullable
                        if (Door_ID.contains("null")) Door_ID = "";

                        String sFloor_level = "";
                        if (cur.get("Floor_Level")!=null) sFloor_level = "0"+String.valueOf(cur.get("Floor_Level"));
                        if (sFloor_level.contains("null")) sFloor_level = "";

                        String sBuilding_Number = "";
                        if (cur.get("Building_Number")!=null) sBuilding_Number = String.valueOf(cur.get("Building_Number"));
                        if (sBuilding_Number.contains("null")) sBuilding_Number = "";

                        String sQR_ID = "";
                        if (cur.get("QR_ID") != null) sQR_ID = "qr"+String.valueOf(cur.get("QR_ID"));
                        if (sQR_ID.contains("null")) sQR_ID = "";

                        String Type = cur.getString("Type");
                        String Description = cur.getString("Description");
                        pois.add(new POI("poi"+String.valueOf(POI_ID), Door_ID, sFloor_level, sBuilding_Number, sQR_ID, Type, Description));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(HomeActivity.this, "Couldn't get Buildings data from server.", Toast.LENGTH_SHORT).show();}});
                    return false;
                }

                return true;
            } else
                return false;
        }

        private void addLocalThings()
        {
            db.addFavStaff(db.getStaffMember("cschd"));
            db.addFavVenue(db.getVenue("37", "02", "9"));
            db.addFavVenue(db.getVenue("05", "00", "4"));

            db.addEntrance(new Entrance(4, 1, "0", 244.00, 130.00, -34.0086800, 25.66917)); // from 6 to 4
            db.addEntrance(new Entrance(4, 1, "0", 49.00, 127.00, -34.0086900, 25.6688600)); // from 9 to 4
            db.addEntrance(new Entrance(9, 1, "0", 40.0, 139.0, -34.0086900, 25.669230)); // from 4 to 9
            db.addEntrance(new Entrance(9, 0, "0", 159.00, 82.00, -34.0084600, 25.6697800));
            db.addEntrance(new Entrance(9, 0, "74", 82.00, 149.00, -34.00882, 25.66947));

        }

    }

}
