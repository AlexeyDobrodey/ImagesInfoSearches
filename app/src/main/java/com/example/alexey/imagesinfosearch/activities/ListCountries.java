package com.example.alexey.imagesinfosearch.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alexey.imagesinfosearch.R;
import com.example.alexey.imagesinfosearch.models.Countries;
import com.example.alexey.imagesinfosearch.models.Country;
import com.example.alexey.imagesinfosearch.models.HttpClient;
import com.example.alexey.imagesinfosearch.models.JsonParcer;
import com.example.alexey.imagesinfosearch.services.TimeReceiver;
import com.example.alexey.imagesinfosearch.services.TimeService;
import com.squareup.picasso.Picasso;

import java.util.Calendar;


public class ListCountries extends AppCompatActivity {
    public static final String TAG = "ListCountries";
    public RecyclerView mRecyclerView;

    public Countries mCountries;
    private TimeReceiver mTimeReceiver;
    private ImageView mErrorConnectInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_countries);

        runServices();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.INTERNET, Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                    }
                    , 1);
        }

        initUI();
    }

    private void initUI() {
        mRecyclerView = (RecyclerView) findViewById(R.id.id_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        setSwipeToRecyclerView();


        mErrorConnectInternet = (ImageView) findViewById(R.id.id_img_error_connection);
        mErrorConnectInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mErrorConnectInternet.setVisibility(View.INVISIBLE);
                DownloadJsonTask downloadJsonTask = new DownloadJsonTask();
                downloadJsonTask.execute();
            }
        });

        mCountries = Countries.get();
        if(mCountries.isEmpty()) {
            DownloadJsonTask downloadJsonTask = new DownloadJsonTask();
            downloadJsonTask.execute();
        }
        else
            updateListCountries();
    }

    private void setSwipeToRecyclerView() {
        ItemTouchHelper.SimpleCallback simpleCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        int swipedPosition = viewHolder.getAdapterPosition();
                        CountryInfoAdapter adapter = (CountryInfoAdapter)mRecyclerView.getAdapter();
                        adapter.onRemoveItem(swipedPosition);
                    }
                };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void runServices() {
        Intent intent = new Intent(this, TimeService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        long time = calendar.getTimeInMillis();

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 2 * 60 * 1000, pendingIntent);
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



    private class DownloadJsonTask extends AsyncTask<Void, Void, String> {
        private ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = new ProgressDialog(ListCountries.this);
            mProgressDialog.setTitle("Json Parsing");
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            return HttpClient.getJSonObjString();
        }

        @Override
        protected void onPostExecute(String jsonStr) {
            super.onPostExecute(jsonStr);
            if(jsonStr == null) {
                mErrorConnectInternet.setVisibility(View.VISIBLE);
            }else {
                mCountries = JsonParcer.getSomeCountries(jsonStr);
                updateListCountries();
            }
            mProgressDialog.dismiss();
        }
    }

    private void updateListCountries() {
        CountryInfoAdapter countryInfoAdapter = new CountryInfoAdapter();
        mRecyclerView.setAdapter(countryInfoAdapter);
    }

    public class CountryInfoAdapter extends RecyclerView.Adapter<CountryInfoView> {

        @Override
        public CountryInfoView onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(ListCountries.this)
                    .inflate(R.layout.row_country_info, parent, false);
            return new CountryInfoView(view);
        }

        @Override
        public void onBindViewHolder(CountryInfoView holder, int position) {
            holder.setCountry(mCountries.getCountry(position), position);
        }

        @Override
        public int getItemCount() {
            return mCountries.getSize();
        }

        public void onRemoveItem(int position) {
            mCountries.removeCountry(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mCountries.getSize());
        }
    }

    private class CountryInfoView extends RecyclerView.ViewHolder{
        private ImageView mCountryFlag;
        private TextView mRank, mName, mPopulation;
        private View mView;

        public CountryInfoView(final View itemView) {
            super(itemView);
            mView = itemView;
            mCountryFlag = (ImageView) itemView.findViewById(R.id.country_flag_image);
            mRank = (TextView)itemView.findViewById(R.id.country_rank);
            mName = (TextView) itemView.findViewById(R.id.country_name);
            mPopulation = (TextView) itemView.findViewById(R.id.country_population);
        }

        public void setCountry(Country country, final int idCountry) {
            Picasso.with(ListCountries.this)
                    .load(country.getFlag())
                    .error(R.drawable.img_not_found)
                    .into(mCountryFlag);

            mRank.setText(getString(R.string.country_rank) + String.valueOf(country.getRank()));
            mName.setText(getString(R.string.country_name) + country.getName());
            mPopulation.setText(getString(R.string.country_population) + country.getPopulation());

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = DescriptionCountry.newInstance(ListCountries.this, idCountry);

                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        ListCountries.this,
                            new Pair<View, String>(mView.findViewById(R.id.country_flag_image), getString(R.string.transition_name_flag))
                    );

                    ActivityCompat.startActivity(ListCountries.this, intent, optionsCompat.toBundle());
                }
            });

        }

    }

}
