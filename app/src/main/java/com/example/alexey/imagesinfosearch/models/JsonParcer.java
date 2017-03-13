package com.example.alexey.imagesinfosearch.models;

import android.util.Log;

import com.example.alexey.imagesinfosearch.activities.ListCountries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by alexey on 3/12/17.
 */

public class JsonParcer {


    public static Countries getSomeCountries(String jsonStr) {
        //List<Country> countries = new ArrayList<>();
        Countries countries = Countries.get();
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray arrayCountries = jsonObject.getJSONArray("worldpopulation");
            for(int i = 0; i < arrayCountries.length(); i++) {
                JSONObject jsonCountry = arrayCountries.getJSONObject(i);
                Country country = new Country();
                country.setRank(jsonCountry.getInt("rank"));
                country.setName(jsonCountry.getString("country"));
                country.setPopulation(jsonCountry.getString("population"));
                country.setFlag(jsonCountry.getString("flag"));
                countries.addCountry(country);
            }
        } catch (JSONException e) {
            Log.d(ListCountries.TAG, e.getMessage());
        }
        return  countries;
    }
}
