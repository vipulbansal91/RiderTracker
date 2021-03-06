package com.vipulbansal91.ridertracker;

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
import android.widget.TextView;

import com.vipulbansal91.ridertracker.helper.Constants;
import com.vipulbansal91.ridertracker.helper.ContactHelper;
import com.vipulbansal91.ridertracker.helper.SMSHelper;
import com.vipulbansal91.ridertracker.model.Sms;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.vipulbansal91.ridertracker.helper.Constants.LATITUDE;
import static com.vipulbansal91.ridertracker.helper.Constants.LONGITUDE;
import static com.vipulbansal91.ridertracker.helper.Constants.MARKER_TITLE;

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
                    //TODO - Handle gracefully

                }
            }
            case ContactHelper.MY_PERMISSIONS_REQUEST_READ_CONTACTS : {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    displayRidersToTrack();

                } else {
                    //TODO - Handle gracefully
                }
            }
        }
    }

    private void displayRidersToTrack() {
        if (SMSHelper.isReadSmsPermissionAvailable(this) && ContactHelper.isReadContactsPermissionAvailable(this)) {
            List<Sms> smsList = SMSHelper.getTMinus24HourSms(this);
            List<Sms> riderTrackerSmsList = filterRiderTrackerSms(smsList);
            if (riderTrackerSmsList.isEmpty()) {
                addNoRiderView();
            } else {
                Map<String, Sms> riderNumberToLatestSms = getRiderNumberToLatestSms(riderTrackerSmsList);
                Map<String, Sms> riderNameOrNumberToLatestSms = getRiderNameOrNumberToLatestSms(riderNumberToLatestSms);

                addButtonsForRiders(riderNameOrNumberToLatestSms);
            }
        } else {
            if (SMSHelper.isReadSmsPermissionAvailable(this) == false) {
                SMSHelper.requestReadSmsPermission(this);
            }

            if (ContactHelper.isReadContactsPermissionAvailable(this) == false) {
                ContactHelper.requestReadContactsPermission(this);
            }

        }
    }

    private void addNoRiderView() {
        LinearLayout ll = findViewById(R.id.trackerHomeLinerLayout);

        TextView textView = new TextView(this);
        textView.setText("Yet to receive a location update !!");

        ll.addView(textView);
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

    private Map<String, Sms> getRiderNameOrNumberToLatestSms(Map<String, Sms> riderNumberToLatestSms) {
        Map<String, Sms> riderNameOrNumberToLatestSms = new HashMap<>();

        for (Map.Entry<String, Sms> entry : riderNumberToLatestSms.entrySet()) {
            String contactName = ContactHelper.getContactName(entry.getKey(), this);
            if (contactName != null) {
                riderNameOrNumberToLatestSms.put(contactName, entry.getValue());
            } else {
                riderNameOrNumberToLatestSms.put(entry.getKey(), entry.getValue());
            }
        }

        return riderNameOrNumberToLatestSms;
    }

    private void addButtonsForRiders(Map<String, Sms> riderNameOrNumberToLatestSms){
        LinearLayout ll = findViewById(R.id.trackerHomeLinerLayout);

        for (Map.Entry<String, Sms> entry : riderNameOrNumberToLatestSms.entrySet()) {

            // add button
            Button button = new Button(this);
            button.setText(Constants.TRACK + " " + entry.getKey());
            button.setLayoutParams(getLayoutParams());
            button.setGravity(Gravity.CENTER);
            setOnClick(button, entry.getValue().getLocation(), entry.getValue().getDate());
            ll.addView(button);

        }
    }

    private ViewGroup.LayoutParams getLayoutParams() {
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        return layoutParams;
    }

    private void setOnClick(final Button btn, final Location location, final Date date){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startShowLocationActivity(location.getLatitude(), location.getLongitude(), date);
            }
        });
    }

    private void startShowLocationActivity(double lat, double lang, Date date) {
        Intent intent = new Intent(this, ShowRiderLocationActivity.class);
        intent.putExtra(LATITUDE, lat);
        intent.putExtra(LONGITUDE, lang);
        intent.putExtra(MARKER_TITLE, "@ " + new SimpleDateFormat("HH:mm").format(date));
        startActivity(intent);
    }
}
