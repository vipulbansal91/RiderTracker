package com.example.vbbansal.ridertracker;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.vbbansal.ridertracker.helper.LocationHelper;
import com.example.vbbansal.ridertracker.helper.SMSHelper;
import com.google.android.gms.tasks.OnSuccessListener;

public class RiderHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_home);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LocationHelper.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    showRiderLocation();

                } else {

                    TextView tv1 = findViewById(R.id.textView);
                    tv1.setText("OOUCH !!");
                }
                return;
            }

            case SMSHelper.MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    showRiderLocation();

                } else {

                    TextView tv1 = findViewById(R.id.textView);
                    tv1.setText("OOUCH !!");
                }
                return;
            }
        }
    }

    public void showRiderLocation(View view) {
        showRiderLocation();
    }

    private void showRiderLocation() {
        if (LocationHelper.isLocationPermissionAvailable(this) && SMSHelper.isSendSmsPermissionAvailable(this)) {
            LocationHelper.getLastLocationTask(this)
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location lastLocation) {
                            if (lastLocation != null) {
                                String locationString = String.format("Latitude: %s, Longitude: %s", Double.toString(lastLocation.getLatitude()), Double.toString(lastLocation.getLongitude()));

                                SMSHelper.sendSMS("+917827754727", locationString);

                                TextView tv1 = findViewById(R.id.textView);
                                tv1.setText(locationString);
                            } else {
                                TextView tv1 = findViewById(R.id.textView);
                                tv1.setText("OOUCH !!");
                            }

                        }
                    });

        } else {
            if (LocationHelper.isLocationPermissionAvailable(this) == false) {
                LocationHelper.requestLocationPermission(this);
            }

            if (SMSHelper.isSendSmsPermissionAvailable(this) == false) {
                SMSHelper.requestSendSmsPermission(this);
            }
            // later part of showing the rider Location is carried out by onRequestPermissionsResult
        }
    }
}
