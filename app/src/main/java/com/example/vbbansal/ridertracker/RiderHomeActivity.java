package com.example.vbbansal.ridertracker;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

    public final static String KEY_TRACKER_NAME = "trackerName";
    public final static String KEY_TRACKER_NUMBER = "trackerNumber";
    public final static String KEY_LOCATION_SHARING_FREQUENCY = "locationSharingFrequency";

    private final static int PICK_CONTACT_REQUEST_CODE = 1;

    private String trackerName;
    private String trackerNumber;
    private int frequencyInMinute;
    private boolean isLocationSharingOn;

    private AlarmManager alarmMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_home);

        alarmMgr = (AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public void onResume() {
        super.onResume();

        trackerName = getSharedPreferences().getString(KEY_TRACKER_NAME, null);
        trackerNumber = getSharedPreferences().getString(KEY_TRACKER_NUMBER, null);
        frequencyInMinute = getSharedPreferences().getInt(KEY_LOCATION_SHARING_FREQUENCY, 30);
        isLocationSharingOn = getNullableAlarmPendingIntent() != null;

        //setup activity to previous state
        setupActivity();
    }

    @Override
    public void onPause() {
        updateSharedPreferences();

        super.onPause();
    }

    private void updateSharedPreferences() {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(KEY_TRACKER_NAME, trackerName);
        editor.putString(KEY_TRACKER_NUMBER, trackerNumber);
        editor.putInt(KEY_LOCATION_SHARING_FREQUENCY, frequencyInMinute);
        editor.commit();
    }

    private SharedPreferences getSharedPreferences() {
        return this.getPreferences(Context.MODE_PRIVATE);
    }

    public void startLocationSharing(View view) {
        frequencyInMinute = Integer.parseInt(((TextView) findViewById(R.id.locationSharingFrequencyEditText)).getText().toString());
        // Sender address has already been set in Contact Picker activity call-back !

        shareLocation();
    }

    private void shareLocation() {
        if (LocationHelper.isLocationPermissionAvailable(this) && SMSHelper.isSendSmsPermissionAvailable(this)) {
            alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), frequencyInMinute * 60 *
                    1000, getAlarmPendingIntent());

            isLocationSharingOn = true;

            setupActivity();

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

    private PendingIntent getAlarmPendingIntent() {
        Intent intent = new Intent(this, LocationSharingAlarm.class);
        intent.putExtra(Constants.ADDRESS_TO_SHARE_LOCATION, trackerNumber);
        return PendingIntent.getBroadcast(this, 0, intent, 0);
    }

    public void stopLocationSharing(View view) {
        if (getNullableAlarmPendingIntent() != null && alarmMgr != null) {
            alarmMgr.cancel(getNullableAlarmPendingIntent());
            getNullableAlarmPendingIntent().cancel();

            isLocationSharingOn = false;
            setupActivity();
        }
    }

    private PendingIntent getNullableAlarmPendingIntent() {
        Intent intent = new Intent(this, LocationSharingAlarm.class);
        intent.putExtra(Constants.ADDRESS_TO_SHARE_LOCATION, trackerNumber);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_NO_CREATE);
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
                    Cursor c = managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String name = c.getString(c.getColumnIndexOrThrow(
                                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        String number = c.getString(c.getColumnIndexOrThrow(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));

                        trackerNumber = number;
                        trackerName = name;

                        updateSharedPreferences();
                        setupStartLocationSharingButton();
                    }
                }

                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    private void setupActivity() {
        setupTextView1();
        setupChooseContactLayout();
        setupLocationSharingFrequency();
        setupStartLocationSharingButton();
        setupStopLocationSharingButton();
    }

    private void setupTextView1() {
        if (isLocationSharingOn) {
            ((TextView) findViewById(R.id.riderHomeTextView1)).setText(
                    getString(R.string.rider_home_start_location_share_on_text_view)
            );
        } else {
            ((TextView) findViewById(R.id.riderHomeTextView1)).setText(
                    getString(R.string.rider_home_start_location_share_off_text_view)
            );
        }
    }

    private void setupChooseContactLayout() {
        if (trackerName != null) {
            ((Button) findViewById(R.id.contactPickerButton)).setText(
                    getString(R.string.rider_home_choose_another_contact));
            ((TextView) findViewById(R.id.chosenContactTextView)).setText(trackerName + " or");

            findViewById(R.id.chosenContactTextView).setVisibility(View.VISIBLE);
        } else {
            ((Button) findViewById(R.id.contactPickerButton)).setText(
                    getString(R.string.rider_home_choose_contact));

            findViewById(R.id.chosenContactTextView).setVisibility(View.INVISIBLE);
        }

        if (isLocationSharingOn) {
            ViewHelper.disableComponent(findViewById(R.id.contactPickerButton));
        } else {
            ViewHelper.enableComponent(findViewById(R.id.contactPickerButton));
        }

    }

    private void setupLocationSharingFrequency() {
        ((TextView) findViewById(R.id.locationSharingFrequencyEditText)).setText(String.valueOf(frequencyInMinute));

        if (isLocationSharingOn) {
            ViewHelper.disableComponent(findViewById(R.id.locationSharingFrequencyEditText));
        } else {
            ViewHelper.enableComponent(findViewById(R.id.locationSharingFrequencyEditText));
        }
    }

    private void setupStartLocationSharingButton() {
        if (isLocationSharingOn == false && trackerName != null && frequencyInMinute > 0) {
            ViewHelper.enableComponent(findViewById(R.id.startLocationSharingButton));
        } else {
            ViewHelper.disableComponent(findViewById(R.id.startLocationSharingButton));
        }
    }

    private void setupStopLocationSharingButton() {
        if (isLocationSharingOn) {
            ViewHelper.enableComponent(findViewById(R.id.stopLocationSharingButton));
        } else {
            ViewHelper.disableComponent(findViewById(R.id.stopLocationSharingButton));
        }
    }
}