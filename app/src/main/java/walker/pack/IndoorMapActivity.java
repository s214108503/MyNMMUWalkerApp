package walker.pack;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.DoubleBuffer;
import java.util.ArrayList;

import walker.pack.classes.DefaultValues;
import walker.pack.classes.POI;
import walker.pack.classes.Staff;
import walker.pack.classes.Venue;
import walker.pack.customviews.AnimationView;
import walker.pack.interfaces.IndoorChangeFloorsInterface;

public class IndoorMapActivity extends AppCompatActivity {

    AnimationView animationView_layout_view;
    TextView indoor_current_location_text_view, indoor_destination_location_text_view, indoor_instructions_sections;
    String start_id, end_id, final_end_id, stairs1, stairs2;
    private String TAG ="IndoorMapAct";

    private final static int SCAN_DOOR_NUMBER_ID = 8, SCAN_QR_CODE_ID = 9;

    private IndoorChangeFloorsInterface listener = new IndoorChangeFloorsInterface() {
        @Override
        public void OnChangeFloorsCalled(String qr) {
            Bitmap background_bitmap;

            if (qr.indexOf("4_00") != -1) {
                //background_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b4_00);
                animationView_layout_view.setImageResource( R.drawable.b4_00);
            } else if (qr.indexOf("9_00") != -1) {
                //background_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b9_00);
                animationView_layout_view.setImageResource( R.drawable.b9_00);
            } else if (qr.indexOf("9_01") != -1) {
                //background_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b9_01);
                animationView_layout_view.setImageResource( R.drawable.b9_01);
            } else if (qr.indexOf("9_02") != -1) {
                //background_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b9_02);
                animationView_layout_view.setImageResource( R.drawable.b9_02);
            } else {
                //background_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.white_background);
                animationView_layout_view.setImageResource( R.drawable.white_background);
            }

            animationView_layout_view.setScaleType(ImageView.ScaleType.MATRIX);

            if (final_end_id != null) {
                Toast.makeText(IndoorMapActivity.this, "Final: " + final_end_id, Toast.LENGTH_SHORT).show();
            }
            //Toast.makeText(IndoorMapActivity.this, "end: " + end_id, Toast.LENGTH_SHORT).show();
            animationView_layout_view.drawDestination = true;
            if (end_id!=null)
                if (end_id.length() > 0 && end_id.split("_").length > 1)
                    Toast.makeText(IndoorMapActivity.this, "Goto level: "+end_id.split("_")[1], Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);
        start_id = getIntent().getStringExtra("start_id");
        end_id = getIntent().getStringExtra("end_id");
        final_end_id = getIntent().getStringExtra("final_end_id");
        stairs1 = getIntent().getStringExtra("stairs1");
        stairs2 = getIntent().getStringExtra("stairs2");
        indoor_current_location_text_view = (TextView) findViewById(R.id.indoor_current_location_text_view);
        indoor_destination_location_text_view = (TextView) findViewById(R.id.indoor_destination_location_text_view);
        indoor_instructions_sections = (TextView) findViewById(R.id.indoor_instructions_sections);
        indoor_current_location_text_view.setText(start_id);
        if (final_end_id==null) indoor_destination_location_text_view.setText(end_id);
        else indoor_destination_location_text_view.setText(final_end_id);
        animationView_layout_view = (AnimationView) findViewById(R.id.animation_view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.indoor_map_menu, menu);
        if (start_id.split("_")[1].equals(end_id.split("_")[1])) {
            invalidateOptionsMenu();
            MenuItem item_change_floors = menu.findItem(R.id.indoor_map_change_floors_menu_item);
            item_change_floors.setVisible(false);
            indoor_instructions_sections.setText(DefaultValues.FOLLOW_PATH_TO_DESTINATION);
        } else {
            indoor_instructions_sections.setText(DefaultValues.FOLLOW_PATH_TO_STAIRWAY);
        }
        return true;
    }

    private boolean show_favourites = false, show_pois = false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User chose a menu item
        Intent intent;
        switch (item.getItemId()) {

            case R.id.indoor_map_qr_code_menu_item:
                // QR code option clicked
                intent = new Intent(getApplicationContext(), QRCodeScannerActivity.class);
                //destination id
                intent.putExtra("start_id", end_id); // door id is the new start id
                intent.putExtra("end_id", final_end_id); // final destination remains the same
                intent.putExtra("stairs1",stairs1);
                intent.putExtra("stairs2",stairs2);
                startActivityForResult(intent, SCAN_QR_CODE_ID);
                return true;
            case R.id.indoor_map_door_number_menu_item:
                // Door number scanner
                intent = new Intent(getApplicationContext(), IndoorNumberScannerActivity.class);
                intent.putExtra("building_floor_id", start_id.substring(0,start_id.lastIndexOf("_")));
                startActivityForResult(intent, SCAN_DOOR_NUMBER_ID);
                return true;
            case R.id.indoor_map_show_favourites_menu_item:
                // Show favourites
                show_favourites = !show_favourites;
                placeFavouritesOnScreen(show_favourites);
                item.setChecked(show_favourites);
                return true;
            case R.id.indoor_map_show_pois_menu_item:
                // Show pois
                show_pois = !show_pois;
                placePOIsonScreen(show_pois);
                item.setChecked(show_pois);
                return true;
            case  R.id.indoor_map_change_floors_menu_item:
                indoor_instructions_sections.setText(DefaultValues.FOLLOW_PATH_TO_EXIT);
                listener.OnChangeFloorsCalled(end_id);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void placeFavouritesOnScreen(boolean show_favourites) {
        ArrayList<String> venue_ids = HomeActivity.db.getFavVenueID(),
                staff_ids = HomeActivity.db.getFavStaffIDs();
        ArrayList<Double[]> favourites_positions = new ArrayList<>();
        String[] end_id_array = end_id.split("_"), start_id_array = start_id.split("_");

        for (Venue v : HomeActivity.db.getVenues()) {
            if (venue_ids.indexOf(v.getBuildingFloorDoorID()) != -1) {
                if (animationView_layout_view.drawDestination) {
                    if (v.getBuilding_Number().equals(end_id_array[0]) && v.getFloor_Number().equals(end_id_array[1])) {
                        favourites_positions.add(new Double[]{v.getLatitude(), v.getLongitude()});
                    }
                } else {
                    if (v.getBuilding_Number().equals(start_id_array[0]) && v.getFloor_Number().equals(start_id_array[1]))
                        favourites_positions.add(new Double[]{v.getLatitude(), v.getLongitude()});
                }
            }
        }
        for (Staff s : HomeActivity.db.getStaffMembers()) {
            if (staff_ids.indexOf(s.getStaff_ID()) != -1) {
                Venue v = HomeActivity.db.getVenue(s.getDoor_ID(), s.getFloor_Number(), s.getBuilding_Number());
                if (animationView_layout_view.drawDestination) {
                    if (v.getBuilding_Number().equals(end_id_array[0]) && v.getFloor_Number().equals(end_id_array[1]))
                        favourites_positions.add(new Double[]{v.getLatitude(), v.getLongitude()});
                } else {
                    if (v.getBuilding_Number().equals(start_id_array[0]) && v.getFloor_Number().equals(start_id_array[1]))
                        favourites_positions.add(new Double[]{v.getLatitude(), v.getLongitude()});
                }
            }
        }
        animationView_layout_view.favourites = favourites_positions;

        animationView_layout_view.drawFavourites = show_favourites;
        animationView_layout_view.invalidate();

    }

    private void placePOIsonScreen(boolean show_pois){
        ArrayList<POI> temp_pois = HomeActivity.db.getPOIs();
        ArrayList<Double[]> xys = new ArrayList<>();
        String[] end_id_array = end_id.split("_"), start_id_array = start_id.split("_");
        for (POI poi: temp_pois) {
            if (poi.determinePOIKind().equals("venue")){
                Venue v = HomeActivity.db.getVenue(poi.getDoor_ID(), poi.getFloor_Level(), poi.getBuilding_Number());
                if (v!=null)
                {
                    if (animationView_layout_view.drawDestination) {
                        if (v.getBuilding_Number().equals(end_id_array[0]) && v.getFloor_Number().equals(end_id_array[1])) {
                            xys.add(new Double[]{v.getLatitude(), v.getLongitude()});
                        }
                    } else {
                        if (v.getBuilding_Number().equals(start_id_array[0]) && v.getFloor_Number().equals(start_id_array[1]))
                            xys.add(new Double[]{v.getLatitude(), v.getLongitude()});
                    }
                }
            }
        }
        animationView_layout_view.pois = xys;
        animationView_layout_view.drawPOIs = show_pois;
        animationView_layout_view.invalidate();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(IndoorMapActivity.this)
                .setTitle("Confirmation")
                .setMessage("Do you want to exit Indoor Map")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TripSetupActivity.btn_unknown_current.setTextColor(Color.WHITE);
                        TripSetupActivity.btn_unknown_destination.setTextColor(Color.WHITE);
                        TripSetupActivity.btn_unknown_destination.setEnabled(false);
                        if (animationView_layout_view != null) {
                            animationView_layout_view.clearCanvas = true;
                            animationView_layout_view.clear();
                            animationView_layout_view.invalidate();
                        }
                        setResult(RESULT_CANCELED);
                        finish();
                    }})
                .create()
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SCAN_DOOR_NUMBER_ID){
            if (resultCode==RESULT_OK){
                boolean close_act = data.getBooleanExtra("close_act", false);
                if (close_act) {
                    if (animationView_layout_view != null) {
                        animationView_layout_view.clearCanvas = true;
                        animationView_layout_view.clear();
                        animationView_layout_view.invalidate();
                    }
                    //animationView_layout_view = new AnimationView(getBaseContext());
                    listener.OnChangeFloorsCalled("nothing");
                    Intent intent = new Intent(IndoorMapActivity.this, TripSetupActivity.class);
                    intent.putExtra("outdoor_mode", data.getBooleanExtra("outdoor_mode", false));
                    setResult(RESULT_OK, intent);
                    Log.i(TAG, "Door ID Results set to OK");
                    finish();

                }
            }
        } else if (requestCode == SCAN_QR_CODE_ID){
            if (resultCode==RESULT_OK){
                Boolean close_act = data.getBooleanExtra("close_act", false);
                Boolean update_plan = data.getBooleanExtra("update_plan", false);
                if (!close_act){
                    if (update_plan) {
                        listener.OnChangeFloorsCalled(end_id);
                    }
                    else
                        (Snackbar.make(animationView_layout_view, "Incorrect option chosen",Snackbar.LENGTH_SHORT)).show();
                } else {
                    Intent intent = new Intent(IndoorMapActivity.this, TripSetupActivity.class);
                    setResult(RESULT_OK, intent);
                    Log.i(TAG, "QR Code Results set to OK");
                    if (animationView_layout_view != null) {
                        animationView_layout_view.clearCanvas = true;
                        animationView_layout_view.clear();
                        animationView_layout_view.invalidate();
                    }
                    finish();
                }
            }
        }
    }
}
