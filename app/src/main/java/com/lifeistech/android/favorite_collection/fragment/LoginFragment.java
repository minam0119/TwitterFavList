package com.lifeistech.android.favorite_collection.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lifeistech.android.favorite_collection.R;
import com.lifeistech.android.favorite_collection.activity.MainActivity;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class LoginFragment extends Fragment {

    private TwitterLoginButton loginButton;
    SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // ログインしているかどうか
        boolean isLogin = sharedPreferences.getBoolean("isLogin", false);

        // SharedPreferencesからログイン済みかどうかを取得する
        if (isLogin) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            // TopActivityにいく
        }


        loginButton = (TwitterLoginButton) view.findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // SharedPreferencesにログイン済みと記録する
                sharedPreferences.edit().putBoolean("isLogin", true).commit();

                Intent intent = new Intent(getActivity(), MainActivity.class);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

    public void idou(View v) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }
}
