package com.example.vbbansal.ridertracker.helper;

import java.util.Calendar;
import java.util.Date;

public class DateHelper {
    public static Date getYesterdayDate() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -12); //TODO : Change to -1, once testing is done.
        return cal.getTime();
    }
}
