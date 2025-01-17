package com.example.step_tracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.NotificationManager;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.app.Activity;
import android.content.ActivityNotFoundException;

public class ActionReceiver extends BroadcastReceiver {

    private static final String TAG = "ActionReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        int notificationId = intent.getIntExtra("notification_id", -1);

        if (notificationId == -1) {
            Toast.makeText(context, "Invalid notification ID.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get current time as response time
        String responseTime = getCurrentTime();

        // Get sent time from SharedPreferences
        SharedPreferencesHelper preferencesHelper = new SharedPreferencesHelper(context);
        String sentTime = preferencesHelper.getSentTime(notificationId);

        // Determine user response
        String userResponse = "Unknown";
        if ("OK_ACTION".equals(action)) {
            userResponse = "OK";
            Toast.makeText(context, "OK clicked", Toast.LENGTH_SHORT).show();

            // Start NotesActivity
            Intent notesIntent = new Intent(context, NotesActivity.class);
            notesIntent.putExtra("notification_id", notificationId);
            notesIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Required to start activity from non-activity context

            try {
                context.startActivity(notesIntent);
                Log.d(TAG, "NotesActivity launched for notification ID: " + notificationId);
            } catch (ActivityNotFoundException e) {
                Log.e(TAG, "NotesActivity not found.", e);
                Toast.makeText(context, "Unable to open Notes.", Toast.LENGTH_SHORT).show();
            }

        } else if ("DISMISS_ACTION".equals(action)) {
            userResponse = "Dismiss";
            Toast.makeText(context, "Dismiss clicked", Toast.LENGTH_SHORT).show();
            // Implement additional logic if needed
        }

        // Log the event to CSV
        CSVLogger csvLogger = new CSVLogger(context);
        csvLogger.logEvent(sentTime, responseTime, userResponse);

        // Remove the sent time from SharedPreferences
        preferencesHelper.removeSentTime(notificationId);

        // Dismiss the notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(notificationId);
            Log.d(TAG, "Notification with ID " + notificationId + " dismissed.");
        }
    }

    // Helper method to get current time in desired format
    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}