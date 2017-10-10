package walker.pack;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {

    public static final String MyPREFERENCES = "WalkerPREFERENCES";
    public static final int SET_GPS_LOCATION_ACCESS = 6;
    public static final int SET_WRITE_EXTERNAL_STORAGE_ACCESS = 7;

    public static String[] PERMISSIONS_ACCESS_GPS = {android.Manifest.permission.ACCESS_FINE_LOCATION};
    public static String[] PERMISSION_ALLOW_WRITE_TO_PHONE = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public void verifyStoragePermissions(Activity activity){
        int permission = ActivityCompat.checkSelfPermission(activity,
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,
                    PERMISSIONS_ACCESS_GPS, SET_GPS_LOCATION_ACCESS);
        }

        int permission2 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission2 != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity, PERMISSION_ALLOW_WRITE_TO_PHONE, SET_WRITE_EXTERNAL_STORAGE_ACCESS);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final ImageView iv_NMMU_logo = (ImageView) findViewById(R.id.imgNMMULogo);
        final Animation an = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);

        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if (sharedpreferences.getBoolean("FirstTimeUser", true)) {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean("FirstTimeUser", false);
            editor.apply();
            try {
                verifyStoragePermissions(this);
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        iv_NMMU_logo.startAnimation(an);
        an.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                if (getIntent().getStringExtra("starting") != null)
                    i.putExtra("starting", "notification");
                else
                    i.putExtra("starting", "splash");
                startActivity(i);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });



    }
}
