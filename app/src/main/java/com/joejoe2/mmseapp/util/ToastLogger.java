package com.joejoe2.mmseapp.util;

import android.app.Activity;
import android.widget.Toast;

public class ToastLogger {
    public static void logOnActivity(final Activity activity, final String msg){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
