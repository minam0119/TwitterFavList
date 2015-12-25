package com.lifeistech.android.favorite_collection.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.lifeistech.android.favorite_collection.R;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class LoginActivity extends Activity {

    private TwitterLoginButton loginButton;
    SharedPreferences sharedPreferences;

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
        }


        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // SharedPreferencesにログイン済みと記録する
                sharedPreferences.edit().putBoolean("isLogin", true).commit();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
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
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

    public void idou(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
