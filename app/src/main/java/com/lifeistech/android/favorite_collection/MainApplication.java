package com.lifeistech.android.favorite_collection;

import com.activeandroid.ActiveAndroid;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.twitter.sdk.android.tweetui.TweetUi;

import io.fabric.sdk.android.Fabric;

public class MainApplication extends com.activeandroid.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(getString(R.string.twitter_key), getString(R.string.twitter_secret));
        Fabric.with(this, new TwitterCore(authConfig), new TweetComposer(), new TweetUi());

        ActiveAndroid.initialize(this);

        BusHolder.init();
    }

    /*@Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }*/
}
