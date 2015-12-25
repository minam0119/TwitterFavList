package com.lifeistech.android.favorite_collection.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import com.lifeistech.android.favorite_collection.R;
import com.lifeistech.android.favorite_collection.fragment.TweetListFragment;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

public class TweetActivity extends AppCompatActivity {
    Button bttweet;
    TwitterApiClient twitterApiClient;
    EditText editText;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);
        bttweet = (Button) findViewById(R.id.button4);
        editText = (EditText) findViewById(R.id.editText);
        twitterApiClient = TwitterCore.getInstance().getApiClient();
        bttweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message = editText.getText().toString();
                StatusesService statusesService = twitterApiClient.getStatusesService();
                statusesService.update(message, null, false, null, null, null, false, null, new Callback<Tweet>() {
                    @Override
                    public void success(Result<Tweet> result) {
                        Intent intent = new Intent(TweetActivity.this,TweetListFragment.class);
                        Log.v("TweetActivity", "ツイート内容:" + message);
                        Toast.makeText(TweetActivity.this,"ツイートを投稿しました", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }

                    @Override
                    public void failure(TwitterException e) {
                        Toast.makeText(TweetActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                });
            }
        });

    }
}
