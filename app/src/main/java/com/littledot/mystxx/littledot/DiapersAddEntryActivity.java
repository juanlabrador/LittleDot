package com.littledot.mystxx.littledot;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import domains.DotCategory;
import domains.DotValues;
import domains.Image;


/**
 * Created by juanlabrador on 18/08/15.
 */
public class DiapersAddEntryActivity extends BaseFeedActivity implements View.OnClickListener {

    private static int CATEGORY = 2;
    private DotCategory mDotCategory;
    private List<DotValues> mValues;
    private DotValues mValuesPee;
    private DotValues mValuesPoo;

    private ListView mListPoo;
    private RadioButton mPee;
    private View mCustomView;
    private boolean isCheck = false;
    private List<Image> mPhotos;
    private String mEntryDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_add_entry);
        changeNotificationBarColor(R.color.diapers_text);
        navigationToolbarWithIcon(R.mipmap.img_navbar_diapers);

        if (getIntent().getParcelableExtra("dot") != null) {
            mDotCategory = getIntent().getParcelableExtra("dot");
            mValues = getListDotFromJSONArray(mDotCategory.getEntryData());
            if (mValues.get(0).getGroup() == 0) {
                mValuesPee = mValues.get(0);
                createDotPoo();
            } else {
                createDotPee();
                mValuesPoo = mValues.get(0);
            }
            if (mValues.size() > 1) {
                mValuesPoo = mValues.get(1);
            }
            mEntryDate = mDotCategory.getDate();
        } else {  // Add New Entry
            mDotCategory = new DotCategory();
            mDotCategory.setId(UUID.randomUUID().toString().toUpperCase());
            mDotCategory.setCategoryId(CATEGORY);
            createDotPee();
            createDotPoo();
            mEntryDate = getIntent().getStringExtra("entry_date");
        }

        init();
    }

    private void init() {
        mCustomView = getLayoutInflater().inflate(R.layout.custom_diapers_add_entry, null);
        mPee = (RadioButton) mCustomView.findViewById(R.id.type_pee);
        mPee.setButtonDrawable(R.drawable.style_radio_diapers);
        mPee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isCheck) {
                    mPee.setChecked(true);
                    isCheck = true;
                    mValuesPee.setValue(0);
                } else{
                    mPee.setChecked(false);
                    isCheck = false;
                    mValuesPee.setValue(-1);
                }
                invalidateOptionsMenu();
            }
        });

        mListPoo = (ListView) mCustomView.findViewById(R.id.list_diapers_add_entry);
        mListPoo.setAdapter(new ArrayAdapter<>(this,
                R.layout.custom_item_list_add_entry_diapers,
                getResources().getStringArray(R.array.diapers_poo_list)));
        mListPoo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mValuesPoo.setValue(position);
                invalidateOptionsMenu();
            }
        });

        createEntryView(mCustomView);


        // Load photos from edit button
        if (mDotCategory.getImages() != null) {
            mPhotos = mDotCategory.getImages();
            for (Image image : mPhotos) {
                addGallery(getImageFromBytes(image.getBytes()), image);
            }
            setTagPhotos(mDotCategory.getTag());
            changeStyleAttach(mDotCategory.getId());
        }


        if (mValues != null) {
            if (mValuesPee.getValue() != -1) {
                mPee.setChecked(true);
                isCheck = true;
            }
            if (mValuesPoo.getValue() != -1)
                mListPoo.setItemChecked(mValuesPoo.getValue(), true);
        }
        setDateEditInButton(mEntryDate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mValuesPee.getValue() != -1 || mValuesPoo.getValue() != -1)
            getMenuInflater().inflate(R.menu.menu_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        List<Image> mGallery = new ArrayList<>();
        if (mValuesPee.getValue() == -1)
            mValuesPee = null;
        if (mValuesPoo.getValue() == -1)
            mValuesPoo = null;

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
            mDotCategory.setEntryData(getJSONArrayFromDiapersDot(mValuesPee, mValuesPoo));
            mDotCategory.setDate(getDateTime());
            if (getIntent().getParcelableExtra("dot") != null) {
                updateDot(mDotCategory);
            } else {
                addNewDot(mDotCategory);
            }
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
                showDateTimeDialog(mDotCategory.getDate(), R.style.DiapersStyleDialog, R.color.diapers_text);
                break;
        }
    }

    /**
     * Init dot for Pee
     */
    private void createDotPee() {
        mValuesPee = new DotValues();
        mValuesPee.setGroup(0);
        mValuesPee.setValue(-1);
    }

    /**
     * Init dot for poo
     */
    private void createDotPoo() {
        mValuesPoo = new DotValues();
        mValuesPoo.setGroup(1);
        mValuesPoo.setValue(-1);
    }
}
