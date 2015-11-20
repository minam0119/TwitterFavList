package com.lifeistech.android.twittertest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lifeistech.android.twittertest.model.Category;
import com.lifeistech.android.twittertest.R;

/**
 * Created by MINAMI on 2015/06/13.
 */
public class FavListAdapter extends ArrayAdapter<Category> {

    public FavListAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_fav, null);
        }

        Category item = getItem(position);

        TextView nameTextView = (TextView)convertView.findViewById(R.id.textView3);
        nameTextView.setText(item.name);

        TextView colorText = (TextView)convertView.findViewById(R.id.textView2);
        colorText.setBackgroundColor(item.color);

        return convertView;
    }
}
