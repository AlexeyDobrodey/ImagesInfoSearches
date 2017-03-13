package com.example.alexey.imagesinfosearch.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
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



public class ListCountries extends AppCompatActivity {
    public static final String TAG = "ListCountries";
    public RecyclerView mRecyclerView;

    public Countries mCountries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        TimeReceiver timeReceiver = new TimeReceiver(this);
        IntentFilter intentFilter = new IntentFilter(TimeReceiver.TIME_BROADCAST);
        registerReceiver(timeReceiver, intentFilter);

        Intent intent = new Intent(this, TimeService.class);
        startService(intent);

        mRecyclerView = (RecyclerView) findViewById(R.id.id_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        setSwipeToRecyclerView();


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
            Log.d(ListCountries.TAG, jsonStr);
            mCountries = JsonParcer.getSomeCountries(jsonStr);
            updateListCountries();
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
