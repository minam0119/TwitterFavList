package com.lifeistech.android.favorite_collection.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.lifeistech.android.favorite_collection.R;
import com.lifeistech.android.favorite_collection.fragment.FavListFragment;

// 追加するTweetのIDを受け取って値を追加するクラス
public class AddFavActivity extends AppCompatActivity {
    public static final String TWEET_ID = "tweet_id";

    // TweetのIDを受け取ってIntentを作る
    public static Intent createIntent(Context context, long twitterId) {
        Intent intent = new Intent(context, AddFavActivity.class);
        intent.putExtra(TWEET_ID, twitterId);
        return intent;
    }

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfav);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("お気に入りに追加");
        toolbar.setTitleTextColor(Color.WHITE);

        Intent intent = getIntent();
        long tweetId = intent.getLongExtra(TWEET_ID, -1);
        Log.d(AddFavActivity.class.getSimpleName(), "TWEET_ID:" + tweetId);
        if (tweetId != -1) {
            //Fragmentをflamelayoutに入れる
            FavListFragment fragment = new FavListFragment();
            Bundle args = new Bundle();
            args.putLong(FavListFragment.TWEET_ID, tweetId);
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, fragment).commit();
        }
    }

}
