package com.example.step_tracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
            Log.d(TAG, "Device rebooted. Rescheduling notifications.");
            // Schedule notifications (use schedule10SecondNotification for testing)
            NotificationHelper.schedule10SecondNotification(context);
//            NotificationHelper.scheduleHourlyNotification(context);
        }
    }
}
