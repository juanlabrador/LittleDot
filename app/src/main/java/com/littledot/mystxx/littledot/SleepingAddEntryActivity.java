package com.littledot.mystxx.littledot;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import domains.DotCategory;
import domains.DotValues;
import domains.Image;

/**
 * Created by CharlesSio on 19/08/15.
 */

public class SleepingAddEntryActivity extends BaseFeedActivity {

    private static int CATEGORY = 1;
    private DotCategory mDotCategory;
    private DotValues mValues;

    private DiscreteSeekBar mHour;
    private TextView mNumber;
    private View mCustomView;
    private List<Image> mPhotos;
    private String mEntryDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_add_entry);
        changeNotificationBarColor(R.color.sleeping_text);
        navigationToolbarWithIcon(R.mipmap.img_navbar_sleep);

        if (getIntent().getParcelableExtra("dot") != null) {
            mDotCategory = getIntent().getParcelableExtra("dot");
            mValues = getDotFromJSONArray(mDotCategory.getEntryData());
            mEntryDate = mDotCategory.getDate();
        } else {  // Add New Entry
            mDotCategory = new DotCategory();
            mDotCategory.setId(UUID.randomUUID().toString().toUpperCase());
            mDotCategory.setCategoryId(CATEGORY);
            mValues = new DotValues();
            mValues.setValue(0);
            mEntryDate = getIntent().getStringExtra("entry_date");
        }

        init();
    }

    private void init() {
        mCustomView = getLayoutInflater().inflate(R.layout.custom_sleeping_add_entry, null, false);

        mNumber = (TextView) mCustomView.findViewById(R.id.content_sleeping);

        mHour = (DiscreteSeekBar) mCustomView.findViewById(R.id.seek_bar_sleeping);
        mHour.setMax(24);
        mHour.setIndicatorPopupEnabled(false);

        if (mValues.getDetailValue() > 0) {
            mHour.setProgress((int) ((mValues.getDetailValue()) * 2.0));
            mNumber.setText(Float.toString(mValues.getDetailValue()));
        }

        mHour.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
            @Override
            public int transform(int i) {
                mNumber.setText(Double.toString((i) / 2.0));
                mValues.setDetailValue((float)((i) / 2.0));
                invalidateOptionsMenu();
                return 0;
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

        setDateEditInButton(mEntryDate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mValues.getDetailValue() != 0)
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
            if (getIntent().getParcelableExtra("dot") != null) {
                updateDot(mDotCategory);
            } else {
                addNewDot(mDotCategory);
            }
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
                showDateDialog(mDotCategory.getDate(), R.style.SleepingStyleDialog);
                break;
        }
    }
}
