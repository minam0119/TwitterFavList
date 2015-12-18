package com.lifeistech.android.twittertest.event;

/**
 * Created by MINAMI on 2015/12/11.
 */
public class AddCategoryEvent {
    public long tweetId;

    public AddCategoryEvent(long tweetId) {
        this.tweetId = tweetId;
    }
}
