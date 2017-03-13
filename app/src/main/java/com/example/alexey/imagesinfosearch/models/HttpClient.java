package com.example.alexey.imagesinfosearch.models;

import android.util.Log;

import com.example.alexey.imagesinfosearch.activities.ListCountries;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by alexey on 3/12/17.
 */

public class HttpClient {

    private static final String URL_COUNTRIES_JSON =
            "http://www.androidbegin.com/tutorial/jsonparsetutorial.txt";

    public static String getJSonObjString() {
        String result = null;

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL_COUNTRIES_JSON)
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            result = response.body().string();
        } catch (IOException e) {
            Log.d(ListCountries.TAG, e.getMessage());
        }
        return result;
    }
}
