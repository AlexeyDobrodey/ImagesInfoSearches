package com.example.alexey.imagesinfosearch.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by alexey on 3/13/17.
 */

public class TimeReceiver extends BroadcastReceiver {

    public static final String TIME_BROADCAST = "time_broadcast";
    public static final String EXTRA_TIME = "extra_new_time";
    public static final String EXTRA_FLAG = "extra_rand_flag";


    private AppCompatActivity mAppCompatActivity;

    public TimeReceiver(AppCompatActivity appCompatActivity) {
        this.mAppCompatActivity = appCompatActivity;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String time = intent.getStringExtra(EXTRA_TIME);
        String urlFlag = intent.getStringExtra(EXTRA_FLAG);

        android.app.FragmentManager fm = mAppCompatActivity.getFragmentManager();
        DialogTimeNotification dialog = DialogTimeNotification.newInstance(time, urlFlag);
        dialog.show(fm, "dialog_time");
    }
}
