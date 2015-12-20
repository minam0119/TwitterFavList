package com.lifeistech.android.twittertest.fragment;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.lifeistech.android.twittertest.R;
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


public class TweetListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    SwipeRefreshLayout swipeRefreshLayout;
    ListView listView;
    ImageView tweetButton;

    TwitterApiClient twitterApiClient;
    TweetAdapter adapter;

    private TweetListListener listener;

    // 今通信をしているかどうか (false -> リクエストしていない, true -> リクエスト中)
    private boolean isRequesting = false;

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
        tweetButton = (ImageView) view.findViewById(R.id.button3);
        tweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), TweetActivity.class);
//                startActivity(intent);
                // ツイート画面へ
                // ツイート画面へ
                TweetComposer.Builder builder = new TweetComposer.Builder(getActivity()).text("");
                builder.show();
            }
        });

        listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setEmptyView(view.findViewById(R.id.emptyView));
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
                        // maxIdが <= だったので、-1する
                        getTweet(null, tweet.getId() - 1);
                    }
                }
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        // Tweetが一件もないときのリクエスト
        getTweet(null, null);
    }

    private void getTweet(Long sinceId, Long maxId) {
        // リクエスト中は再度リクエストしないようにする
        if (isRequesting) return;
        // statusAPI用のserviceクラス
        StatusesService statusesService = twitterApiClient.getStatusesService();
        statusesService.homeTimeline(30, sinceId, maxId, false, false, false, true,
                new Callback<List<Tweet>>() {
                    @Override
                    public void success(Result<List<Tweet>> listResult) {
                        adapter.addAll(listResult.data);
                        isRequesting = false;
                        if (swipeRefreshLayout.isRefreshing()) {
                            //とまる
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }

                    @Override
                    public void failure(TwitterException e) {
                        isRequesting = false;
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
        isRequesting = true;
    }

    @Override
    public void onRefresh() {
        // TODO リストを上から下に引いて更新したときの処理を書く
//        isRequesting = false;
//        getTweet(adapter.getItem(0).getId(), null);
        adapter.clear();
        getTweet(null, null);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof TweetListListener) {
            listener = (TweetListListener) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface TweetListListener {
        void onListAddListener(String name);
    }
}
