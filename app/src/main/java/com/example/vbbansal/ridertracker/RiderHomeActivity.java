package com.example.vbbansal.ridertracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.vbbansal.ridertracker.bo.LocationSharingAlarm;
import com.example.vbbansal.ridertracker.helper.Constants;
import com.example.vbbansal.ridertracker.helper.LocationHelper;
import com.example.vbbansal.ridertracker.helper.SMSHelper;

public class RiderHomeActivity extends AppCompatActivity {

    private final static String senderAddress = "+917827754727";
    private final static int frequencyInMinute = 15;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

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

                    shareLocation();

                } else {

                    TextView tv1 = findViewById(R.id.textView);
                    tv1.setText("OOUCH !!");
                }
                return;
            }

            case SMSHelper.MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    shareLocation();

                } else {

                    TextView tv1 = findViewById(R.id.textView);
                    tv1.setText("OOUCH !!");
                }
                return;
            }
        }
    }

    public void startLocationSharing(View view) {
        shareLocation();
    }

    private void shareLocation() {
        if (LocationHelper.isLocationPermissionAvailable(this) && SMSHelper.isSendSmsPermissionAvailable(this)) {
            alarmMgr = (AlarmManager)this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(this, LocationSharingAlarm.class);
            intent.putExtra(Constants.ADDRESS_TO_SHARE_LOCATION, senderAddress);
            alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

            alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), frequencyInMinute*60*1000, alarmIntent);

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
