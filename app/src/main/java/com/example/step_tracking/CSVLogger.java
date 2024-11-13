package com.example.step_tracking;


import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CSVLogger {
    private static final String TAG = "CSVLogger";
    private static final String FILE_NAME = "notification_logs.csv";
    private Context context;

    public CSVLogger(Context context) {
        this.context = context;
        createCSVFileIfNotExists();
    }

    private void createCSVFileIfNotExists() {
        File file = new File(context.getFilesDir(), FILE_NAME);
        if (!file.exists()) {
            try (FileWriter writer = new FileWriter(file, true)) {
                writer.append("Sent Time,Response Time,User Response\n");
                writer.flush();
                Log.d(TAG, "CSV file created with headers.");
            } catch (IOException e) {
                Log.e(TAG, "Error creating CSV file", e);
            }
        }
    }

    public void logEvent(String sentTime, String responseTime, String userResponse) {
        File file = new File(context.getFilesDir(), FILE_NAME);
        try (FileWriter writer = new FileWriter(file, true)) {
            writer.append("\"").append(sentTime).append("\",")
                    .append("\"").append(responseTime).append("\",")
                    .append("\"").append(userResponse).append("\"\n");
            writer.flush();
            Log.d(TAG, "Logged event: " + sentTime + ", " + responseTime + ", " + userResponse);
        } catch (IOException e) {
            Log.e(TAG, "Error writing to CSV file", e);
        }
    }

    // Optional: Method to retrieve the CSV file path
    public String getCSVFilePath() {
        File file = new File(context.getFilesDir(), FILE_NAME);
        return file.getAbsolutePath();
    }
}