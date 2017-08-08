package walker.pack;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import walker.pack.customviews.AnimationView;
import walker.pack.interfaces.IndoorChangeFloorsInterface;

public class IndoorMapActivity extends AppCompatActivity {

    AnimationView animationView_layout_view;
    String start_id, end_id;

    private final static int SCAN_DOOR_NUMBER_ID = 8;

    private IndoorChangeFloorsInterface listener = new IndoorChangeFloorsInterface() {
        @Override
        public void OnChangeFloorsCalled(String qr) {
            if (end_id.indexOf("4_00") != -1) {
                animationView_layout_view.setBackgroundResource(R.drawable.b4_00);
            } else if (end_id.indexOf("9_00") != -1) {
                animationView_layout_view.setBackgroundResource(R.drawable.b9_00);
            } else if (end_id.indexOf("9_01") != -1) {
                animationView_layout_view.setBackgroundResource(R.drawable.b9_01);
            } else if (end_id.indexOf("9_02") != -1) {
                animationView_layout_view.setBackgroundResource(R.drawable.b9_02);
            }
            animationView_layout_view.drawDestination = true;
            if (end_id.length() > 0)
                Toast.makeText(IndoorMapActivity.this, "Goto level: "+end_id.substring(end_id.indexOf("_")+1, end_id.lastIndexOf("_")), Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_drawing);
        animationView_layout_view = new AnimationView(this, getIntent());
        setContentView(animationView_layout_view);
        start_id = getIntent().getStringExtra("start_id");
        end_id = getIntent().getStringExtra("end_id");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.indoor_map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User chose a menu item
        Intent intent;
        switch (item.getItemId()) {

            case R.id.indoor_map_qr_code_menu_item:
                // QR code option clicked
                intent = new Intent(getApplicationContext(), QRCodeScannerActivity.class);
                        //destination id
                startActivity(intent);
                Toast.makeText(this, "QR Code", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.indoor_map_door_number_menu_item:
                // Door number scanner
                 intent = new Intent(getApplicationContext(), IndoorNumberScannerActivity.class);
                intent.putExtra("building_floor_id", start_id.substring(0,start_id.lastIndexOf("_")));
                startActivityForResult(intent, SCAN_DOOR_NUMBER_ID);
                return true;
            case R.id.indoor_map_show_favourites_menu_item:
                // Show favourites
                Toast.makeText(this, "Favourites", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.indoor_map_show_pois_menu_item:
                // Show pois
                Toast.makeText(this, "POI", Toast.LENGTH_SHORT).show();
                return true;
            case  R.id.indoor_map_change_floors_menu_item:
                listener.OnChangeFloorsCalled(end_id);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        new AlertDialog.Builder(IndoorMapActivity.this)
                .setTitle("Confirmation")
                .setMessage("Do you want to exit Indoor Map")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        animationView_layout_view.clear();
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
            boolean close_act = data.getBooleanExtra("close_act", false);
            if (close_act) {
                animationView_layout_view.clear();
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }
}
