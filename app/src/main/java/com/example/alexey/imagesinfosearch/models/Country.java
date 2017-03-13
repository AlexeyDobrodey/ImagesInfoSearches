package com.example.alexey.imagesinfosearch.models;

import java.io.Serializable;

/**
 * Created by alexey on 3/12/17.
 */

public class Country{
    private int mRank;
    private String mName;
    private String mPopulation;
    private String mFlag;

    public int getRank() {
        return mRank;
    }

    public void setRank(int rank) {
        mRank = rank;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPopulation() {
        return mPopulation;
    }

    public void setPopulation(String population) {
        mPopulation = population;
    }

    public String getFlag() {
        return mFlag;
    }

    public void setFlag(String flag) {
        mFlag = flag;
    }
}
