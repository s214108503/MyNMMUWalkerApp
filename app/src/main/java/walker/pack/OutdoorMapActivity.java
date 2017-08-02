package walker.pack;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import walker.pack.classes.POI;

public class OutdoorMapActivity extends AppCompatActivity {

    private MapzenMap map;
    MapzenRouter router;
    private boolean enableLocationOnResume = false;
    private static double[] startPoint, endPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.outdoor_map_layout);

        Intent intent = getIntent();
        boolean is_building = intent.getBooleanExtra("is_building", false);

        if (is_building) {
            startPoint = new double[]{TripSetupActivity.STARTING_BUILDING.getLatitude(), TripSetupActivity.STARTING_BUILDING.getLongitude()};
            endPoint = new double[]{TripSetupActivity.DESTINATION_BUILDING.getLatitude(), TripSetupActivity.DESTINATION_BUILDING.getLongitude()};
        } else {
            startPoint = new double[]{TripSetupActivity.STARTING_OUTDOOR_LOCATION.longitude,TripSetupActivity.STARTING_OUTDOOR_LOCATION.latitude};
            endPoint = new double[]{TripSetupActivity.DESTINATION_OUTDOOR_LOCATION.longitude,TripSetupActivity.DESTINATION_OUTDOOR_LOCATION.latitude};
        }

        final MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override public void onMapReady(MapzenMap map_) {
                // Map is ready.
                map =  map_;
                map.setPosition(new LngLat(25.66841, -34.00910));
                map.setRotation(0f);
                map.setZoom(16f);
                map.setTilt(0f);
                map.setMyLocationEnabled(true);
            }
        });

        router = new MapzenRouter(this);
        router.setWalking();
        router.setCallback(new RouteCallback() {
            @Override public void success(Route route) {
                //Do something with route
                //map.drawRouteLine(route.getGeometry().);
                Toast.makeText(OutdoorMapActivity.this, "Route Loaded", Toast.LENGTH_LONG).show();
                List<LngLat> points = new ArrayList<>();
                for (ValhallaLocation v: route.getGeometry())
                    points.add(new LngLat(v.getLongitude(), v.getLatitude()));
                map.drawRouteLine(points);

                for (int x = 0; x < route.getRouteInstructions().size(); x++){
                    Toast.makeText(OutdoorMapActivity.this, route.getRouteInstructions().get(x).toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override public void failure(int i) {
                //Handle failure
                Toast.makeText(OutdoorMapActivity.this, "Unable to load route", Toast.LENGTH_LONG).show();
            }
        });
        router.setLocation(startPoint);
        router.setLocation(endPoint);
        router.fetch();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.outdoor_menu, menu);
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
        switch (item.getItemId()) {
            case R.id.outdoor_qr_code_menu_item:
                // QR code option clicked
                Toast.makeText(this, "QR Code", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.outdoor_door_number_menu_item:
                // Door number scanner
                Intent intent = new Intent(getApplicationContext(), IndoorNumberScannerActivity.class);
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
}
