package com.example.step_tracking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final int PERMISSION_REQUEST_ACTIVITY_RECOGNITION = 1001;
    private static final int PERMISSION_REQUEST_POST_NOTIFICATIONS = 1002;
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private int stepCount = 0;
    private int initialSteps = 0;

    // Reference to stepCountTextView
    private TextView stepCountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MainActivity", "onCreate called");

        // Initialize UI elements
        stepCountTextView = findViewById(R.id.stepCountTextView);
        if(stepCountTextView == null){
            Log.e("MainActivity", "stepCountTextView is null!");
            Toast.makeText(this, "UI Error: Step Count TextView not found.", Toast.LENGTH_LONG).show();
        } else {
            Log.d("MainActivity", "stepCountTextView initialized");
        }

        Button openNotesButton = findViewById(R.id.openNotesButton);
        Button openLogsButton = findViewById(R.id.openLogsButton);
        Button viewAllNotesButton = findViewById(R.id.viewAllNotesButton); // New button

        openNotesButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NotesActivity.class);
            startActivity(intent);
        });

        openLogsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LogsActivity.class);
            startActivity(intent);
        });

        viewAllNotesButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ViewNotesActivity.class);
            startActivity(intent);
        });

        // Initialize SensorManager and Step Sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if(sensorManager != null){
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            if(stepSensor != null){
                Log.d("MainActivity", "Step sensor is available.");
            } else {
                Log.e("MainActivity", "Step sensor is NOT available.");
                Toast.makeText(this, "No Step Sensor Available!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("MainActivity", "SensorManager is null.");
            Toast.makeText(this, "Sensor Manager Error!", Toast.LENGTH_SHORT).show();
        }

        // Request ACTIVITY_RECOGNITION permission
        requestActivityRecognitionPermission();

        // Request POST_NOTIFICATIONS permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPostNotificationsPermission();
        }

        // Note: Sensor listener registration and notification scheduling will be handled after permissions are granted

        // Initialize step counts from SharedPreferences
        SharedPreferencesHelper preferencesHelper = new SharedPreferencesHelper(this);
        initialSteps = preferencesHelper.getInitialSteps();
        stepCount = preferencesHelper.getStepCount();
        stepCountTextView.setText("Steps: " + stepCount);
        Log.d("MainActivity", "Initial stepCount: " + stepCount);
    }

    /**
     * Requests ACTIVITY_RECOGNITION permission at runtime.
     */
    private void requestActivityRecognitionPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("MainActivity", "Requesting ACTIVITY_RECOGNITION permission.");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                    PERMISSION_REQUEST_ACTIVITY_RECOGNITION);
        } else {
            Log.d("MainActivity", "ACTIVITY_RECOGNITION permission already granted.");
            // Register sensor listener if not already registered
            registerStepSensorListener();
        }
    }

    /**
     * Requests POST_NOTIFICATIONS permission at runtime for Android 13+.
     */
    private void requestPostNotificationsPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("MainActivity", "Requesting POST_NOTIFICATIONS permission.");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    PERMISSION_REQUEST_POST_NOTIFICATIONS);
        } else {
            Log.d("MainActivity", "POST_NOTIFICATIONS permission already granted.");
            // Schedule notifications since permission is already granted
            scheduleNotifications();
        }
    }

    /**
     * Handles the result of permission requests.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_ACTIVITY_RECOGNITION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity", "ACTIVITY_RECOGNITION permission granted.");
                Toast.makeText(this, "Activity Recognition Permission Granted!", Toast.LENGTH_SHORT).show();
                // Register the sensor listener now that permission is granted
                registerStepSensorListener();
            } else {
                Log.e("MainActivity", "ACTIVITY_RECOGNITION permission denied.");
                Toast.makeText(this, "Permission Denied! Step counting will not work.", Toast.LENGTH_LONG).show();
                // Optionally, disable step counting features or inform the user
            }
        }

        if (requestCode == PERMISSION_REQUEST_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity", "POST_NOTIFICATIONS permission granted.");
                Toast.makeText(this, "Notifications Permission Granted!", Toast.LENGTH_SHORT).show();
                // Schedule notifications now that permission is granted
                scheduleNotifications();
            } else {
                Log.e("MainActivity", "POST_NOTIFICATIONS permission denied.");
                Toast.makeText(this, "Permission Denied! Notifications will not be shown.", Toast.LENGTH_LONG).show();
                // Optionally, disable notification features or inform the user
            }
        }
    }

    /**
     * Registers the step sensor listener.
     */
    private void registerStepSensorListener(){
        if(stepSensor != null && sensorManager != null){
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d("MainActivity", "Sensor listener registered.");
        } else {
            Log.e("MainActivity", "Cannot register sensor listener, stepSensor or sensorManager is null.");
        }
    }

    /**
     * Schedules notifications based on the desired interval.
     */
    private void scheduleNotifications(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Ensure the notification channel is created
            createNotificationChannel();
        }

        // For testing purposes, schedule notifications every 10 seconds
        // Replace with scheduleHourlyNotification(this) for production

