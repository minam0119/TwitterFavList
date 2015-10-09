package com.lifeistech.android.twittertest;

import android.app.ListActivity;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetui.CompactTweetView;
import com.twitter.sdk.android.tweetui.TweetViewFetchAdapter;

import java.util.List;


public class TweetListActivity extends ListActivity {

    TwitterApiClient twitterApiClient;
    TweetAdapter adapter;
    Button tweetbt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_list);

        twitterApiClient = TwitterCore.getInstance().getApiClient();
        adapter = new TweetAdapter(this, 0);

        setListAdapter(adapter);

        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            //スクロールする度に過去のツイートが取得される
            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount > 0 && totalItemCount <= firstVisibleItem + visibleItemCount + 2) {
                    // ListViewに表示されている最後のTweet
                    Tweet tweet = adapter.getItem(adapter.getCount() - 1);
                    if (tweet != null) {
                        getTweet(null, tweet.getId());
                    } else {
                        getTweet(null, null);
                    }
                }
            }
        });
        getTweet(null, null);

        tweetbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ツイートする
            }
        });
    }

    private void getTweet(Long sinceId, Long maxId) {
        // statusAPI用のserviceクラス
        StatusesService statusesService = twitterApiClient.getStatusesService();
        statusesService.homeTimeline(30, sinceId, maxId, false, false, false, false,
                new Callback<List<Tweet>>() {
                    @Override
                    public void success(Result<List<Tweet>> listResult) {
                        adapter.addAll(listResult.data);
                    }

                    @Override
                    public void failure(TwitterException e) {
                    }
                });

    }

}
