package com.lifeistech.android.twittertest.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lifeistech.android.twittertest.R;
import com.lifeistech.android.twittertest.model.Category;


public class TitleDialogFragment extends DialogFragment {
    TextView titletx;
    Button okbt;
    Toolbar toolbar;
    EditText txtitle;
    RadioGroup group;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_title_dialog, null);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("新しいリストを作成");
        toolbar.setTitleTextColor(Color.WHITE);

        titletx = (TextView) view.findViewById(R.id.titletx);
        okbt = (Button) view.findViewById(R.id.okbt);
        okbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = txtitle.getText().toString();

                RadioButton button = (RadioButton) group.findViewById(group.getCheckedRadioButtonId());
                int color = Color.parseColor((String) button.getTag());

                Category category = new Category();
                category.name = title;
                category.color = color;
                category.save();

                Fragment fragment = getParentFragment();
                if (fragment instanceof CreateDialogListener) {
                    ((CreateDialogListener) fragment).onCreateCategory(category);
                }
            }
        });
        txtitle = (EditText) view.findViewById(R.id.titletx);
        group = (RadioGroup) view.findViewById(R.id.radioGroup);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        return builder.create();
    }


    public interface CreateDialogListener {
        public void onCreateCategory(Category category);
    }
}
