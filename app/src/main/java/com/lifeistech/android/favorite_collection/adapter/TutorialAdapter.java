package com.lifeistech.android.favorite_collection.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lifeistech.android.favorite_collection.R;
import com.lifeistech.android.favorite_collection.fragment.FavListFragment;
import com.lifeistech.android.favorite_collection.fragment.LoginFragment;
import com.lifeistech.android.favorite_collection.fragment.TutorialFragment;
import com.lifeistech.android.favorite_collection.fragment.TweetListFragment;

/**
 * Created by MINAMI on 2015/12/25.
 */
public class TutorialAdapter extends FragmentPagerAdapter {

    private LoginFragment loginFragment;

    public TutorialAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return TutorialFragment.newInstance("まず、タイムラインのツイートの\n" +
                        "リスト追加ボタンを押します", R.drawable.tr01);
            case 1:
                return TutorialFragment.newInstance("リスト画面に移動するので、\n" +
                        "追加先のリストを選択、または追加します。\n" +
                        "このとき、\n" +
                        "ツイートがリストに追加され、お気に入りに登録されます。", R .drawable.tr02);
            case 2:
                return TutorialFragment.newInstance("また、同様に\n" +
                        "すべてのお気に入りからも\n" +
                        "他のリストに追加することができます。", R .drawable.tr03);
            case 3:
                if (loginFragment == null) {
                    loginFragment = new LoginFragment();
                }
                return loginFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
