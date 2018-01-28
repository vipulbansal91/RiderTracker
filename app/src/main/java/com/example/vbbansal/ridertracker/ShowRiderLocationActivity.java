package com.example.vbbansal.ridertracker;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.example.vbbansal.ridertracker.helper.Constants.LATITUDE;
import static com.example.vbbansal.ridertracker.helper.Constants.LONGITUDE;

public class ShowRiderLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_rider_location);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker to the location to be displayed.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng locationToMark = new LatLng(getIntent().getDoubleExtra(LATITUDE, 0.0),
                getIntent().getDoubleExtra(LONGITUDE, 0.0));
        mMap.addMarker(new MarkerOptions().position(locationToMark).title("Sample"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(locationToMark));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14.0f));
    }
}
