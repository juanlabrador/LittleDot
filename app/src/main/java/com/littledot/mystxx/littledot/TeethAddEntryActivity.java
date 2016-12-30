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

public class TeethAddEntryActivity extends BaseFeedActivity {

    private static int CATEGORY = 9;
    private DotCategory mDotCategory;
    private DotValues mValues;
    private ListView mListTeeth;
    private View mCustomView;
    private String mEntryDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_add_entry);

        mDotCategory = new DotCategory();
        mDotCategory.setId(UUID.randomUUID().toString().toUpperCase());
        mDotCategory.setCategoryId(CATEGORY);
        mValues = new DotValues();
        mValues.setValue(-1);
        mEntryDate = getIntent().getStringExtra("entry_date");

        changeNotificationBarColor(R.color.teeth_text);
        navigationToolbarWithIcon(R.mipmap.img_navbar_teeth);

        init();
    }

    private void init() {
        mCustomView = getLayoutInflater().inflate(R.layout.custom_simple_list_view, null);
        mListTeeth = (ListView) mCustomView.findViewById(R.id.list_add_entry);
        mListTeeth.addFooterView(
                getLayoutInflater().inflate(R.layout.custom_footer_empty, mListTeeth, false), null, false);

        mListTeeth.setAdapter(new ArrayAdapter<>(this,
                R.layout.custom_item_list_add_entry_teeth,
                teethList()));

        createEntryView(mCustomView);

        mListTeeth.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mValues.setValue(getValue(position));
                invalidateOptionsMenu();
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
            overridePendingTransition(R.anim.anim_nothing, R.anim.anim_out);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.choose_time_button:
                showDateDialog(mDotCategory.getDate(), R.style.TeethStyleDialog);
                break;
        }
    }

    /**
     * Get list teeth without tooth saved in db
     * @return
     */
    private List<String> teethList() {
        List<DotCategory> teeth = getDotsByCategory(CATEGORY);
        List<DotValues> values = new ArrayList<>();
        for (DotCategory tooth : teeth) {
            values.add(getDotFromJSONArray(tooth.getEntryData()));
        }

        List<String> newList = new ArrayList<>();

        for (int i = 0; i < getResources().getStringArray(R.array.teeth_list).length; i++) {
            newList.add(getResources().getStringArray(R.array.teeth_list)[i]);
        }

        for (DotValues tooth : values) {
            newList.remove(tooth.getValue());
        }

        return newList;
    }

    /**
     * Get value in list when there is little elements
     * @return
     */
    private int getValue(int position) {
        List<String> newList = teethList();

        for (int i = 0; i < getResources().getStringArray(R.array.teeth_list).length; i++) {
            if (newList.get(position).equals(getResources().getStringArray(R.array.teeth_list)[i]))
                return i;
        }
        return 0;
    }
 }
