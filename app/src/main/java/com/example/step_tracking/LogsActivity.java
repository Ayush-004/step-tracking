package com.example.step_tracking;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class LogsActivity extends AppCompatActivity {

    private static final String TAG = "LogsActivity";
    private TextView logsTextView;
    private CSVLogger csvLogger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);

        logsTextView = findViewById(R.id.logsTextView);
        csvLogger = new CSVLogger(this);

        displayLogs();
    }

    private void displayLogs(){
        File file = new File(csvLogger.getCSVFilePath());
        if(!file.exists()){
            logsTextView.setText("No logs available.");
            return;
        }

        StringBuilder logs = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))){
            String line;
            while((line = br.readLine()) != null){
                logs.append(line).append("\n");
            }
            logsTextView.setText(logs.toString());
        } catch (IOException e){
            Log.e(TAG, "Error reading CSV file", e);
            logsTextView.setText("Error reading logs.");
        }
    }
}