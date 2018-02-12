package com.example.vbbansal.ridertracker;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.vbbansal.ridertracker.bo.LocationSharingAlarm;
import com.example.vbbansal.ridertracker.helper.Constants;
import com.example.vbbansal.ridertracker.helper.LocationHelper;
import com.example.vbbansal.ridertracker.helper.SMSHelper;
import com.example.vbbansal.ridertracker.helper.ViewHelper;

public class RiderHomeActivity extends AppCompatActivity {

    private String senderAddress;
    private int frequencyInMinute;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private final static int PICK_CONTACT_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_home);

        ViewHelper.disableComponent(findViewById(R.id.startLocationSharingButton));
        ViewHelper.disableComponent(findViewById(R.id.stopLocationSharingButton));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LocationHelper.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    shareLocation();

                } else {
                    //TODO : handle gracefully
                }
                return;
            }

            case SMSHelper.MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    shareLocation();

                } else {
                    //TODO : handle gracefully
                }
                return;
            }
        }
    }

    public void startLocationSharing(View view) {
        frequencyInMinute = Integer.parseInt(((TextView) findViewById(R.id.locationSharingFrequencyEditText)).getText().toString());
        // Sender address has already been set in Contact Picker activity call-back !

        shareLocation();
    }

    private void shareLocation() {
        if (LocationHelper.isLocationPermissionAvailable(this) && SMSHelper.isSendSmsPermissionAvailable(this)) {
            alarmMgr = (AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(this, LocationSharingAlarm.class);
            intent.putExtra(Constants.ADDRESS_TO_SHARE_LOCATION, senderAddress);
            alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

            alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), frequencyInMinute * 60 * 1000, alarmIntent);

            ((TextView) findViewById(R.id.riderHomeTextView1)).setText(
                    getString(R.string.rider_home_start_location_share_on_text_view)
            );

            ViewHelper.disableComponent(findViewById(R.id.startLocationSharingButton));
            ViewHelper.disableComponent(findViewById(R.id.locationSharingFrequencyEditText));
            ViewHelper.disableComponent(findViewById(R.id.contactPickerButton));
            ViewHelper.enableComponent(findViewById(R.id.stopLocationSharingButton));

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

    public void stopLocationSharing(View view) {
        if (alarmMgr != null) {
            alarmMgr.cancel(alarmIntent);

            ((TextView) findViewById(R.id.riderHomeTextView1)).setText(
                    getString(R.string.rider_home_start_location_share_off_text_view)
            );

            ViewHelper.disableComponent(findViewById(R.id.stopLocationSharingButton));
            ViewHelper.enableComponent(findViewById(R.id.startLocationSharingButton));
            ViewHelper.enableComponent(findViewById(R.id.locationSharingFrequencyEditText));
            ViewHelper.enableComponent(findViewById(R.id.contactPickerButton));
        }
    }

    public void selectContact(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case PICK_CONTACT_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c =  managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String name = c.getString(c.getColumnIndexOrThrow(
                                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        String number = c.getString(c.getColumnIndexOrThrow(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));

                        ((Button) findViewById(R.id.contactPickerButton)).setText(
                                getString(R.string.rider_home_choose_another_contact));

                        ((TextView)findViewById(R.id.chosenContactTextView)).setText(name + " or");
                        findViewById(R.id.chosenContactTextView).setVisibility(View.VISIBLE);

                        senderAddress = number;

                        ViewHelper.enableComponent(findViewById(R.id.startLocationSharingButton));
                    }
                }
        }
    }
}