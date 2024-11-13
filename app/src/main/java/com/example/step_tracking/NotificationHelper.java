package com.example.step_tracking;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class NotificationHelper {

    private static final String TAG = "NotificationHelper";

    public static void scheduleHourlyNotification(Context context){
        scheduleRepeatingNotification(context, AlarmManager.INTERVAL_HOUR, "Hourly Notification");
    }

    public static void schedule10SecondNotification(Context context){
        // 10 seconds in milliseconds
        long interval = 10 * 1000;
        scheduleRepeatingNotification(context, interval, "10-Second Notification");
    }

    private static void scheduleRepeatingNotification(Context context, long intervalMillis, String type){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 10); // Initial delay of 10 seconds for testing

        if(alarmManager != null){
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    intervalMillis,
                    pendingIntent
            );
            Log.d(TAG, type + " scheduled to repeat every " + (intervalMillis / 1000) + " seconds.");
        } else {
            Log.e(TAG, "AlarmManager is null, cannot schedule " + type);
        }
    }
}
