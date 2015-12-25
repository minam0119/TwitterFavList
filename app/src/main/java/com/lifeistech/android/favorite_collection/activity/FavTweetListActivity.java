package com.lifeistech.android.favorite_collection.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.lifeistech.android.favorite_collection.R;
import com.lifeistech.android.favorite_collection.adapter.TweetAdapter;
import com.lifeistech.android.favorite_collection.model.Category;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.FavoriteService;
import com.twitter.sdk.android.tweetui.LoadCallback;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.UserTimeline;

import java.util.ArrayList;
import java.util.List;

public class FavTweetListActivity extends AppCompatActivity {
    public static final String CATEGORY_ID = "category_id";

    Toolbar toolbar;
    private ListView listView;

    TweetAdapter adapter;

    TwitterCore twitterCore;
    TwitterApiClient twitterApiClient;
    private boolean isRequesting = false;

    private Long lastSinceId;
    private Category mCategory;
    private String mQueryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_tweet_list);

        lastSinceId = null;
        twitterCore = TwitterCore.getInstance();
        twitterApiClient = twitterCore.getApiClient();

        listView = (ListView) findViewById(R.id.listView);
        adapter = new TweetAdapter(this, 0);
        listView.setAdapter(adapter);
        listView.setEmptyView(findViewById(R.id.emptyView));

        long categoryId = getIntent().getLongExtra(CATEGORY_ID, -1);
        if (categoryId >= 0) {
            mCategory = new Select().from(Category.class).where("ID = ?", categoryId).executeSingle();
            if (mCategory == null) {
                Toast.makeText(this, "カテゴリーの値が不正です", Toast.LENGTH_SHORT).show();
                finish();
            }
            if (mCategory.name.startsWith("@")) {
                mQueryName = mCategory.name.substring(1, mCategory.name.length());
                Log.d(FavTweetListActivity.class.getSimpleName(), mQueryName);
                getFavTweet(lastSinceId, null);
            } else {
                getCategoryTweet();
            }
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Log.d(FavTweetListActivity.class.getSimpleName(), "onItemLongClick");
                    final Tweet item = adapter.getItem(position);

                    AlertDialog.Builder builder = new AlertDialog.Builder(FavTweetListActivity.this)
                            .setMessage("このツイートをリストから削除しますか?")
                            .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    adapter.remove(item);
                                    mCategory.ids.remove(item.getId());
                                    dialog.dismiss();
                                }
                            }).setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    //ダイアログを表示
                    builder.show();
                    mCategory.save();
                    return false;
                }
            });
        } else {
            mCategory = new Category();
            mCategory.name = "全てのお気に入り";
            mCategory.color = Color.parseColor("#aaFF0000");;

            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int i) {
                }

                @Override
                public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (totalItemCount > 0 && totalItemCount <= firstVisibleItem + visibleItemCount) {
                        getFavTweet(lastSinceId, null);
                    }
                }
            });
            getFavTweet(lastSinceId, null);
        }
        setToolbar();
    }

    public void getCategoryTweet() {
        if (mCategory.ids == null || mCategory.ids.isEmpty()) return;
        Log.d(FavTweetListActivity.class.getSimpleName(), "mCategory.ids.size = " + mCategory.ids.size());
        TweetUtils.loadTweets(mCategory.ids, new LoadCallback<List<Tweet>>() {
            @Override
            public void success(List<Tweet> tweets) {
                Log.d(FavTweetListActivity.class.getSimpleName(), "success");
                adapter.addAll(tweets);
            }

            @Override
            public void failure(TwitterException e) {
                //もし429 Too Many Requests だったらToastを表示する
                Toast.makeText(FavTweetListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
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
                        if (!TextUtils.isEmpty(mQueryName)) {
                            List<Tweet> data = new ArrayList<Tweet>();
                            for (Tweet tweet : result.data) {
                                if (mQueryName.equals(tweet.user.screenName)) {
                                    data.add(tweet);
                                }
                            }
                            adapter.addAll(data);
                        } else {
                            adapter.addAll(result.data);
                        }
                    }

                    @Override
                    public void failure(TwitterException e) {
                        isRequesting = false;
                        //もし429 Too Many Requests だったらToastを表示する
                        Toast.makeText(FavTweetListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                });
        isRequesting = true;
    }

    private void setToolbar() {
        //toolbarにリスト名とリストの色を表示する
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mCategory.name);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(mCategory.color);
        toolbar.setAlpha(0.7f);
    }

}
