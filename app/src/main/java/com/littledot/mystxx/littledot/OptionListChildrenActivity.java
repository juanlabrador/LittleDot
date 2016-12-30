package com.littledot.mystxx.littledot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import adapters.OptionsSettingChildrenListAdapter;
import database.DataBaseHandler;
import domains.Child;

/**
 * Created by juanlabrador on 18/08/15.
 */
public class OptionListChildrenActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    public static OptionListChildrenActivity mActivity;
    private DataBaseHandler mDB;
    private List<Child> mChildrenEdits;
    private ListView mListChildren;
    private OptionsSettingChildrenListAdapter mAdapter;
    private LayoutInflater mLayoutInflater;
    private View mFooterList;
    private View mAddChildren;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_option_list_children);
        mActivity = this;
        mDB = new DataBaseHandler(this);
        mChildrenEdits = mDB.getAllChildChildren();
        init();

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDataChild();
    }

    private void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.other_menu_edit);

        setSupportActionBar(mToolbar);
        mListChildren = (ListView) findViewById(R.id.children_list_edit);
        mAdapter = new OptionsSettingChildrenListAdapter(this, mChildrenEdits);

        mLayoutInflater = getLayoutInflater();
        mFooterList = mLayoutInflater.inflate(R.layout.custom_footer_more_options_list_children, null);

        mAddChildren = mFooterList.findViewById(R.id.add_children);
        mAddChildren.setOnClickListener(this);

        mListChildren.addFooterView(mFooterList, null, false);

        mListChildren.setAdapter(mAdapter);
        mListChildren.setOnItemClickListener(this);
    }

    private void updateDataChild() {
        mChildrenEdits = mDB.getAllChildChildren();
        mAdapter = new OptionsSettingChildrenListAdapter(this, mChildrenEdits);
        mListChildren.setAdapter(mAdapter);
    }
    @Override
    public void onClick(View v) {
        startActivity(new Intent(this, OptionChildrenAddEditActivity.class));
        overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(this, OptionChildrenAddEditActivity.class)
                .putExtra("children", mChildrenEdits.get(position)));
        overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_nothing, R.anim.anim_out);
    }
}
