package com.lifeistech.android.favorite_collection;

import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.TweetEntities;
import com.twitter.sdk.android.core.models.UrlEntity;
import com.twitter.sdk.android.tweetui.internal.util.HtmlEntities;

import java.util.List;

/**
 * Created by MINAMI on 2015/11/13.
 */
public final class TweetTextUtils {
    private static final String PHOTO_TYPE = "photo";

    private TweetTextUtils() {
    }

    public static MediaEntity getLastPhotoEntity(TweetEntities entities) {
        if(entities == null) {
            return null;
        } else {
            List mediaEntityList = entities.media;
            if(mediaEntityList != null && !mediaEntityList.isEmpty()) {
                for(int i = mediaEntityList.size() - 1; i >= 0; --i) {
                    MediaEntity entity = (MediaEntity)mediaEntityList.get(i);
                    if(entity.type != null && entity.type.equals("photo")) {
                        return entity;
                    }
                }

                return null;
            } else {
                return null;
            }
        }
    }

    public static boolean hasPhotoUrl(TweetEntities entities) {
        return getLastPhotoEntity(entities) != null;
    }
}