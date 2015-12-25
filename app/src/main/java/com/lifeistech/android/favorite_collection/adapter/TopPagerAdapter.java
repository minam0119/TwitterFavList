package com.lifeistech.android.favorite_collection.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lifeistech.android.favorite_collection.fragment.FavListFragment;
import com.lifeistech.android.favorite_collection.fragment.TweetListFragment;

/**
 * Created by MINAMI on 2015/11/06.
 */
public class TopPagerAdapter extends FragmentPagerAdapter {

    public TopPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            // 左側のFragment
            case 0:
                return new TweetListFragment();
            // 右側のFragment
            case 1:
                return new FavListFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
