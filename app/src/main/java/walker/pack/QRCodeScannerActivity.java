package walker.pack;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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

public class QRCodeScannerActivity extends AppCompatActivity {

    private final static String TAG = "QRCode Scanner Act";
    private SurfaceView qr_code_surface_view;
    private CameraSource camera_src;

    private final int REQUEST_CAMERA_PERMISSION_ID = 5;
    private final int UPDATE_GOOGLE_SERVICES_REQUEST = 9001;

    // TODO QR Code scanner work

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_code_scanner_layout);
        qr_code_surface_view = (SurfaceView) findViewById(R.id.qr_code_surface_view);

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
                for (int x = 0; x < barcodes.size(); x++){
                    Barcode barcode = barcodes.valueAt(x);
                    Log.i(TAG, barcode.rawValue);
                    //Snackbar.make(surfaceView, barcode.displayValue, Snackbar.LENGTH_SHORT);

                    /*QR_ID,
                    Building_Number;
                    DescriptionL;
                    Latitude;
                    Longitude;
                    Floor_Level;*/

                    // Check if ID matches destination's ID,
                    // if it does then update open outdoor map else just update location

                    // ASSUMING USER SCANS CORRECT QR CODE
                    Intent intent = new Intent(getApplicationContext(), OutdoorMapActivity.class);
                    intent.putExtra("is_building", true);
                    startActivity(intent);
                    finish();
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
                .setRequestedFps(2.0f)
                .setAutoFocusEnabled(true)
                .build();

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
}
