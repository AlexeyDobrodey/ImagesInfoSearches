package com.example.alexey.imagesinfosearch.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.alexey.imagesinfosearch.activities.ListCountries;
import com.example.alexey.imagesinfosearch.models.Countries;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class TimeService extends Service{
    public TimeService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(ListCountries.TAG, "I Bind");
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, final int startId) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                String dataStr = simpleDateFormat.format(new Date());

                Countries countries = Countries.get();

                String urlFlag = "534535";

                if(!countries.isEmpty()) {
                    int randomCountry = new Random().nextInt(countries.getSize());
                    urlFlag = countries.getCountry(randomCountry).getFlag();
                }

                Intent intent1 = new Intent(TimeReceiver.TIME_BROADCAST);
                intent1.putExtra(TimeReceiver.EXTRA_TIME, dataStr);
                intent1.putExtra(TimeReceiver.EXTRA_FLAG, urlFlag);
                sendBroadcast(intent1);
                stopSelf(startId);
            }
        });
        thread.start();
        return START_REDELIVER_INTENT;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(ListCountries.TAG, "Destroy");
    }
}
