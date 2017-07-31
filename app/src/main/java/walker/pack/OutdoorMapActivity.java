package walker.pack;

import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mapzen.android.graphics.MapFragment;
import com.mapzen.android.graphics.MapzenMap;
import com.mapzen.android.graphics.OnMapReadyCallback;
import com.mapzen.android.graphics.model.Marker;
import com.mapzen.android.routing.MapzenRouter;
import com.mapzen.tangram.LngLat;
import com.mapzen.valhalla.Route;
import com.mapzen.valhalla.RouteCallback;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

public class OutdoorMapActivity extends AppCompatActivity {

    private MapzenMap map;
    MapzenRouter router;
    private boolean enableLocationOnResume = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.outdoor_map_layout);

        final MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override public void onMapReady(MapzenMap map_) {
                // Map is ready.
                map =  map_;
                map.setPosition(new LngLat(-34.00910, 25.66841));
                map.setRotation(0f);
                map.setZoom(12f);
                map.setTilt(0f);
                map.setMyLocationEnabled(true);
            }
        });
        router = new MapzenRouter(this);
        router.setWalking();
        router.setCallback(new RouteCallback() {
            @Override public void success(Route route) {
                //Do something with route    
            }

            @Override public void failure(int i) {
                //Handle failure
                
            }
        });
    }

    private void displayPOIs() {
        // display poi on map
        map.addMarker(new Marker(-73.9903, 40.74433));
        map.addMarker(new Marker(-73.984770, 40.734807));
        map.addMarker(new Marker(-73.998674, 40.732172));
        map.addMarker(new Marker(-73.996142, 40.741050));
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
