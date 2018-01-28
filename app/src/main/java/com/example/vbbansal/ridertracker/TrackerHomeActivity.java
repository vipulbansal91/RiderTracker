package com.example.vbbansal.ridertracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import static com.example.vbbansal.ridertracker.helper.Constants.LATITUDE;
import static com.example.vbbansal.ridertracker.helper.Constants.LONGITUDE;

public class TrackerHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker_home);
    }

    public void displaySampleLocationOnMap(View view) {
        double lat = 12.9737292;
        double lang = 77.6921404;

        startShowLocationActivity(lat, lang);
    }

    private void startShowLocationActivity(double lat, double lang) {
        Intent intent = new Intent(this, ShowRiderLocationActivity.class);
        intent.putExtra(LATITUDE, lat);
        intent.putExtra(LONGITUDE, lang);
        startActivity(intent);
    }
}
