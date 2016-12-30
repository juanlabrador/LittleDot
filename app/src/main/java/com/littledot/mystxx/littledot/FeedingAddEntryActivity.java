package com.littledot.mystxx.littledot;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
 * Created by juanlabrador on 18/08/15.
 */
public class FeedingAddEntryActivity extends BaseFeedActivity implements View.OnClickListener {

    private static int CATEGORY = 0;
    private DotCategory mDotCategory;
    private DotValues mValues;
    private List<Image> mPhotos;

    private ListView mListFeedOption;
    private EditText mOtherValue;
    private DiscreteSeekBar mBottle;
    private DiscreteSeekBar mBreast;
    private TextView mLabelBottle;
    private TextView mNumberBottle;
    private TextView mNumberBreast;
    private String[] mFeedOption;
    private View mCustomView;
    private RelativeLayout mBottleLayout;
    private RelativeLayout mBreastLayout;
    private RelativeLayout mOtherLayout;
    private int maxTime = 40;
    private int maxMl = 200;
    private Conversion mConversion;
    private String mEntryDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_add_entry);
        mConversion = new Conversion();
        changeNotificationBarColor(R.color.feeding_text);
        navigationToolbarWithIcon(R.mipmap.img_navbar_feeding);
        if (getIntent().getParcelableExtra("dot") != null) {
            mDotCategory = getIntent().getParcelableExtra("dot");
            mValues = getDotFromJSONArray(mDotCategory.getEntryData());
            mEntryDate = mDotCategory.getDate();
        } else {  // Add New Entry
            mDotCategory = new DotCategory();
            mDotCategory.setId(UUID.randomUUID().toString().toUpperCase());
            mDotCategory.setCategoryId(CATEGORY);
            mValues = new DotValues();
            mValues.setValue(-1);
            mEntryDate = getIntent().getStringExtra("entry_date");
        }

        generateData();

        init();
    }

    private void init() {
        mCustomView = getLayoutInflater().inflate(R.layout.custom_feeding_add_entry, null);
        mListFeedOption = (ListView) mCustomView.findViewById(R.id.feeding_set_list_view);
        mListFeedOption.setAdapter(new ArrayAdapter<>(this,
                R.layout.custom_item_list_add_entry_feeding,
                mFeedOption));
        mListFeedOption.setItemChecked(mValues.getValue(), true);

        mLabelBottle = (TextView) mCustomView.findViewById(R.id.label_bottle);
        mNumberBottle = (TextView) mCustomView.findViewById(R.id.content_bottle);
        mNumberBreast = (TextView) mCustomView.findViewById(R.id.content_breast);
        mBottleLayout = (RelativeLayout) mCustomView.findViewById(R.id.feeding_bottle_layout);
        mBreastLayout = (RelativeLayout) mCustomView.findViewById(R.id.feeding_breast_layout);
        mOtherLayout = (RelativeLayout) mCustomView.findViewById(R.id.feeding_other_layout);

        mOtherValue = (EditText) mCustomView.findViewById(R.id.content_text);
        mOtherValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                invalidateOptionsMenu();
            }
        });

        mBottle = (DiscreteSeekBar) mCustomView.findViewById(R.id.seek_bar_bottle);
        mBottle.setIndicatorPopupEnabled(false);
        mBottle.setMax(maxMl);

        mBreast = (DiscreteSeekBar) mCustomView.findViewById(R.id.seek_bar_breast);
        mBreast.setIndicatorPopupEnabled(false);
        mBreast.setMax(maxTime);

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

        mListFeedOption.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:  // Bottle
                        mValues.setDetailValue(mBottle.getProgress());

                        if (getAppPreferences().getInt("unit", 0) == 0) {
                            mLabelBottle.setText(getString(R.string.add_feeding_ml));
                        } else {
                           mLabelBottle.setText(getString(R.string.add_feeding_fl));
                        }

                        mBottleLayout.setVisibility(View.VISIBLE);
                        mBreastLayout.setVisibility(View.GONE);
                        mOtherLayout.setVisibility(View.GONE);
                        break;
                    case 1: // Breast
                    case 2:
                        mValues.setDetailValue(mBreast.getProgress());
                        mBottleLayout.setVisibility(View.GONE);
                        mBreastLayout.setVisibility(View.VISIBLE);
                        mOtherLayout.setVisibility(View.GONE);
                        break;
                    case 3:  // Other
                        mBottleLayout.setVisibility(View.GONE);
                        mBreastLayout.setVisibility(View.GONE);
                        mOtherLayout.setVisibility(View.VISIBLE);
                        break;
                }
                mValues.setValue(position);
                invalidateOptionsMenu();
            }
        });

        // If data come to edit
        switch (mValues.getValue()) {
            case 0:  // Bottle
                if (getAppPreferences().getInt("unit", 0) == 0) {
                    mBottle.setProgress((int) mValues.getDetailValue());
                    mNumberBottle.setText(Float.toString(mValues.getDetailValue()));
                    mLabelBottle.setText(getString(R.string.add_feeding_ml));
                } else {
                    mBottle.setProgress(Integer.parseInt(mConversion.mlToFl((double) mValues.getDetailValue())));
                    mNumberBottle.setText(mConversion.mlToFl((double) mValues.getDetailValue()));
                    mLabelBottle.setText(getString(R.string.add_feeding_fl));
                }
                mBottleLayout.setVisibility(View.VISIBLE);
                mBreastLayout.setVisibility(View.GONE);
                mOtherLayout.setVisibility(View.GONE);
                break;
            case 1: // Breast
            case 2:
                mBreast.setProgress((int) mValues.getDetailValue());
                mNumberBreast.setText(Integer.toString((int) mValues.getDetailValue()));
                mBottleLayout.setVisibility(View.GONE);
                mBreastLayout.setVisibility(View.VISIBLE);
                mOtherLayout.setVisibility(View.GONE);
                break;
            case 3:
                mOtherValue.setText(mValues.getText());
                mBottleLayout.setVisibility(View.GONE);
                mBreastLayout.setVisibility(View.GONE);
                mOtherLayout.setVisibility(View.VISIBLE);
                break;

        }

        setDateEditInButton(mEntryDate);

        mBottle.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
            @Override
            public int transform(int i) {
                mNumberBottle.setText(Integer.toString((i / 5) * 5));
                if (getAppPreferences().getInt("unit", 0) == 0)
                    mNumberBottle.setText(Integer.toString((i / 5) * 5));
                else
                    mNumberBottle.setText(mConversion.mlToFl((double)((i / 5) * 5)));
                if (i > 0)
                    mValues.setDetailValue((i / 5) * 5);

                invalidateOptionsMenu();
                return 0;
            }
        });

        mBreast.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
            @Override
            public int transform(int i) {
                mNumberBreast.setText(Integer.toString(i));
                if (i > 0)
                    mValues.setDetailValue(i);
                invalidateOptionsMenu();
                return 0;
            }
        });
    }

    private void generateData() {
        mFeedOption = getResources().getStringArray(R.array.feed_list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Validate if don't content is empty or zero for show menu save
        switch (mValues.getValue()) {
            case 0:
            case 1:
            case 2:
                if (mValues.getDetailValue() != 0)
                    getMenuInflater().inflate(R.menu.menu_save, menu);
                break;
            case 3:
                if (!mOtherValue.getText().toString().equals(""))
                    getMenuInflater().inflate(R.menu.menu_save, menu);
                break;
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
            mValues.setText(mOtherValue.getText().toString());
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
                showDateTimeDialog(mDotCategory.getDate(), R.style.FeedingStyleDialog, R.color.feeding_text);
                break;
        }
    }
}
