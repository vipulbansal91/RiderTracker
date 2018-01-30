package com.example.vbbansal.ridertracker.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;

public class SMSHelper {
    public static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 2;

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

    public static void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }
}
