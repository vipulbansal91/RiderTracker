package com.example.vbbansal.ridertracker.helper;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;

import com.example.vbbansal.ridertracker.model.Sms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SMSHelper {
    public static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 2;
    public static final int MY_PERMISSIONS_REQUEST_READ_SMS = 3;
    private static final Uri INBOX_URI = Uri.parse("content://sms/inbox");

    public static boolean isSendSmsPermissionAvailable(Context activity) {
        int permissionCheck = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.SEND_SMS);

        return (permissionCheck == PackageManager.PERMISSION_GRANTED);
    }

    public static void requestSendSmsPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.SEND_SMS},
                MY_PERMISSIONS_REQUEST_SEND_SMS);
    }

    public static boolean isReadSmsPermissionAvailable(Context activity) {
        int permissionCheck = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_SMS);

        return (permissionCheck == PackageManager.PERMISSION_GRANTED);
    }

    public static void requestReadSmsPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.READ_SMS},
                MY_PERMISSIONS_REQUEST_READ_SMS);
    }

    public static void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }

    public static List<Sms> getTMinus24HourSms(Context context) {
        List<Sms> smsListToReturn = new ArrayList<>();

        String[] projection = new String[]{ "_id", "address", "body", "date" };
        String selection = "date > '" + DateHelper.getYesterdayDate().getTime() + "'";

        Cursor cursor = context.getContentResolver().query(INBOX_URI, projection, selection,null,"date desc");
        if (cursor.moveToFirst()) {
            do {
                smsListToReturn.add(new Sms(
                        cursor.getString(cursor.getColumnIndex(projection[1])),
                        cursor.getString(cursor.getColumnIndex(projection[2])),
                        new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(projection[3]))))
                ));
            } while (cursor.moveToNext());
        }

        return smsListToReturn;
    }
}
