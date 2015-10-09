package com.lifeistech.android.twittertest;

import android.app.ListActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.AbsListView;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.FavoriteService;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetui.TweetViewFetchAdapter;

import java.util.ArrayList;
import java.util.List;

//
public class FavTweetListActivity extends ListActivity {

    //final TweetViewFetchAdapter adapter = new TweetViewFetchAdapter<>(FavTweetListActivity.this);
    TweetAdapter adapter;
    TwitterApiClient twitterApiClient;

    private String mCategoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_list);

        mCategoryName = getIntent().getStringExtra("categoryName");
        // カテゴリー名がNullではなく、@で始まっている場合
        if (!TextUtils.isEmpty(mCategoryName) && mCategoryName.startsWith("@")) {
            // カテゴリー名に@を抜いた文字を入れる
            mCategoryName = mCategoryName.substring("@".length());
        }

        adapter = new TweetAdapter(this, 0);
        setListAdapter(adapter);

        twitterApiClient = TwitterCore.getInstance().getApiClient();

        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(totalItemCount > 0 && totalItemCount <= firstVisibleItem + visibleItemCount){
                    Tweet tweet = adapter.getItem(adapter.getCount() - 1);
                    if (tweet != null) {
                        getFavTweet(String.valueOf(tweet.getId()), null);
                    } else {
                        getFavTweet(null, null);
                    }
                }
            }
        });
        getFavTweet(null, null);
    }

    // 全部のお気に入りTweetを取得する
    private void getFavTweet(String sinceId, String maxId) {
        // statusAPI用のserviceクラス
        FavoriteService favoriteService = twitterApiClient.getFavoriteService();
        favoriteService.list(
                TwitterCore.getInstance().getSessionManager().getActiveSession().getUserId(),
                null,
                30, //Count
                sinceId,
                maxId,
                null,
                new Callback<List<Tweet>>() {
                    @Override
                    public void success(Result<List<Tweet>> result) {
                        // もらってきたデータ
                        List<Tweet> data = new ArrayList<>();
                        // もらってきたデータのひとつひとつに対して
                        for (Tweet tweet : result.data) {
                            // ユーザー名を特定する
                            String name = tweet.user.screenName;
                            Log.d("Twitter", "username: " + name);
                            // ユーザー名が空か、ユーザー名がもしfavlistの名前と一緒だったら
                            if (TextUtils.isEmpty(mCategoryName) || mCategoryName.equals(name)) {
                                // リストに追加する
                                Log.d("Twitter", "user detected: " + name);
                                // Tweetを追加
                                data.add(tweet);
                            }
                        }
                        if (data.isEmpty() && !result.data.isEmpty()) {
                            long sinceId = result.data.get(result.data.size() - 1).getId();
                            getFavTweet(String.valueOf(sinceId), null);
                        } else {
                            adapter.addAll(data);
                        }

                    }

                    @Override
                    public void failure(TwitterException e) {
                        e.printStackTrace();
                    }
                });
    }
}
