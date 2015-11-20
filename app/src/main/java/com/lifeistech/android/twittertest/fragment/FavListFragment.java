package com.lifeistech.android.twittertest.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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
import com.lifeistech.android.twittertest.model.Category;
import com.lifeistech.android.twittertest.adapter.FavListAdapter;
import com.lifeistech.android.twittertest.R;

import java.util.List;


public class FavListFragment extends Fragment {

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
        //長押しでdelete
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            //ダイアログを作成
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
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
        });

        listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(mAdapter);

        //ListをClickで移動
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                //title2 = mAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), FavTweetListActivity.class);
                if (position == 0) {
                    intent.putExtra("categoryName", "");
                } else {
                    intent.putExtra("categoryName", mAdapter.getItem(position).name);
                }
                startActivity(intent);
            }
        });
    }

    private void showCreateDialog() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.activity_title_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("");
        builder.setView(layout);
        final AlertDialog dialog = builder.show();

        Button btok = (Button) layout.findViewById(R.id.okbt);
        btok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText txtitle = (EditText) layout.findViewById(R.id.titletx);
                String title = txtitle.getText().toString();

                RadioGroup group = (RadioGroup) layout.findViewById(R.id.radioGroup);
                RadioButton button = (RadioButton) group.findViewById(group.getCheckedRadioButtonId());
                if (button == null) {
                    dialog.dismiss();
                    return;
                }
                int color = Color.parseColor((String) button.getTag());

                Category category = new Category();
                category.name = title;
                category.color = color;
                category.save();
                mAdapter.add(category);
                dialog.dismiss();
            }
        });
    }
}