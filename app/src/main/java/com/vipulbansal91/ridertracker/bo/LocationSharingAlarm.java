package com.vipulbansal91.ridertracker.bo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.vipulbansal91.ridertracker.helper.Constants;
import com.vipulbansal91.ridertracker.helper.LocationHelper;
import com.vipulbansal91.ridertracker.helper.SMSHelper;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationSharingAlarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String addressToShareLocation = getAddressToShareLocation(intent);
        shareLocation(context, addressToShareLocation);
    }

    private String getAddressToShareLocation(Intent intent) {
        return intent.getExtras().getString(Constants.ADDRESS_TO_SHARE_LOCATION);
    }

    private void shareLocation(Context context, final String addressToShareLocation) {

        if (LocationHelper.isLocationPermissionAvailable(context) && SMSHelper.isSendSmsPermissionAvailable(context)) {
            LocationHelper.getLastLocationTask(context)
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location lastLocation) {
                            if (lastLocation != null) {
                                String locationString = String.format("Latitude: %s, Longitude: %s", Double.toString(lastLocation.getLatitude()), Double.toString(lastLocation.getLongitude()));

                                SMSHelper.sendSMS(addressToShareLocation, locationString);
                            } else {
                                //TODO : Handle failure gracefully
                            }

                        }
                    });

        } else {
            // TODO : Code to trigger activity to demand permission
        }
    }
}
