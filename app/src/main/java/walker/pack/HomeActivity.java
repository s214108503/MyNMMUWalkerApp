package walker.pack;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import walker.pack.classes.Building;
import walker.pack.classes.POI;
import walker.pack.classes.QRCode;
import walker.pack.classes.Staff;
import walker.pack.classes.Venue;
import walker.pack.sqlitedatabase.DatabaseHelper;

/**
 * Created by s214108503 on 2017/06/20.
 */

public class HomeActivity extends AppCompatActivity {

    public static DatabaseHelper db;

    public static final int SET_GPS_LOCATION_ACCESS = 6;

    public static String[] PERMISSIONS_ACCESS_GPS = {Manifest.permission.ACCESS_FINE_LOCATION};

    public void verifyStoragePermissions(Activity activity){
        int permission = ActivityCompat.checkSelfPermission(activity,
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,
                    PERMISSIONS_ACCESS_GPS, SET_GPS_LOCATION_ACCESS);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        verifyStoragePermissions(this);

        ImageView img_view_home_navigate = (ImageView) findViewById(R.id.img_view_home_navigate);
        img_view_home_navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), TripSetupActivity.class);
                startActivity(intent);
            }
        });

        db = new DatabaseHelper(this);

        // =================================Dummy Data==============================================
        db.addBuilding(new Building("4", "Old Mutual Halls", -34.00861, 25.66975));
        db.addBuilding(new Building("9", "Embizweni", -34.00861, 25.66975));
        db.addBuilding(new Building("35", "Building 35", -34.00973, 25.67061));
        db.addBuilding(new Building("123", "Building 123", -34.00930, 25.67188));

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

        /*db.addQRCode(new QRCode("qr3", "9", "QR Code for telephone",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d0/QR_code_for_mobile_English_Wikipedia.svg/220px-QR_code_for_mobile_English_Wikipedia.svg.png",
                200.0, 131.0, 0));

        db.addQRCode(new QRCode("qr3", "9", "QR Code for telephone",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d0/QR_code_for_mobile_English_Wikipedia.svg/220px-QR_code_for_mobile_English_Wikipedia.svg.png",
                200.0, 131.0, 0));*/

        //POI(String POI_ID, String door_ID, String floor_Level, String building_Number, String QR_ID, String type, String description)
        db.addPOI(new POI("poi1", "15", "02", "9", "", "COMPUTER SCIENCES", "THE GOTO PERSON"));
        db.addPOI(new POI("poi2", "", "", "9", "", "BUILDING", "Embizweni building"));
        db.addPOI(new POI("poi3", "", "", "", "qr1", "TELEPHONE", "Telkom telephone"));
        db.addPOI(new POI("poi4", "", "", "35", "", "BUILDING", "Building 35"));
        db.addPOI(new POI("poi5", "", "", "123", "", "BUILDING", "Building 123"));

        db.addFavStaff(db.getStaffMember("cschd"));
        db.addFavVenue(db.getVenue("37", "02", "9"));
        db.addFavPOI(db.getPOI("poi4"));
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
                startActivity(intent);
                return true;
            case R.id.menu_item_cms:
                // Take user to CMS home page by launching browser
                Toast.makeText(this, "CMS clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_item_user_settings:
                // Take user to user settings screen
                Toast.makeText(this, "User Settings clicked", Toast.LENGTH_SHORT).show();
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



    /* This thread checks if there's a local copy of MyNMMUDB on device, if one doesn't exist
     * then it is created and populated. Floor plan objects are also downloaded to device. */

    private class InitialisationTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("InitialisationTask: ", " Started");
            Toast.makeText(getBaseContext(), "Started", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            /**/

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

    }

}
