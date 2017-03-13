package com.example.alexey.imagesinfosearch.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexey on 3/12/17.
 */

public class Countries {

    private List<Country> mListCounties;

    private static Countries sCountries = null;
    public static Countries get() {
        if(sCountries == null)
            sCountries = new Countries();
        return sCountries;
    }
    private Countries(){
        mListCounties = new ArrayList<>();
    }

    public Country getCountry(int index) {
        return mListCounties.get(index);
    }

    public void addCountry(Country country) {
        mListCounties.add(country);
    }

    public void removeCountry(int index) {
        mListCounties.remove(index);
    }

    public int getSize() {
        return mListCounties.size();
    }

    public boolean isEmpty() {
        return mListCounties.isEmpty();
    }
}
