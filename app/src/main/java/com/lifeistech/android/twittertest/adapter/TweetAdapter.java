package com.lifeistech.android.twittertest.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lifeistech.android.twittertest.BusHolder;
import com.lifeistech.android.twittertest.R;
import com.lifeistech.android.twittertest.event.AddCategoryEvent;
import com.lifeistech.android.twittertest.fragment.FavListFragment;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.FavoriteService;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.twitter.sdk.android.tweetui.internal.util.AspectRatioImageView;

import java.util.List;

/**
 * Created by MINAMI on 2015/09/19.
 */
public class TweetAdapter extends ArrayAdapter<Tweet> {

    TwitterApiClient twitterApiClient;

    public TweetAdapter(Context context, int resource) {
        super(context, resource);
        twitterApiClient = TwitterCore.getInstance().getApiClient();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.item_tweet, null);
            viewHolder = new ViewHolder(convertView);
            //リプライボタンを押した時
            viewHolder.replyBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    long tweetId = (Long) view.getTag();
                    TweetComposer.Builder builder = new TweetComposer.Builder(getContext()).text("");
                    builder.show();
                    /*final String message = "";
                    StatusesService statusesService = twitterApiClient.getStatusesService();
                    statusesService.update(
                            message, // message
                            tweetId,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            new Callback<Tweet>() {
                                @Override
                                public void success(Result<Tweet> result) {
                                    TweetComposer.Builder builder = new TweetComposer.Builder(getContext()).text("");
                                    builder.show();
                                }

                                @Override
                                public void failure(TwitterException e) {

                                }
                            }
                    );*/

                    //リプライの処理

                }
            });

            //リツイートボタンを押した時
            viewHolder.retweetBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    long tweetId = (Long) view.getTag();
                    StatusesService statusesService = (StatusesService) twitterApiClient.getStatusesService();
                    statusesService.retweet(tweetId, false, new Callback<Tweet>() {
                        @Override
                        public void success(Result<Tweet> result) {
                            //画像を暗くする
                            Toast.makeText(getContext(), result.data.user.screenName + "さんのツイートをリツイートしました", Toast.LENGTH_SHORT).show();
                            viewHolder.retweetBtn.setImageResource(R.drawable.retweet_green);
                        }

                        @Override
                        public void failure(TwitterException e) {

                        }
                    });


                }
            });

            //お気に入りボタンを押した時
            viewHolder.favoriteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FavoriteService favoriteService = (FavoriteService) twitterApiClient.getFavoriteService();
                    // ID受け取る
                    long tweetId = (Long) view.getTag();
                    // お気に入りにする処理
                    favoriteService.create(tweetId, false, new Callback<Tweet>() {
                        @Override
                        public void success(Result<Tweet> result) {
                            //画像を暗くする
                            Toast.makeText(getContext(), result.data.user.screenName + "さんの投稿をお気に入りに登録しました", Toast.LENGTH_SHORT).show();
                            viewHolder.favoriteBtn.setImageResource(R.drawable.fav_color);
                        }

                        @Override
                        public void failure(TwitterException e) {

                        }
                    });
                }
            });

            //リスト追加ボタンを押した時
            convertView.setTag(viewHolder);
            viewHolder.listBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BusHolder.post(new AddCategoryEvent(getItem(position).id));
                }
            });
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Tweet item = getItem(position);
        viewHolder.nicknameText.setText(item.user.name);
        viewHolder.usernameText.setText("@" + item.user.screenName);

        viewHolder.tweetText.setText(item.text);
        Glide.with(getContext()).load(item.user.profileImageUrl).into(viewHolder.icon);
        viewHolder.favoriteBtn.setTag(item.getId());
        if (item.favorited) {
            viewHolder.favoriteBtn.setImageResource(R.drawable.fav_color);
        } else {
            viewHolder.favoriteBtn.setImageResource(R.drawable.fav_gray);
        }

        viewHolder.retweetBtn.setTag(item.getId());
        if (item.retweeted) {
            viewHolder.retweetBtn.setImageResource(R.drawable.retweet_green);
        } else {
            viewHolder.retweetBtn.setImageResource(R.drawable.retweet_light);
        }
        viewHolder.replyBtn.setTag(item.getId());

        if (item.entities != null && item.entities.media != null && !item.entities.media.isEmpty()) {
            final MediaEntity mediaEntity = item.entities.media.get(0);
            Log.d("TweetAdapter", mediaEntity.mediaUrlHttps);
            viewHolder.mediaImageView.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(mediaEntity.mediaUrlHttps).into(viewHolder.mediaImageView);
        } else {
            viewHolder.mediaImageView.setVisibility(View.GONE);
            convertView.setBackgroundColor(Color.WHITE);
        }
        return convertView;
    }

    public class ViewHolder {
        TextView nicknameText;
        ImageView icon;
        TextView usernameText;
        TextView tweetText;
        ImageView replyBtn;
        ImageView retweetBtn;
        ImageView favoriteBtn;
        ImageView listBtn;
        ImageView mediaImageView;

        ViewHolder(View view) {
            nicknameText = (TextView) view.findViewById(R.id.nickname);
            icon = (ImageView) view.findViewById(R.id.icon);
            usernameText = (TextView) view.findViewById(R.id.username);
            tweetText = (TextView) view.findViewById(R.id.tweettext);
            replyBtn = (ImageView) view.findViewById(R.id.btreply);
            retweetBtn = (ImageView) view.findViewById(R.id.btretweet);
            favoriteBtn = (ImageView) view.findViewById(R.id.btfavorite);
            listBtn = (ImageView) view.findViewById(R.id.btlist);
            mediaImageView = (ImageView) view.findViewById(R.id.media_image);
        }
    }
}
