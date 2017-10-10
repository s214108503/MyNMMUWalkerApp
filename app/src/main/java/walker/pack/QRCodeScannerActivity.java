package walker.pack;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.ArrayList;

import walker.pack.classes.Building;
import walker.pack.classes.Entrance;
import walker.pack.classes.QRCode;
import walker.pack.interfaces.IndoorChangeFloorsInterface;
import walker.pack.interfaces.QRCodeLocationUpdateInterface;

public class QRCodeScannerActivity extends AppCompatActivity implements QRCodeLocationUpdateInterface {

    private final static String TAG = "QRCode Scanner Act";
    private SurfaceView qr_code_surface_view;
    private CameraSource camera_src;
    String start_id, end_id, stairs1, stairs2;
    boolean startedByOutdoor, canProcess = true;
    public AlertDialog dialog;

    private final int REQUEST_CAMERA_PERMISSION_ID = 5;
    private final int UPDATE_GOOGLE_SERVICES_REQUEST = 9001;

    //private QRCodeLocationUpdateInterface location_change_update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_code_scanner_layout);
        qr_code_surface_view = (SurfaceView) findViewById(R.id.qr_code_surface_view);
        start_id = getIntent().getStringExtra("start_id");
        end_id = getIntent().getStringExtra("end_id");
        stairs1 = getIntent().getStringExtra("stairs1");
        stairs2 = getIntent().getStringExtra("stairs2");
        startedByOutdoor = getIntent().getBooleanExtra("startedByOutdoor", false);
        BarcodeDetector detector = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        detector.setProcessor(new Detector.Processor<Barcode>() {

            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SurfaceView surfaceView = qr_code_surface_view;
                //Toast.makeText(getApplicationContext(), "VALUES DETECTED", Toast.LENGTH_SHORT).show();
                SparseArray<Barcode> barcodes = detections.getDetectedItems();
                for (int x = 0; x < barcodes.size(); x++) {
                    Barcode barcode = barcodes.valueAt(x);
                    if (barcode.valueFormat == Barcode.TEXT) {
                        Log.i(TAG, barcode.rawValue);

                            /*QR_ID,
                            Building_Number, {value};
                            Description, {value};
                            Latitude, {value};
                            Longitude, {value};
                            Floor_Level, {value};*/
                        String[] values = barcode.displayValue.split(";");
                        String qr_id = "qr" + values[0].trim().split(",")[1];
                        QRCode qr = HomeActivity.db.getQRCode(qr_id);

                        OnQRCodeScanned(qr);
                    }
                }
            }
        });
        if (!detector.isOperational()) {
            Toast.makeText(this, TAG + ": Detector dependencies are not yet available", Toast.LENGTH_SHORT).show();

            IntentFilter low_storage_filter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, low_storage_filter) != null;
            if (hasLowStorage) {
                Toast.makeText(this, "Low Storage", Toast.LENGTH_LONG).show();
                Log.w(TAG, "Low Storage");
            }
        }

        camera_src = new CameraSource.Builder(getApplicationContext(), detector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setRequestedFps(0.01666666666666666666666666666667f)
                .setAutoFocusEnabled(true)
                .build();

        surfaceViewCallBack();
    }

    private void surfaceViewCallBack() {
        qr_code_surface_view.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(QRCodeScannerActivity.this,
                                new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION_ID);
                        return;
                    }

                    int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                            getApplicationContext());
                    if (code != ConnectionResult.SUCCESS) {
                        Dialog dlg =
                                GoogleApiAvailability.getInstance().getErrorDialog(QRCodeScannerActivity.this, code, UPDATE_GOOGLE_SERVICES_REQUEST);
                        dlg.show();
                    }
                    camera_src.start(surfaceHolder);
                } catch (IOException e) {
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION_ID:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                        return;
                    try {
                        camera_src.start(qr_code_surface_view.getHolder());
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
    public void OnQRCodeScanned(final QRCode qrCode) {
        canProcess = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(QRCodeScannerActivity.this, qrCode.getQR_ID(), Toast.LENGTH_SHORT).show();
            }
        });

        // user in same building
        if (start_id.split("_")[0].equals(qrCode.getBuilding_Number())) {
            // user on same/destination floor
            if (Integer.valueOf(start_id.split("_")[1]) == (qrCode.getFloor_Level()) || !stairs1.isEmpty()&&Integer.valueOf(stairs1.split("_")[1]) == (qrCode.getFloor_Level())) {
                // user scanned correct qr code

                if (startedByOutdoor) {
                    // user is at entrance
                    Intent intent = new Intent(QRCodeScannerActivity.this, OutdoorMapActivity.class);
                    //TripSetupActivity.setStart_id(start_id);
                    //TripSetupActivity.setStart_model("venue");
                    intent.putExtra("close_act", true);
                    intent.putExtra("update_plan", false);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                if (TripSetupActivity.stairs1.getX() == qrCode.getLatitude() && // scan at stairway
                        TripSetupActivity.stairs1.getY() == qrCode.getLongitude() ||
                        TripSetupActivity.stairs2.getX() == qrCode.getLatitude() &&
                                TripSetupActivity.stairs2.getY() == qrCode.getLongitude()) {
                    Intent intent = new Intent(QRCodeScannerActivity.this, IndoorMapActivity.class);
                    intent.putExtra("close_act", false);
                    intent.putExtra("update_plan", true);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                if (TripSetupActivity.DESTINATION_NODE != null){
                    if (TripSetupActivity.DESTINATION_NODE.getX() == qrCode.getLatitude() &&
                            TripSetupActivity.DESTINATION_NODE.getY() == qrCode.getLongitude()){

                        TripSetupActivity.setStart_model("building");
                        Intent intent = new Intent(QRCodeScannerActivity.this, getIntent().getClass());
                        intent.putExtra("close_act", true);
                        intent.putExtra("update_plan", false);
                        intent.putExtra("outdoor_mode", true);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
                // user scanned incorrect qr
                incorrectQRScanned(qrCode);

            } else {
                // user scans qr at exit/entrance
                //Find entrance of start and end
                Entrance start_entrance = HomeActivity.db.getEntranceByLocalXY(qrCode.getLatitude(),
                        qrCode.getLongitude());

                if (start_entrance != null) {
                    Intent intent;
                    if (startedByOutdoor) {
                        intent = new Intent(QRCodeScannerActivity.this, OutdoorMapActivity.class);
                        intent.putExtra("qr_code_id", qrCode.getQR_ID());
                        TripSetupActivity.setStart_model("venue");
                    } else {
                        TripSetupActivity.Clear(); // clears the starting...
                        Building starting_building = HomeActivity.db.getBuilding(String.valueOf(start_entrance.Building));
                        starting_building.setLatitude(start_entrance.Latitude);
                        starting_building.setLongitude(start_entrance.Longitude);
                        TripSetupActivity.setStart_id(start_id);
                        TripSetupActivity.setStart_model("building");
                        TripSetupActivity.setStartingBuilding(starting_building);
                        Log.i(TAG, "starting building and start model set");
                        TripSetupActivity.updateDestinationLocation();
                        intent = new Intent(QRCodeScannerActivity.this, IndoorMapActivity.class);
                    }
                    intent.putExtra("close_act", true);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    incorrectQRScanned(qrCode);
                }
            }

        } else {
            incorrectQRScanned(qrCode);
        }

    }

    private void incorrectQRScanned(final QRCode qrCode) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(QRCodeScannerActivity.this)
                        .setTitle("Confirmation")
                        .setMessage("Incorrect QR Code scanned. Do you want to reroute?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                canProcess = true;
                                TripSetupActivity.setStart_model("qr_code");
                                Intent intent;
                                if (!startedByOutdoor) {
                                    intent = new Intent(QRCodeScannerActivity.this, IndoorMapActivity.class);

                                } else {
                                    intent = new Intent(QRCodeScannerActivity.this, OutdoorMapActivity.class);
                                }
                                intent.putExtra("qr_code_id", qrCode.getQR_ID());
                                intent.putExtra("incorrect_qr_scanned", true);
                                intent.putExtra("close_act", false);
                                intent.putExtra("update_plan", false);
                                setResult(RESULT_CANCELED, intent);
                                finish();

                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                TripSetupActivity.btn_unknown_current.setTextColor(Color.WHITE);
                                TripSetupActivity.btn_unknown_destination.setTextColor(Color.WHITE);
                                TripSetupActivity.btn_unknown_destination.setEnabled(false);
                                TripSetupActivity.setStart_model("qr_code");
                                Intent intent;
                                if (!startedByOutdoor) {
                                    intent = new Intent(QRCodeScannerActivity.this, IndoorMapActivity.class);

                                } else {
                                    intent = new Intent(QRCodeScannerActivity.this, OutdoorMapActivity.class);
                                }
                                intent.putExtra("qr_code_id", qrCode.getQR_ID());
                                intent.putExtra("close_act", true);
                                intent.putExtra("update_plan", false);
                                setResult(RESULT_OK, intent);
                                finish();

                            }
                        })
                        .create()
                        .show();
            }
        });
    }
}
