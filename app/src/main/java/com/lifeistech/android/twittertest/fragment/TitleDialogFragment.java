package com.lifeistech.android.twittertest.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lifeistech.android.twittertest.R;
import com.lifeistech.android.twittertest.model.Category;

import java.util.ArrayList;


public class TitleDialogFragment extends DialogFragment {
    private static final String TWEET_ID = "tweet_id";

    public TitleDialogFragment createInstance(Long tweetId) {
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
    RadioButton pink,aqua,orange,green;

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        return builder.create();
    }


    public interface CreateDialogListener {
        void onCreateCategory(Category category);
    }
}
