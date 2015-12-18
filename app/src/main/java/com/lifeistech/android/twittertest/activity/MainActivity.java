package com.lifeistech.android.twittertest.activity;

import android.app.ActionBar;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;

import com.lifeistech.android.twittertest.BusHolder;
import com.lifeistech.android.twittertest.R;
import com.lifeistech.android.twittertest.adapter.TopPagerAdapter;
import com.lifeistech.android.twittertest.event.AddCategoryEvent;
import com.lifeistech.android.twittertest.fragment.TweetListFragment;
import com.squareup.otto.Subscribe;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TopPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        adapter = new TopPagerAdapter(getSupportFragmentManager());
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.mipmap.homeicon);
        tabLayout.getTabAt(1).setIcon(R.mipmap.listicon);

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {
                    toolbar.setTitle("タイムライン");
                } else if (position == 1) {
                    toolbar.setTitle("お気に入りリスト");
                }
            }
        });
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

    @Subscribe
    public void subscribeEvent(AddCategoryEvent event) {
        viewPager.setCurrentItem(1, true);
    }

}
