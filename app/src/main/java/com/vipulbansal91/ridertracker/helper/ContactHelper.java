package com.vipulbansal91.ridertracker.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class ContactHelper {

    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 4;

    public static boolean isReadContactsPermissionAvailable(Context context) {
        int permissionCheck = ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_CONTACTS);

        return (permissionCheck == PackageManager.PERMISSION_GRANTED);
    }

    public static void requestReadContactsPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.READ_CONTACTS},
                MY_PERMISSIONS_REQUEST_READ_CONTACTS);
    }

    public static String getContactName(String contactNumber, Context context) {
        Uri phoneLookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(contactNumber));
        String[] projection = new String[]{ContactsContract.Data.DISPLAY_NAME};

        Cursor cursor = context.getContentResolver().query(phoneLookupUri, projection ,null,null,null);

        if (cursor.moveToFirst()) {
            return cursor.getString(0);
        }

        return null;
    }
}
