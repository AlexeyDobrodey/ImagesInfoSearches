package com.example.alexey.imagesinfosearch.services;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alexey.imagesinfosearch.R;
import com.squareup.picasso.Picasso;

/**
 * Created by alexey on 3/13/17.
 */

public class DialogTimeNotification extends DialogFragment {


    private static final String KEY_EXTRA_TIME = "key_extra_time";
    private static final String KEY_EXTRA_URL_FLAG = "key_extra_url_flag";

    private String mTime;
    private String mURLFlag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            mTime = getArguments().getString(KEY_EXTRA_TIME);
            mURLFlag = getArguments().getString(KEY_EXTRA_URL_FLAG);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time_notification, null);


        ImageView imageView = (ImageView)view.findViewById(R.id.country_flag);
        Picasso.with(getActivity()).load(mURLFlag).error(R.drawable.bangladesh).into(imageView);

        TextView textView = (TextView) view.findViewById(R.id.now_time);
        textView.setText(mTime);
        return new AlertDialog.Builder(getActivity())
                .setTitle("Now Time")
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }

    public static DialogTimeNotification newInstance(String time, String urlFlag) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_TIME, time);
        bundle.putString(KEY_EXTRA_URL_FLAG, urlFlag);
        DialogTimeNotification dialogTimeNotification = new DialogTimeNotification();
        dialogTimeNotification.setArguments(bundle);
        return  dialogTimeNotification;
    }
}
