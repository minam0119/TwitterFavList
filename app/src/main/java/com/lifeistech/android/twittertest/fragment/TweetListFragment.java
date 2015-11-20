package com.lifeistech.android.twittertest.fragment;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.content.Intent;
import android.widget.ListView;

import com.lifeistech.android.twittertest.R;
import com.lifeistech.android.twittertest.activity.TweetActivity;
import com.lifeistech.android.twittertest.adapter.TweetAdapter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.util.List;


public class TweetListFragment extends Fragment {

    ListView listView;
    TwitterApiClient twitterApiClient;
    TweetAdapter adapter;
    Button tweetbt;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tweet_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        twitterApiClient = TwitterCore.getInstance().getApiClient();
        adapter = new TweetAdapter(getActivity(), 0);
        tweetbt = (Button) view.findViewById(R.id.button3);

        listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setEmptyView(view.findViewById(R.id.emptyView));

        tweetbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ツイート画面へ
//                Intent intent = new Intent(getActivity(), TweetActivity.class);
//                startActivity(intent);

                TweetComposer.Builder builder = new TweetComposer.Builder(getActivity()).text("");
                builder.show();
            }
        });


        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
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
