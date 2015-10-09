package com.lifeistech.android.twittertest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class FavListActivity extends AppCompatActivity {

    ListView listView;
    FavListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_list);

        mAdapter = new FavListAdapter(this, 0);

        Category category = new Category();
        category.name = "すべてのお気に入り";
        category.color = Color.parseColor(("#aaFF0000"));
        mAdapter.add(category);

        listView = (ListView) findViewById(R.id.listView);
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

                AlertDialog.Builder builder = new AlertDialog.Builder(FavListActivity.this)
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

        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(mAdapter);

        //ListをClickで移動
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                //title2 = mAdapter.getItem(position);
                Intent intent = new Intent(FavListActivity.this, FavTweetListActivity.class);
                if (position == 0) {
                    intent.putExtra("categoryName", "");
                } else {
                    intent.putExtra("categoryName", mAdapter.getItem(position).name);
                }
                startActivity(intent);
            }
        });
    }


    public void plus(View v) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.activity_title_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setView(layout);
        final AlertDialog dialog = builder.show();

        Button btok = (Button) layout.findViewById(R.id.okbt);
        btok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //EditTextの定義
                //titleにEditTextの内容を代入
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
                mAdapter.add(category);

                dialog.dismiss();
            }
        });

    }
}