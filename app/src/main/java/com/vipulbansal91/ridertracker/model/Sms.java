package com.vipulbansal91.ridertracker.model;

import android.location.Location;

import com.vipulbansal91.ridertracker.helper.Constants;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Sms {
    String senderNumber;
    String body;
    Date date;

    private final static String VALID_RIDER_TRACKER_REGEX = "(" + Constants.LATITUDE+ ": )([0-9]*.[0-9]*)(, "+ Constants.LONGITUDE + ": )([0-9]*.[0-9]*)";
    private final static Pattern validRiderTrackerSmsPattern;

    static
    {
        validRiderTrackerSmsPattern = Pattern.compile(VALID_RIDER_TRACKER_REGEX);
    }

    public boolean isRiderTrackerSms() {
        return validRiderTrackerSmsPattern.matcher(this.body).matches();
    }

    public Location getLocation() {
        Matcher matcher = validRiderTrackerSmsPattern.matcher(this.body);
        if (matcher.matches()) {
            double latitude = Double.parseDouble(matcher.group(2));
            double longitude = Double.parseDouble(matcher.group(4));

            Location location =  new Location("RiderSms");
            location.setLatitude(latitude);
            location.setLongitude(longitude);

            return location;
        }

        return null;
    }
}
