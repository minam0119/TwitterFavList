package com.lifeistech.android.twittertest.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ListView;

import com.lifeistech.android.twittertest.R;
import com.lifeistech.android.twittertest.adapter.TweetAdapter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.FavoriteService;

import java.util.ArrayList;
import java.util.List;

//
public class FavTweetListActivity extends AppCompatActivity {

    private ListView listView;
    TweetAdapter adapter;
    TwitterCore twitterCore;
    TwitterApiClient twitterApiClient;
    private String mCategoryName;
    private boolean isRequesting = false;
    private Long lastSinceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_tweet_list);

        twitterCore = TwitterCore.getInstance();
        twitterApiClient = twitterCore.getApiClient();

        mCategoryName = getIntent().getStringExtra("categoryName");
        // カテゴリー名がNullではなく、@で始まっている場合
        if (!TextUtils.isEmpty(mCategoryName) && mCategoryName.startsWith("@")) {
            // カテゴリー名に@を抜いた文字を入れる
            mCategoryName = mCategoryName.substring("@".length());
        }

        adapter = new TweetAdapter(this, 0);

        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setEmptyView(findViewById(R.id.emptyView));
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount > 0 && totalItemCount <= firstVisibleItem + visibleItemCount + 2) {
                    getFavTweet(lastSinceId, null);
                }
            }
        });
        lastSinceId = null;
        getFavTweet(lastSinceId, null);
    }

    // 全部のお気に入りTweetを取得する
    private void getFavTweet(Long sinceId, Long maxId) {
        if (isRequesting) return;
        // statusAPI用のserviceクラス
        FavoriteService favoriteService = twitterApiClient.getFavoriteService();
        favoriteService.list(
                twitterCore.getSessionManager().getActiveSession().getUserId(),
                null,
                30, //Count
                sinceId == null ? null : String.valueOf(sinceId),
                maxId == null ? null : String.valueOf(maxId),
                null,
                new Callback<List<Tweet>>() {
                    @Override
                    public void success(Result<List<Tweet>> result) {
                        if (result.data.isEmpty()) return;
                        isRequesting = false;
                        lastSinceId = result.data.get(result.data.size() - 1).getId();
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
                        // Filterにかけた
                        if (data.isEmpty()) {
                            getFavTweet(lastSinceId, null);
                        } else {
                            adapter.addAll(data);
                        }

                    }

                    @Override
                    public void failure(TwitterException e) {
                        isRequesting = false;
                        e.printStackTrace();
                    }
                });
        isRequesting = true;
    }
}
