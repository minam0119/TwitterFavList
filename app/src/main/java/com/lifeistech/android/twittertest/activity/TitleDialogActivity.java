package com.lifeistech.android.twittertest.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.lifeistech.android.twittertest.R;


public class TitleDialogActivity extends ActionBarActivity {
    TextView titletx;
    Button okbt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title_dialog);

        titletx = (TextView)findViewById(R.id.titletx);
        okbt = (Button)findViewById(R.id.okbt);
    }

}
