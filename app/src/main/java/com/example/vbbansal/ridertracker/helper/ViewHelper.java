package com.example.vbbansal.ridertracker.helper;

import android.view.View;

public class ViewHelper {
    public static void disableComponent(View view) {
        view.setEnabled(false);
        view.setClickable(false);
    }

    public static void enableComponent(View view) {
        view.setEnabled(true);
        view.setClickable(true);
    }
}
