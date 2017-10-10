package walker.pack;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.mapzen.android.graphics.MapFragment;
import com.mapzen.android.graphics.MapzenMap;
import com.mapzen.android.graphics.OnMapReadyCallback;
import com.mapzen.android.graphics.model.Marker;
import com.mapzen.android.routing.MapzenRouter;
import com.mapzen.model.ValhallaLocation;
import com.mapzen.tangram.LngLat;
import com.mapzen.valhalla.Route;
import com.mapzen.valhalla.RouteCallback;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.List;

import walker.pack.classes.Building;
import walker.pack.classes.DefaultValues;
import walker.pack.classes.POI;

public class OutdoorMapActivity extends AppCompatActivity {

    private MapzenMap map;
    MapzenRouter router;
    private boolean enableLocationOnResume = false;
    private TextView indoor_current_location_text_view, indoor_destination_location_text_view,
            indoor_instructions_sections;
    private static double[] startPoint, endPoint;
    public String TAG = "OutdoorMapActivity";
    public int SCAN_QR_CODE_ID = 11;
    String start_id, end_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.outdoor_map_layout);
        indoor_current_location_text_view = (TextView) findViewById(R.id.indoor_current_location_text_view);
        indoor_destination_location_text_view = (TextView) findViewById(R.id.indoor_destination_location_text_view);
        indoor_instructions_sections = (TextView) findViewById(R.id.indoor_instructions_sections);

        Intent intent = getIntent();
        boolean is_building = intent.getBooleanExtra("is_building", false);
        start_id = getIntent().getStringExtra("start_id");
        end_id = getIntent().getStringExtra("end_id");

        if (is_building) {
            Log.i(TAG, "is building");
            startPoint = new double[]{TripSetupActivity.getStartingBuilding().getLatitude(), TripSetupActivity.getStartingBuilding().getLongitude()};
            endPoint = new double[]{TripSetupActivity.getDestinationBuilding().getLatitude(), TripSetupActivity.getDestinationBuilding().getLongitude()};
            Log.i(TAG, "endPoint: "+endPoint[0]+","+endPoint[1]);
            Log.i(TAG, "startPoint: "+startPoint[0]+","+startPoint[1]);

            indoor_current_location_text_view.setText("Current building: "+TripSetupActivity.getStartingBuilding().getBuilding_Number());
            indoor_destination_location_text_view.setText("Destination building: "+TripSetupActivity.getDestinationBuilding().getBuilding_Number());
        } else {
            startPoint = new double[]{TripSetupActivity.getStartingOutdoorLocation().longitude,TripSetupActivity.getStartingOutdoorLocation().latitude};
            endPoint = new double[]{TripSetupActivity.getDestinationOutdoorLocation().longitude,TripSetupActivity.getDestinationOutdoorLocation().latitude};

            indoor_current_location_text_view.setText("Current building: "+TripSetupActivity.getStartingOutdoorLocation().longitude);
            indoor_destination_location_text_view.setText("Current building: "+TripSetupActivity.getDestinationOutdoorLocation().longitude);
        }
        indoor_instructions_sections.setText(DefaultValues.FOLLOW_PATH_TO_BUILDING);

        final MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override public void onMapReady(MapzenMap map_) {
                // Map is ready.
                map =  map_;
                //map.setPosition(new LngLat(25.66841,-34.00910));
                map.setPosition(new LngLat(startPoint[1],startPoint[0]));
                map.setRotation(0f);
                map.setZoom(17f);
                map.setTilt(0f);
                map.setMyLocationEnabled(true);
                Log.i(TAG, "done drawing map");
            }
        });

        Log.i(TAG, "router set");
        router = new MapzenRouter(this);
        Log.i(TAG, "router.setWalking mode");
        router.setWalking();
        router.setCallback(new RouteCallback() {
            @Override public void success(final Route route) {
                Log.i(TAG, "drawing route");
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        List<LngLat> points = new ArrayList<>();
                        for (ValhallaLocation v: route.getGeometry())
                            points.add(new LngLat(v.getLongitude(), v.getLatitude()));
                        if (map!=null) {
                            if (points.size() > 0)
                                map.drawRouteLine(points);
                            map.addMarker(new Marker(points.get(0).longitude, points.get(0).latitude));
                            map.addMarker(new Marker(points.get(points.size()-1).longitude, points.get(points.size()-1).latitude));
                        }
                        return null;
                    }
                }.execute();

            }

            @Override public void failure(int i) {
                //Handle failure
                Toast.makeText(OutdoorMapActivity.this, "Unable to load route", Toast.LENGTH_LONG).show();
            }
        });
        Log.i(TAG, "starting point set");
        router.setLocation(startPoint);
        Log.i(TAG, "end point set");
        router.setLocation(endPoint);
        Log.i(TAG, "fetching route");
        router.fetch();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.outdoor_menu, menu);
        invalidateOptionsMenu();
        MenuItem item1 = menu.findItem(R.id.outdoor_door_number_menu_item);
        item1.setVisible(false);
        return true;
    }

    private boolean isPOIClicked = false, isFavClicked = false;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem checkable = menu.findItem(R.id.outdoor_show_poi_menu_item);
        checkable.setChecked(isPOIClicked);
        MenuItem favCheckable = menu.findItem(R.id.outdoor_show_favourites_menu_item);
        favCheckable.setChecked(isFavClicked);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User chose a menu item
        Intent intent;
        switch (item.getItemId()) {
            case R.id.outdoor_qr_code_menu_item:
                // QR code option clicked
                intent = new Intent(getApplicationContext(), QRCodeScannerActivity.class);
                //destination id
                intent.putExtra("start_id", start_id); // door id is the new start id
                intent.putExtra("end_id", end_id); // final destination remains the same
                intent.putExtra("stairs1","");
                intent.putExtra("stairs2","");
                intent.putExtra("startedByOutdoor", true);

                if (map != null)
                    if (map.isMyLocationEnabled()) {
                        map.setMyLocationEnabled(false);
                        enableLocationOnResume = true;
                    }

                startActivityForResult(intent, SCAN_QR_CODE_ID);
                return true;
            case R.id.outdoor_door_number_menu_item:
                // Door number scanner
                intent = new Intent(getApplicationContext(), IndoorNumberScannerActivity.class);
                intent.putExtra("building_floor_id", "");
                startActivity(intent);
                return true;
            case R.id.outdoor_show_favourites_menu_item:
                // Show favourites
                displayFavs(item);
                Toast.makeText(this, "Favourites", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.outdoor_show_poi_menu_item:
                // Show pois
                displayPOIs(item);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void displayPOIs(MenuItem item) {
        if(!item.isChecked())
            for (POI p : HomeActivity.db.getPOIs()) {
                if (p.determinePOIKind().equals("building")) {
                    Building b = HomeActivity.db.getBuilding(p.getBuilding_Number());
                    map.addMarker(new Marker(b.getLongitude(), b.getLatitude()));
                }
            }
        else
            map.removeMarker();

        isPOIClicked = !item.isChecked();
    }
    private void displayFavs(MenuItem item) {
        if(!item.isChecked())
            for (String s : HomeActivity.db.getFavPOIIDs()) {
                POI p = HomeActivity.db.getPOI(s);
                if (p.determinePOIKind().equals("building")) {
                    Building b = HomeActivity.db.getBuilding(p.getBuilding_Number());
                    map.addMarker(new Marker(b.getLongitude(), b.getLatitude()));
                }
            }
        else
            map.removeMarker();
        isFavClicked = !item.isChecked();
    }
    @Override protected void onPause() {
        super.onPause();
        if (map != null)
            if (map.isMyLocationEnabled()) {
                map.setMyLocationEnabled(false);
                enableLocationOnResume = true;
        }
    }
    @Override protected void onResume() {
        super.onResume();
        if (map != null)
            if (enableLocationOnResume) {
                map.setMyLocationEnabled(true);
            }
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(OutdoorMapActivity.this)
                .setTitle("Confirmation")
                .setMessage("Do you want to exit Indoor Map")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TripSetupActivity.btn_unknown_current.setTextColor(Color.WHITE);
                        TripSetupActivity.btn_unknown_destination.setTextColor(Color.WHITE);
                        TripSetupActivity.btn_unknown_destination.setEnabled(false);
                        setResult(RESULT_CANCELED);
                        map.setMyLocationEnabled(false);
                        finish();
                    }})
                .create()
                .show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SCAN_QR_CODE_ID){
            if (resultCode == RESULT_OK){
                boolean close_act = data.getBooleanExtra("close_act", false);
                if (close_act){
                    Intent intent = new Intent(OutdoorMapActivity.this, TripSetupActivity.class);
                    intent.putExtra("start_id", start_id);
                    intent.putExtra("end_id", end_id);
                    intent.putExtra("qr_code_id", data.getStringExtra("qr_code_id"));
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        }
    }
}