//        NotificationHelper.schedule10SecondNotification(this);
        NotificationHelper.scheduleHourlyNotification(this);
        Log.d("MainActivity", "Notifications scheduled.");
    }

    /**
     * SensorEventListener callback for sensor value changes.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("MainActivity", "onSensorChanged called with value: " + event.values[0]);
        SharedPreferencesHelper preferencesHelper = new SharedPreferencesHelper(this);

        if(initialSteps == 0){
            initialSteps = (int) event.values[0];
            preferencesHelper.setInitialSteps(initialSteps);
            Log.d("MainActivity", "Initial Steps Set: " + initialSteps);
            return; // Do not update stepCount on the first sensor event
        }

        stepCount = (int) event.values[0] - initialSteps;
        Log.d("MainActivity", "Calculated stepCount: " + stepCount);

        preferencesHelper.setStepCount(stepCount);

        // Ensure UI updates are on the main thread
        runOnUiThread(() -> {
            if(stepCountTextView != null){
                stepCountTextView.setText("Steps: " + stepCount);
                Log.d("MainActivity", "Updated stepCountTextView to: " + stepCount);
            } else {
                Log.e("MainActivity", "stepCountTextView is null during UI update.");
            }
        });

        Log.d("MainActivity", "Step Count Updated: " + stepCount);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No action needed
    }

    /**
     * Creates a notification channel for Android O and above.
     */
    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = getString(R.string.step_tracker_channel);
            String description = "Channel for Step Tracker Notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("STEP_TRACKER_CHANNEL", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if(notificationManager != null){
                notificationManager.createNotificationChannel(channel);
                Log.d("MainActivity", "Notification channel created.");
            } else {
                Log.e("MainActivity", "NotificationManager is null.");
            }
        }
    }

    /**
     * Retrieves the current step count.
     * @return Current step count.
     */
    public int getStepCount(){
        return stepCount;
    }

    /**
     * Unregisters the sensor listener when the activity is paused.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if(sensorManager != null){
            sensorManager.unregisterListener(this);
            Log.d("MainActivity", "Sensor listener unregistered in onPause.");
        }
    }

    /**
     * Re-registers the sensor listener when the activity is resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if(sensorManager != null && stepSensor != null){
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d("MainActivity", "Sensor listener re-registered in onResume.");
        }
    }

    /**
     * Optionally, handle cleanup when the activity is destroyed.
     * Uncomment if you want to cancel alarms when the app is closed.
     */
    /*
    @Override
    protected void onDestroy() {
        super.onDestroy();
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        if(alarmManager != null){
            alarmManager.cancel(pendingIntent);
            Log.d("MainActivity", "AlarmManager notifications canceled.");
        }
    }
    */
}
