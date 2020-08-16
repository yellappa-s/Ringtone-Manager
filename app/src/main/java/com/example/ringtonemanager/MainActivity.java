package com.example.ringtonemanager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Intent mIntent;
    boolean per = false;

    @SuppressLint("SourceLockedOrientationActivity")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        askpermission();
        BackgroundService backgroundService = new BackgroundService();
        mIntent = new Intent(getApplicationContext(),BackgroundService.class);
        if(!isMyServiceRunning(backgroundService.getClass())){
            startService(mIntent);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && requestCode == 123) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                per = true;
                askpermission();
            }
            else
            Toast.makeText(MainActivity.this, "Some Permissions Denied", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void askpermission() {
        if (!per) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE,Manifest.permission.FOREGROUND_SERVICE}, 123);
            }
        } else if (!Settings.System.canWrite(this)){
                Intent in = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                in.setData(Uri.parse("package:" + getBaseContext().getPackageName()));
                this.startActivity(in);
            }
        }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void nextactivity(View view) {
        if (Settings.System.canWrite(this) && per){
            Intent intent = new Intent(this, secondActivity.class);
            startActivity(intent);
        } else {
            askpermission();
            Toast.makeText(MainActivity.this, "Grant Permission to proceed", Toast.LENGTH_SHORT).show();
        }
        }

    @Override
    protected void onDestroy() {
        stopService(mIntent);
        super.onDestroy();
    }
}

