package com.example.vbbansal.ridertracker;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.vbbansal.ridertracker.helper.Constants;
import com.example.vbbansal.ridertracker.helper.SMSHelper;
import com.example.vbbansal.ridertracker.model.Sms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.vbbansal.ridertracker.helper.Constants.LATITUDE;
import static com.example.vbbansal.ridertracker.helper.Constants.LONGITUDE;

public class TrackerHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker_home);

        displayRidersToTrack();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case SMSHelper.MY_PERMISSIONS_REQUEST_READ_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    displayRidersToTrack();

                } else {

                }
                return;
            }
        }
    }

    private void displayRidersToTrack() {
        if (SMSHelper.isReadSmsPermissionAvailable(this)) {
            List<Sms> smsList = SMSHelper.getTMinus24HourSms(this);
            List<Sms> riderTrackerSmsList = filterRiderTrackerSms(smsList);
            Map<String, Sms> riderNumberToLatestSms = getRiderNumberToLatestSms(riderTrackerSmsList);

            addButtonsForRiders(riderNumberToLatestSms);


        } else {
            SMSHelper.requestReadSmsPermission(this);
        }
    }

    private List<Sms> filterRiderTrackerSms(List<Sms> smsList) {
        List<Sms> riderTrackerSmsList = new ArrayList<>();

        for (Sms sms: smsList) {
            if (sms.isRiderTrackerSms()) {
                riderTrackerSmsList.add(sms);
            }
        }

        return riderTrackerSmsList;
    }

    private Map<String, Sms> getRiderNumberToLatestSms(List<Sms> riderTrackerSmsList) {
        Map<String, Sms> riderNumberToLatestSms = new HashMap<>();

        for (Sms sms : riderTrackerSmsList) {
            if (riderNumberToLatestSms.containsKey(sms.getSenderNumber())) {
                if (riderNumberToLatestSms.get(sms.getSenderNumber()).getDate().before(sms.getDate())) {
                    riderNumberToLatestSms.put(sms.getSenderNumber(), sms);
                }
            } else {
                riderNumberToLatestSms.put(sms.getSenderNumber(), sms);
            }
        }

        return riderNumberToLatestSms;
    }

    private void addButtonsForRiders(Map<String, Sms> riderNumberToLatestSms){
        LinearLayout ll = findViewById(R.id.trackerHomeLinerLayout);

        for (Map.Entry<String, Sms> entry : riderNumberToLatestSms.entrySet()) {

            // add button
            Button b = new Button(this);
            b.setText(Constants.TRACK + " " + entry.getKey());
            b.setLayoutParams(getLayoutParams());
            b.setGravity(Gravity.CENTER);
            setOnClick(b, entry.getValue().getLocation());
            ll.addView(b);

        }
    }

    private ViewGroup.LayoutParams getLayoutParams() {
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        //TODO : Add code to center the button. The below code does not make any change
        //layoutParams.setMargins(8,8,8,8);

        return layoutParams;
    }

    private void setOnClick(final Button btn, final Location location){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startShowLocationActivity(location.getLatitude(), location.getLongitude());
            }
        });
    }

    private void startShowLocationActivity(double lat, double lang) {
        Intent intent = new Intent(this, ShowRiderLocationActivity.class);
        intent.putExtra(LATITUDE, lat);
        intent.putExtra(LONGITUDE, lang);
        startActivity(intent);
    }
}
