package walker.pack;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

import walker.pack.interfaces.OCRDoorIDLocationUpdateInterface;
import walker.pack.ocr.things.TextDetectorProcessor;

public class IndoorNumberScannerActivity extends AppCompatActivity implements OCRDoorIDLocationUpdateInterface{
    final static String TAG = "DoorScannerAct";

    private SurfaceView door_number_surface_view;
    private Button btn_door_number_confirm;
    private EditText door_number_edit_text;
    private CameraSource camera_src;

    private final int REQUEST_CAMERA_PERMISSION_ID = 5;
    private final int UPDATE_GOOGLE_SERVICES_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.door_number_layout);

        door_number_surface_view = (SurfaceView) findViewById(R.id.door_number_surface_view);
        btn_door_number_confirm = (Button) findViewById(R.id.btn_door_number_confirm);
        door_number_edit_text = (EditText) findViewById(R.id.door_number_edit_text);

        btn_door_number_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnDoorIDScanned(door_number_edit_text.getText().toString());
            }
        });


        TextRecognizer textRecognizer = new TextRecognizer.Builder(getBaseContext()).build();

        if (!textRecognizer.isOperational()){
            Toast.makeText(this, "Detector dependencies are not yet available", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter low_storage_filter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, low_storage_filter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, "Low Storage", Toast.LENGTH_LONG).show();
                Log.w(TAG, "Low Storage");
            }
        }

        camera_src = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setRequestedFps(2.0f)
                .setAutoFocusEnabled(true)
                .build();

        door_number_surface_view.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                try{
                    if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(IndoorNumberScannerActivity.this,
                                new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION_ID);
                        return;
                    }

                    int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                            getApplicationContext());
                    if (code != ConnectionResult.SUCCESS) {
                        Dialog dlg =
                                GoogleApiAvailability.getInstance().getErrorDialog(IndoorNumberScannerActivity.this, code, UPDATE_GOOGLE_SERVICES_REQUEST);
                        dlg.show();
                    }
                    camera_src.start(surfaceHolder);
                } catch (IOException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                camera_src.stop();
            }
        });

        Intent data = getIntent();
        final String building_floor_id = data.getStringExtra("building_floor_id");
        textRecognizer.setProcessor(new TextDetectorProcessor(getApplicationContext(),door_number_edit_text, building_floor_id));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case  REQUEST_CAMERA_PERMISSION_ID:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                        return;
                    try {
                        camera_src.start(door_number_surface_view.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void OnDoorIDScanned(String door_id) {
        camera_src.stop();
        // Update start location and display changes on indoor plan
        // TODO Check if door id is valid
        if (door_id.split("_").length == 3){
            TripSetupActivity.Clear();
            Intent intent = new Intent(IndoorNumberScannerActivity.this, IndoorMapActivity.class);
            intent.putExtra("close_act", true);
            setResult(RESULT_OK, intent);
            TripSetupActivity.setStart_id(door_id);
            finish();
        }
    }
}
