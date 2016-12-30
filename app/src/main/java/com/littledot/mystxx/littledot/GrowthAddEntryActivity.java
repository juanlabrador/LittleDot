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

import domains.Conversion;
import domains.DotCategory;
import domains.DotValues;
import domains.Image;

/**
 * Created by juanlabrador on 13/08/15.
 */
public class GrowthAddEntryActivity extends BaseFeedActivity {

    private static int CATEGORY = 6;
    private DotCategory mDotCategory;
    private DotValues mValueWeight;
    private DotValues mValueHeight;
    private DotValues mValueHead;
    private List<DotValues> mValues;

    private DiscreteSeekBar mWeight, mHeight, mHead;
    private TextView mWeightNumber, mHeightNumber, mHeadNumber, mWeightLabel, mHeightLabel, mHeadLabel;
    private View mCustomView;
    private List<Image> mPhotos;
    private Conversion mConversion;
    private String mEntryDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_add_entry);
        mConversion = new Conversion();
        changeNotificationBarColor(R.color.growth_text);
        navigationToolbarWithIcon(R.mipmap.img_navbar_growth);

        if (getIntent().getParcelableExtra("dot") != null) {
            mDotCategory = getIntent().getParcelableExtra("dot");
            // Here get Json array how dot
            mValues = getListDotFromJSONArray(mDotCategory.getEntryData());
            mValueWeight = mValues.get(0);
            mValueHeight = mValues.get(1);
            // if child have less 3 years
            if (calculateAge())
                if (mValues.size() > 2)
                    mValueHead = mValues.get(2);
                else
                    createDotHead();

            mEntryDate = mDotCategory.getDate();
        } else {  // Add New Entry
            mDotCategory = new DotCategory();
            mDotCategory.setId(UUID.randomUUID().toString().toUpperCase());
            mDotCategory.setCategoryId(CATEGORY);
            createDotWeight();
            createDotHeight();
            if (calculateAge()) {
                createDotHead();
            }
            mEntryDate = getIntent().getStringExtra("entry_date");
        }

        init();
    }

    private void init() {
        mCustomView = getLayoutInflater().inflate(R.layout.custom_growth_add_entry, null);

        mWeightLabel = (TextView) mCustomView.findViewById(R.id.type_weight);
        mHeightLabel = (TextView) mCustomView.findViewById(R.id.type_height);
        mHeadLabel = (TextView) mCustomView.findViewById(R.id.type_head);

        if (getAppPreferences().getInt("unit", 0) == 0) {
            mWeightLabel.setText(getString(R.string.add_growth_weight));
            mHeightLabel.setText(getString(R.string.add_growth_height));
            mHeadLabel.setText(getString(R.string.add_growth_head));
        } else {
            mWeightLabel.setText(getString(R.string.add_growth_weight_pounds));
            mHeightLabel.setText(getString(R.string.add_growth_height_inches));
            mHeadLabel.setText(getString(R.string.add_growth_head_inches));
        }

        mWeight = (DiscreteSeekBar) mCustomView.findViewById(R.id.seek_bar_weight);
        mWeight.setMax(1000);
        mWeight.setIndicatorPopupEnabled(false);
        mWeightNumber = (TextView) mCustomView.findViewById(R.id.content_weight);

        mHeight = (DiscreteSeekBar) mCustomView.findViewById(R.id.seek_bar_height);
        mHeight.setMax(430);
        mHeight.setIndicatorPopupEnabled(false);
        mHeightNumber = (TextView) mCustomView.findViewById(R.id.content_height);

        if (calculateAge()) {
            mHead = (DiscreteSeekBar) mCustomView.findViewById(R.id.seek_bar_head);
            mHead.setMax(900);
            mHead.setIndicatorPopupEnabled(false);
            mHeadNumber = (TextView) mCustomView.findViewById(R.id.content_head);
            mCustomView.findViewById(R.id.type_head).setVisibility(View.VISIBLE);
            mHead.setVisibility(View.VISIBLE);
            mHeadNumber.setVisibility(View.VISIBLE);
        }

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

            if (getAppPreferences().getInt("unit", 0) == 0){
                mWeight.setProgress((int) ((mValueWeight.getDetailValue() * 10.0)));
                mWeightNumber.setText(Float.toString(mValueWeight.getDetailValue()));

                mHeight.setProgress((int) ((mValueHeight.getDetailValue() * 2.0)));
                mHeightNumber.setText(Float.toString(mValueHeight.getDetailValue()));

                if (calculateAge()) {
                    if (mValueHead != null) {
                        mHead.setProgress((int) ((mValueHead.getDetailValue() * 10.0)));
                        mHeadNumber.setText(Float.toString(mValueHead.getDetailValue()));
                    }
                }

            } else {
                mWeight.setProgress((int) ((mValueWeight.getDetailValue() * 10.0)));
                mWeightNumber.setText(mConversion.kgToPounds((double) mValueWeight.getDetailValue()));

                mHeight.setProgress((int) ((mValueHeight.getDetailValue() * 2.0)));
                mHeightNumber.setText(mConversion.cmToInches((double) (mValueHeight.getDetailValue())));

                if (calculateAge()) {
                    if (mValueHead != null) {
                        mHead.setProgress((int) ((mValueHead.getDetailValue() * 10.0)));
                        mHeadNumber.setText(mConversion.cmToInches((double) mValueHead.getDetailValue()));
                    }
                }
            }
        }

        mWeight.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
            @Override
            public int transform(int i) {
                mValueWeight.setValue(0);
                if (getAppPreferences().getInt("unit", 0) == 0){
                    mWeightNumber.setText(Double.toString(i / 10.0));
                    mValueWeight.setDetailValue((float) (i / 10.0));
                } else {
                    mWeightNumber.setText(mConversion.kgToPounds((i / 10.0)));
                    mValueWeight.setDetailValue((float) (i / 10.0));
                }
                invalidateOptionsMenu();
                return 0;
            }
        });

        mHeight.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
            @Override
            public int transform(int i) {
                mValueHeight.setValue(1);
                if (getAppPreferences().getInt("unit", 0) == 0){
                    mHeightNumber.setText(Double.toString(i / 2.0));
                    mValueHeight.setDetailValue((float) (i / 2.0));
                } else {
                    mHeightNumber.setText(mConversion.cmToInches(i / 2.0));
                    mValueHeight.setDetailValue((float) (i / 2.0));
                }
                invalidateOptionsMenu();
                return 0;
            }
        });

        if (calculateAge())
            mHead.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
                @Override
                public int transform(int i) {
                    mValueHead.setValue(2);

                    if (getAppPreferences().getInt("unit", 0) == 0){
                        mHeadNumber.setText(Float.toString((float)(i / 10.0)));
                        mValueHead.setDetailValue((float) (i / 10.0));
                    } else {
                        mHeadNumber.setText(mConversion.cmToInches((i / 10.0)));
                        mValueHead.setDetailValue((float) (i / 10.0));
                    }

                    invalidateOptionsMenu();
                    return 0;
                }
            });

        setDateEditInButton(mEntryDate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (calculateAge()) {
            if (mValueWeight.getDetailValue() != 0 &&
                    mValueHeight.getDetailValue() != 0 &&
                    mValueHead.getDetailValue() != 0)
                getMenuInflater().inflate(R.menu.menu_save, menu);
        } else {
            if (mValueWeight.getDetailValue() != 0 &&
                    mValueHeight.getDetailValue() != 0)
                getMenuInflater().inflate(R.menu.menu_save, menu);
        }

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
            mDotCategory.setEntryData(getJSONArrayFromGrowthDot(mValueWeight, mValueHeight, mValueHead));
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
                showDateDialog(mEntryDate, R.style.GrowthStyleDialog);
                break;
        }
    }

    /**
     * init dot for weight
     */
    private void createDotWeight() {
        mValueWeight = new DotValues();
        mValueWeight.setValue(-1);
    }

    /**
     * init dot for height
     */
    private void createDotHeight() {
        mValueHeight = new DotValues();
        mValueHeight.setValue(-1);
    }

    /**
     * init dot for head circumference
     */
    private void createDotHead() {
        mValueHead = new DotValues();
        mValueHead.setValue(-1);
    }
}
