package com.lifeistech.android.twittertest.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.activeandroid.query.Select;
import com.lifeistech.android.twittertest.activity.FavTweetListActivity;
import com.lifeistech.android.twittertest.activity.TitleDialogFragment;
import com.lifeistech.android.twittertest.model.Category;
import com.lifeistech.android.twittertest.adapter.FavListAdapter;
import com.lifeistech.android.twittertest.R;

import java.util.List;


public class FavListFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, TitleDialogFragment.CreateDialogListener {

    ListView listView;
    FavListAdapter mAdapter;
    List<Category> mCategoryList;
    FloatingActionButton mFab;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fav_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new FavListAdapter(getActivity(), 0);
        mFab = (FloatingActionButton) view.findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateDialog();
            }
        });

        Category allCategory = new Category();
        allCategory.name = "すべてのお気に入り";
        allCategory.color = Color.parseColor("#aaFF0000");
        mAdapter.add(allCategory);

        // 今の保存されているカテゴリーを取り出す
        mCategoryList = new Select().from(Category.class).execute();
        for (Category category : mCategoryList) {
            mAdapter.add(category);
        }

        listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(mAdapter);
        //ListをClickで移動
        listView.setOnItemClickListener(this);
        //長押しでdelete
        listView.setOnItemLongClickListener(this);
    }

    private void showCreateDialog() {
        TitleDialogFragment titleDialogFragment = new TitleDialogFragment();
        titleDialogFragment.show(getChildFragmentManager(), "dialog");
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        //title2 = mAdapter.getItem(position);
        Intent intent = new Intent(getActivity(), FavTweetListActivity.class);
        if (position == 0) {
            intent.putExtra("categoryName", "");
        } else {
            intent.putExtra("categoryName", mAdapter.getItem(position).name);
        }
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
        // ダイアログの作成
        final Category selectedItem = mAdapter.getItem(position);
        //0番のときは何もしない
        if (position == 0) {
            return true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setMessage(selectedItem.name + "を削除しますか?")
                .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Category selectedItem = mAdapter.getItem(position);
                        // ActiveAndroid内からも削除するためにdeleteメソッドを呼び出し
                        selectedItem.delete();
                        mAdapter.remove(selectedItem);
                        dialog.dismiss();
                    }
                }).setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        //ダイアログを表示
        builder.show();
        return true;
    }

    @Override
    public void onCreateCategory(Category category) {
        mAdapter.add(category);
    }
}