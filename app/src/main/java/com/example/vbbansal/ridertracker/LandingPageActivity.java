package com.example.vbbansal.ridertracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class LandingPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
    }

    public void showRiderHome(View view) {
        Intent intent = new Intent(this, RiderHomeActivity.class);
        startActivity(intent);
    }
}
