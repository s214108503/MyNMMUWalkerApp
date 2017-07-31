package walker.pack;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import walker.pack.classes.Cell;
import walker.pack.customviews.AnimationView;
import walker.pack.interfaces.IndoorQRCodeInterface;

public class IndoorMapActivity extends AppCompatActivity {

    AnimationView animationView_layout_view;
    String the_id;

    private IndoorQRCodeInterface listener = new IndoorQRCodeInterface() {
        @Override
        public void OnQRCodeCalled(String qr) {
            animationView_layout_view.setBackgroundResource(R.drawable.b9_00);
            animationView_layout_view.drawDestination = true;
            Toast.makeText(IndoorMapActivity.this, "Goto level: "+the_id.substring(the_id.indexOf("_")+1, the_id.lastIndexOf("_")), Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_drawing);
        animationView_layout_view = new AnimationView(this, getIntent());
        setContentView(animationView_layout_view);
        the_id = getIntent().getStringExtra("plan_id");
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
        switch (item.getItemId()) {
            case R.id.indoor_map_qr_code_menu_item:
                // QR code option clicked
                Toast.makeText(this, "QR Code", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.indoor_map_door_number_menu_item:
                // Door number scanner
                Intent intent = new Intent(getApplicationContext(), IndoorNumberScannerActivity.class);
                intent.putExtra("building_floor_id", the_id.substring(0,the_id.lastIndexOf("_")));
                startActivity(intent);
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
                listener.OnQRCodeCalled(the_id);
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
}
