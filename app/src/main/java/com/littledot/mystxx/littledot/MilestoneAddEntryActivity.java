package com.littledot.mystxx.littledot;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import domains.DotCategory;
import domains.DotValues;
import domains.Image;

/**
 * Created by juanlabrador on 18/08/15.
 */
public class MilestoneAddEntryActivity extends BaseFeedActivity {

    private static int CATEGORY = 8;
    private ListView mListMilestones;
    private View mCustomView;
    private DotCategory mDotCategory;
    private DotValues mValues;
    private String mEntryDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_add_entry);
        navigationToolbarWithIcon(R.mipmap.img_navbar_milestones);
        changeNotificationBarColor(R.color.milestone_text);

        mDotCategory = new DotCategory();
        mDotCategory.setId(UUID.randomUUID().toString().toUpperCase());
        mDotCategory.setCategoryId(CATEGORY);
        mValues = new DotValues();
        mValues.setValue(-1);
        mEntryDate = getIntent().getStringExtra("entry_date");

        init();
    }

    private void init() {
        mCustomView = getLayoutInflater().inflate(R.layout.custom_simple_list_view, null, false);
        mListMilestones = (ListView) mCustomView.findViewById(R.id.list_add_entry);
        mListMilestones.addFooterView(
                getLayoutInflater().inflate(R.layout.custom_footer_empty, mListMilestones, false), null, false);
        mListMilestones.setAdapter(new ArrayAdapter<>(this,
                R.layout.custom_item_list_add_entry_milestone,
                milestoneList()));

        createEntryView(mCustomView);

        mListMilestones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                invalidateOptionsMenu();
                mValues.setValue(getValue(position));
            }
        });

        setDateEditInButton(mEntryDate);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mValues.getValue() != -1)
            getMenuInflater().inflate(R.menu.menu_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        List<Image> mGallery = new ArrayList<>();
        if (id == R.id.save_entry) {
            // Get all new photos add
            if (getPhotos() != null) {
                for (Bitmap photo : getPhotos()) {
                    Image image = new Image();
                    image.setId(UUID.randomUUID().toString().toUpperCase());
                    image.setBytes(getBytesFromBitmap(photo));
                    mGallery.add(image);
                }
                if (!mGallery.isEmpty()) {
                    mDotCategory.setImages(mGallery);
                }
            } else // If there isn't new images, not add the sames
                mDotCategory.setImages(null);
            mDotCategory.setTag(getTagPhotos());
            mDotCategory.setEntryData(getJSONArrayFromListDot(mValues));
            mDotCategory.setDate(getDateTime());
            addNewDot(mDotCategory);
        }
        overridePendingTransition(R.anim.anim_nothing, R.anim.anim_out);
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.choose_time_button:
                showDateDialog(mDotCategory.getDate(), R.style.MilestoneStyleDialog);
                break;
        }
    }

    /**
     * Get list milestone without item saved in db
     * @return
     */
    private List<String> milestoneList() {
        List<DotCategory> miles = getDotsByCategory(CATEGORY);
        List<DotValues> values = new ArrayList<>();
        for (DotCategory dot : miles) {
            values.add(getDotFromJSONArray(dot.getEntryData()));
        }

        List<String> newList = new ArrayList<>();

        for (int i = 0; i < getResources().getStringArray(R.array.milestone_list).length; i++) {
            newList.add(getResources().getStringArray(R.array.milestone_list)[i]);
        }

        for (DotValues milestone : values) {
            newList.remove(milestone.getValue());
        }

        return newList;
    }

    /**
     * Get value in list when there is little elements
     * @return
     */
    private int getValue(int position) {
        List<String> newList = milestoneList();

        for (int i = 0; i < getResources().getStringArray(R.array.milestone_list).length; i++) {
            if (newList.get(position).equals(getResources().getStringArray(R.array.milestone_list)[i]))
                return i;
        }
        return 0;
    }
}
