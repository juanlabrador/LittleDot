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
public class TemperatureAddEntryActivity extends BaseFeedActivity implements View.OnClickListener {

    private static int CATEGORY = 4;
    private DotCategory mDotCategory;
    private DotValues mValues;

    private DiscreteSeekBar mTemperature;
    private TextView mNumber;
    private View mCustomView;
    private List<Image> mPhotos;
    private Conversion mConversion;
    private String mEntryDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_add_entry);
        mConversion = new Conversion();
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
        changeNotificationBarColor(R.color.temperature_text);
        navigationToolbarWithIcon(R.mipmap.img_navbar_temperature);

        init();
    }

    private void init() {
        /** Create the temperature view and add */
        mCustomView = getLayoutInflater().inflate(R.layout.custom_temperature_add_entry, null);

        TextView mType = (TextView) mCustomView.findViewById(R.id.type_temperature);

        if (getAppPreferences().getInt("unit", 0) == 0) {
            mType.setText(R.string.add_temperature_celsius);
        } else {
            mType.setText(R.string.add_temperature_fahrenheit);
        }

        mNumber = (TextView) mCustomView.findViewById(R.id.content_temperature);

        mTemperature = (DiscreteSeekBar) mCustomView.findViewById(R.id.seek_bar_temperature);
        mTemperature.setIndicatorPopupEnabled(false);

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

        if (mValues.getDetailValue() > 0) {
            if (getAppPreferences().getInt("unit", 0) == 0) {
                mTemperature.setProgress((int) ((mValues.getDetailValue() * 10) - 360));
                mNumber.setText(Float.toString(mValues.getDetailValue()));
            } else {
                mTemperature.setProgress(Integer.parseInt(mConversion.celsiusToFahrenheit((double) (mValues.getDetailValue() * 10) - 360)));
                mNumber.setText(mConversion.celsiusToFahrenheit((double) mValues.getDetailValue()));
            }
        }

        setDateEditInButton(mEntryDate);

        mTemperature.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
            @Override
            public int transform(int i) {
                if (getAppPreferences().getInt("unit", 0) == 0)
                    mNumber.setText(Float.toString((float) ((i + 360) / 10.0)));
                else
                    mNumber.setText(mConversion.celsiusToFahrenheit((i + 360) / 10.0));
                mValues.setDetailValue((float) ((i + 360) / 10.0));
                return 0;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
                showDateTimeDialog(mEntryDate, R.style.TemperatureStyleDialog, R.color.temperature_text);
                break;
        }
    }
}
