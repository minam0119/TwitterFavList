package com.lifeistech.android.twittertest.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.lifeistech.android.twittertest.BusHolder;
import com.lifeistech.android.twittertest.activity.FavTweetListActivity;
import com.lifeistech.android.twittertest.event.AddCategoryEvent;
import com.lifeistech.android.twittertest.model.Category;
import com.lifeistech.android.twittertest.adapter.FavListAdapter;
import com.lifeistech.android.twittertest.R;
import com.squareup.otto.Subscribe;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.FavoriteService;
import com.twitter.sdk.android.core.services.StatusesService;

import java.util.ArrayList;
import java.util.List;

public class FavListFragment extends Fragment implements
        AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener,
        TitleDialogFragment.CreateDialogListener {
    public static final String TWEET_ID = "tweet_id";

    ListView listView;
    FavListAdapter mAdapter;
    List<Category> mCategoryList;
    FloatingActionButton mFab;
    TwitterApiClient twitterApiClient;
    private Long mTweetId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args == null) return;
        mTweetId = args.getLong(TWEET_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fav_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new FavListAdapter(getActivity(), 0);
        mFab = (FloatingActionButton) view.findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateDialog();
            }
        });
        twitterApiClient = TwitterCore.getInstance().getApiClient();

        Category allCategory = new Category();
        allCategory.name = "すべてのお気に入り";
        allCategory.color = Color.parseColor("#aaFF0000");
        mAdapter.add(allCategory);

        // 今の保存されているカテゴリーを取り出す
        mCategoryList = new Select().from(Category.class).execute();
        for (Category category : mCategoryList) {
            mAdapter.add(category);
        }

        listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(mAdapter);
        //ListをClickで移動
        listView.setOnItemClickListener(this);
        //長押しでdelete
        listView.setOnItemLongClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        BusHolder.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        BusHolder.unregister(this);
    }

    private void showCreateDialog() {
        TitleDialogFragment titleDialogFragment = new TitleDialogFragment();
        titleDialogFragment.show(getChildFragmentManager(), "dialog");
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Intent intent = new Intent(getActivity(), FavTweetListActivity.class);
        if (position == 0) {
            if (mTweetId != null) {
                Toast.makeText(getActivity(), "全てのお気に入りにはツイートを追加できません", Toast.LENGTH_SHORT).show();
                return;
            }
            intent.putExtra(FavTweetListActivity.CATEGORY_ID, -1);
        } else {
            Category category = mAdapter.getItem(position);
            if (addTweet(category)) {
                return;
            } else {
                intent.putExtra(FavTweetListActivity.CATEGORY_ID, category.getId());
            }
        }
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
        // ダイアログの作成
        final Category selectedItem = mAdapter.getItem(position);
        //0番のときは何もしない
        if (position == 0) {
            return true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setMessage(selectedItem.name + "を削除しますか?")
                .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Category selectedItem = mAdapter.getItem(position);
                        // ActiveAndroid内からも削除するためにdeleteメソッドを呼び出し
                        selectedItem.delete();
                        mAdapter.remove(selectedItem);
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
        return true;
    }

    @Override
    public void onCreateCategory(Category category) {
        mAdapter.add(category);
        if (mTweetId != null) {
            mTweetId = null;
            Toast.makeText(getActivity(), category.name + "にツイートを追加しました！", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean addTweet(Category category) {
        if (mTweetId != null) {
            if (category.ids == null) category.ids = new ArrayList<>();
            category.ids.add(mTweetId);
            category.save();

            FavoriteService favoriteService = twitterApiClient.getFavoriteService();
            favoriteService.create(mTweetId, false, new Callback<Tweet>() {
                @Override
                public void success(Result<Tweet> result) {
                    Toast.makeText(getActivity(), result.data.user.screenName + "さんの投稿をお気に入りに登録しました", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void failure(TwitterException e) {

                }
            });

            mTweetId = null;
            Toast.makeText(getActivity(), category.name + "にツイートを追加しました！", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

}