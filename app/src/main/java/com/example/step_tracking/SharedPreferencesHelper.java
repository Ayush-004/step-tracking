package com.example.step_tracking;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {
    private static final String PREFS_NAME = "StepTrackerPrefs";
    private static final String STEP_COUNT_KEY = "step_count";
    private static final String SENT_TIME_PREFIX = "sent_time_";
    private static final String INITIAL_STEPS_KEY = "initial_steps";



    private SharedPreferences sharedPreferences;

    public SharedPreferencesHelper(Context context){
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void setInitialSteps(int initialSteps){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(INITIAL_STEPS_KEY, initialSteps);
        editor.apply();
    }

    public int getInitialSteps(){
        return sharedPreferences.getInt(INITIAL_STEPS_KEY, 0);
    }

    public void resetInitialSteps(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(INITIAL_STEPS_KEY);
        editor.apply();
    }
    public void setStepCount(int count){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(STEP_COUNT_KEY, count);
        editor.apply();
    }

    public int getStepCount(){
        return sharedPreferences.getInt(STEP_COUNT_KEY, 0);
    }

    public void resetStepCount(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(STEP_COUNT_KEY, 0);
        editor.apply();
    }

    // New methods for handling sent times
    public void setSentTime(int notificationId, String sentTime){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SENT_TIME_PREFIX + notificationId, sentTime);
        editor.apply();
    }

    public String getSentTime(int notificationId){
        return sharedPreferences.getString(SENT_TIME_PREFIX + notificationId, "N/A");
    }

    public void removeSentTime(int notificationId){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(SENT_TIME_PREFIX + notificationId);
        editor.apply();
    }
}