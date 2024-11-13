package com.example.step_tracking;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = "NotificationReceiver";
    private static int notificationIdCounter = 100; // Starting ID for notifications

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive called");

        // Access step count
        SharedPreferencesHelper preferencesHelper = new SharedPreferencesHelper(context);
        int stepCount = preferencesHelper.getStepCount();

        String message;
        if (stepCount < 200) {
            message = "You have taken less than 200 steps. Please take some steps.";
        } else if (stepCount > 1500) {
            message = "Great job! You've taken over 1500 steps. Take a rest.";
        } else {
            message = "Keep it up! You've taken " + stepCount + " steps.";
        }

        // Assign a unique notification ID
        int notificationId = notificationIdCounter++;
        if (notificationId < 0) {
            notificationIdCounter = 100;
            notificationId = notificationIdCounter++;
        }

        // Store sent time with notification ID
        String sentTime = getCurrentTime();
        preferencesHelper.setSentTime(notificationId, sentTime);

        // Create intent for OK action
        Intent okIntent = new Intent(context, ActionReceiver.class);
        okIntent.setAction("OK_ACTION");
        okIntent.putExtra("notification_id", notificationId);
        PendingIntent okPendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId * 2, // Unique request code
                okIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Create intent for Dismiss action
        Intent dismissIntent = new Intent(context, ActionReceiver.class);
        dismissIntent.setAction("DISMISS_ACTION");
        dismissIntent.putExtra("notification_id", notificationId);
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId * 2 + 1, // Unique request code
                dismissIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "STEP_TRACKER_CHANNEL")
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Ensure this icon exists
                .setContentTitle("Step Tracker Alert")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{0, 1000, 500, 1000}) // Vibration pattern
                .setOngoing(false) // Allows dismissal
                .setAutoCancel(true) // Auto-dismiss when tapped
                .addAction(R.drawable.ic_ok, "OK", okPendingIntent) // Ensure these icons exist
                .addAction(R.drawable.ic_dismiss, "Dismiss", dismissPendingIntent); // Ensure these icons exist

        // Show notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(notificationId, builder.build());
            Log.d(TAG, "Notification sent with ID: " + notificationId);
        } else {
            Log.e(TAG, "NotificationManager is null, cannot send notification.");
        }
    }

    // Helper method to get current time in desired format
    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}
