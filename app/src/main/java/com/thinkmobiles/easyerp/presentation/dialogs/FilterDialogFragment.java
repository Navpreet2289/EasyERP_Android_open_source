package com.thinkmobiles.easyerp.presentation.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.thinkmobiles.easyerp.R;
import com.thinkmobiles.easyerp.presentation.adapters.crm.SearchDialogAdapter;
import com.thinkmobiles.easyerp.presentation.holders.data.crm.FilterDH;
import com.thinkmobiles.easyerp.presentation.utils.Constants;

import java.util.ArrayList;


public class FilterDialogFragment extends AppCompatDialogFragment implements DialogInterface.OnClickListener {

    private EditText etSearch;
    private RecyclerView rvList;

    private SearchDialogAdapter searchAdapter;

    private ArrayList<FilterDH> filterList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        filterList = getArguments().getParcelableArrayList(Constants.KEY_FILTER_LIST);

        searchAdapter = new SearchDialogAdapter();
        searchAdapter.setOriginList(filterList);
        searchAdapter.setOnCardClickListener((view, position, viewType) -> {
            switch (view.getId()) {
                case R.id.cbItem:
                    int pos = filterList.indexOf(searchAdapter.getItem(position));
                    filterList.get(pos).selected = ((CheckBox) view).isChecked();
                    break;
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View parent = LayoutInflater.from(getContext()).inflate(R.layout.dialog_filter, null);

        etSearch = (EditText) parent.findViewById(R.id.etSearch_DF);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchAdapter.getFilter().filter(s);
            }
        });
        etSearch.setOnClickListener((v) -> etSearch.setText(""));
        etSearch.setOnKeyListener((v, keyCode, event) -> {
            if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                    && event.getAction() == KeyEvent.ACTION_DOWN) {
                searchAdapter.getFilter().filter(etSearch.getText());
                return true;
            }
            return false;
        });

        rvList = (RecyclerView) parent.findViewById(R.id.rvList_DF);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvList.setAdapter(searchAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AppTheme_FilterDialogStyle)
                .setView(parent)
                .setTitle(R.string.menu_filter)
                .setPositiveButton(R.string.dialog_btn_apply, this)
                .setNegativeButton(R.string.dialog_btn_cancel, this)
                .setNeutralButton(R.string.dialog_btn_remove, this);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Fragment target = getTargetFragment();
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                if (target != null) {
                    Intent intent = new Intent();
                    intent.putParcelableArrayListExtra(Constants.KEY_FILTER_LIST, filterList);
                    target.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                }
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                break;
            case DialogInterface.BUTTON_NEUTRAL:
                if (target != null) {
                    target.onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, null);
                }
                break;
        }
        dialog.dismiss();
    }
}
