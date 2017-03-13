package com.example.alexey.imagesinfosearch.activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alexey.imagesinfosearch.R;
import com.example.alexey.imagesinfosearch.models.Countries;
import com.example.alexey.imagesinfosearch.models.Country;
import com.example.alexey.imagesinfosearch.services.TimeReceiver;
import com.squareup.picasso.Picasso;

/**
 * Created by alexey on 3/12/17.
 */

public class DescriptionCountry extends AppCompatActivity {
    private ImageView mCountryFlag;
    private TextView mDescription;

    private Country mCountry;
    private static final String EXTRA_ID_COUNTRY = "country_extra";

    private TimeReceiver mTimeReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description_country);

        int idCountry = getIntent().getExtras().getInt(EXTRA_ID_COUNTRY);
        mCountry = Countries.get().getCountry(idCountry);
        mCountryFlag = (ImageView) findViewById(R.id.country_flag);

        Picasso.with(this)
                .load(mCountry.getFlag())
                .into(mCountryFlag);

        mDescription = (TextView)  findViewById(R.id.description_country);
        mDescription.setText(
                getString(R.string.country_rank) + mCountry.getRank() + ", " +
                getString(R.string.country_name) + mCountry.getName() + ", " +
                getString(R.string.country_population) + mCountry.getPopulation()
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        mTimeReceiver = new TimeReceiver(this);
        IntentFilter intentFilter = new IntentFilter(TimeReceiver.TIME_BROADCAST);
        registerReceiver(mTimeReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mTimeReceiver);
    }

    public static Intent newInstance(Context ctx, int idCountry) {
        Intent intent = new Intent(ctx, DescriptionCountry.class);
        intent.putExtra(EXTRA_ID_COUNTRY, idCountry);
        return intent;
    }
}
