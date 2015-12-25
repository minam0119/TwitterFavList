package com.lifeistech.android.favorite_collection.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Adapter;

import com.lifeistech.android.favorite_collection.R;
import com.lifeistech.android.favorite_collection.adapter.TutorialAdapter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class LoginActivity extends AppCompatActivity {

    private ViewPager viewPager;
    SharedPreferences sharedPreferences;
    TutorialAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // ログインしているかどうか
        boolean isLogin = sharedPreferences.getBoolean("isLogin", false);

        // SharedPreferencesからログイン済みかどうかを取得する
        if (isLogin) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            // TopActivityにいく
            return;
        }

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        adapter = new TutorialAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });

        TwitterCore twitterCore = TwitterCore.getInstance();
        TwitterSession twitterSession = twitterCore.getSessionManager().getActiveSession();
        if (twitterSession == null) {
            twitterCore.getSessionManager();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (viewPager.getCurrentItem() == adapter.getCount() - 1) {
            Fragment fragment = adapter.getItem(viewPager.getCurrentItem());
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    public void idou(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
