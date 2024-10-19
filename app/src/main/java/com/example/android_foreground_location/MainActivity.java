package com.example.android_foreground_location;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.android_foreground_location.R;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final int REQUEST_NOTIFICATION_PERMISSION = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check and request permissions before starting the service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {  // Android 12+
            checkAndRequestPermissions();
        } else {
            startForegroundServiceIfAllowed();
        }
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
            }
        }

        // Check if fine location, background location, and foreground service permissions are granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {

            // Request all necessary permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                                Manifest.permission.FOREGROUND_SERVICE
                        },
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            // If permissions are already granted, start the foreground service
            startForegroundServiceIfAllowed();
        }
    }

    // Handle the result of permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // Check if all requested permissions were granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startForegroundServiceIfAllowed();
            } else {
                Toast.makeText(this, "Location and foreground service permissions are required.", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Start the foreground service if permissions are granted
    private void startForegroundServiceIfAllowed() {
        Intent serviceIntent = new Intent(this, MyForegroundService.class);

        // For Android 8.0+ (Oreo and above), use startForegroundService
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);  // For older versions
        }
    }

    // Check if the service is already running
    public boolean isServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (MyForegroundService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void stopForegroundServiceManually(View view) {
        Intent serviceIntent = new Intent(this, MyForegroundService.class);
        stopService(serviceIntent);  // Stop the foreground service
        Toast.makeText(this, "Foreground service stopped", Toast.LENGTH_SHORT).show();
    }

    //    // Method to check and request necessary location and foreground service permissions
//    private void checkAndRequestPermissions() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
//                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED ||
//                ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {
//
//            // Request necessary permissions
//            ActivityCompat.requestPermissions(this,
//                    new String[]{
//                            Manifest.permission.ACCESS_FINE_LOCATION,
//                            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
//                            Manifest.permission.FOREGROUND_SERVICE,
//                    },
//                    LOCATION_PERMISSION_REQUEST_CODE);
//        } else {
//            // Start the foreground service if permissions are granted
//            startForegroundService();
//        }
//    }

    // Handle the result of permission requests
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                startForegroundService();
//            } else {
//                Toast.makeText(this, "Location and foreground service permissions are required.", Toast.LENGTH_LONG).show();
//            }
//        }
//    }

//    // Method to start the foreground service
//    private void startForegroundService() {
//        Intent serviceIntent = new Intent(this, MyForegroundService.class);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(serviceIntent);  // For Android 8.0+ (API 26+)
//        } else {
//            startService(serviceIntent);  // For older versions
//        }
//    }
}
