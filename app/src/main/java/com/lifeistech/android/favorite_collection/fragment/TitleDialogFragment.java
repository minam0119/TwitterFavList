package com.lifeistech.android.favorite_collection.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lifeistech.android.favorite_collection.R;
import com.lifeistech.android.favorite_collection.model.Category;

import java.util.ArrayList;


public class TitleDialogFragment extends DialogFragment {
    private static final String TWEET_ID = "tweet_id";
    private static final String[] CATEGORY_COLORS = {"#ff00ed", "#00fff2", "#ff9100", "#19ff00"};

    public static TitleDialogFragment createInstance(Long tweetId) {
        Bundle args = new Bundle();
        if (tweetId != null) {
            args.putLong(TWEET_ID, tweetId);
        }
        TitleDialogFragment fragment = new TitleDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private TextView titletx;
    private ImageView okbt;
    private Toolbar toolbar;
    private EditText txtitle;
    private RadioGroup group;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_title_dialog, null);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("新しいリストを作成");
        toolbar.setTitleTextColor(Color.WHITE);


        titletx = (TextView) view.findViewById(R.id.titletx);
        okbt = (ImageView) view.findViewById(R.id.okbt);
        okbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = txtitle.getText().toString();

                RadioButton button = (RadioButton) group.findViewById(group.getCheckedRadioButtonId());
                int color = Color.parseColor((String) button.getTag());

                Category category = new Category();
                category.name = title;
                category.color = color;
                category.ids = new ArrayList<Long>();

                Bundle args = getArguments();
                long tweetId = args != null ? args.getLong(TWEET_ID, -1) : -1;
                if (tweetId != -1) {
                    category.ids.add(tweetId);
                }
                category.save();

                Fragment fragment = getParentFragment();
                if (fragment instanceof CreateDialogListener) {
                    ((CreateDialogListener) fragment).onCreateCategory(category);
                }
                dismiss();
            }
        });
        txtitle = (EditText) view.findViewById(R.id.titletx);
        group = (RadioGroup) view.findViewById(R.id.radioGroup);
        for (int i = 0; i < group.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) group.getChildAt(i);
            GradientDrawable drawable = (GradientDrawable) radioButton.getBackground();
            drawable.setColor(Color.parseColor(CATEGORY_COLORS[i]));
            drawable.setAlpha(125);
            radioButton.setBackgroundDrawable(drawable);
            radioButton.setTag(CATEGORY_COLORS[i]);
        }

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                for (int i = 0; i < group.getChildCount(); i++) {
                    RadioButton radioButton = (RadioButton) group.getChildAt(i);
                    GradientDrawable drawable = (GradientDrawable) radioButton.getBackground();
                    drawable.setColor(Color.parseColor(CATEGORY_COLORS[i]));
                    if (radioButton.getId() == id) {
                        // 選択されているボタン
                        drawable.setAlpha(255);
                    } else {
                        // 選択されていないボタンbg
                        drawable.setAlpha(125);
                    }
                    radioButton.setBackgroundDrawable(drawable);
                }

            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        return builder.create();
    }


    public interface CreateDialogListener {
        void onCreateCategory(Category category);
    }
}
