package com.littledot.mystxx.littledot;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckedTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import domains.DotCategory;
import domains.DotValues;
import domains.Image;
import domains.Vaccine;

/**
 * Created by juanlabrador on 18/08/15.
 */
public class VaccineEditEntryActivity extends BaseFeedActivity {

    private static int CATEGORY = 7;
    private Vaccine mVaccine;
    private DotCategory mDotCategory;
    private DotValues mValues;
    private boolean isSkipped;
    private int mDoseActual;
    private CheckedTextView mCheck;
    private DotCategory mEntry;
    private List<Image> mPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_add_entry);
        changeNotificationBarColor(R.color.vaccine_text);
        navigationToolbarWithIcon(R.mipmap.img_navbar_vaccine);

        // If user edit vaccine
        if (getIntent().getParcelableExtra("dot") != null) {
            mDotCategory = getIntent().getParcelableExtra("dot");
            mValues = getDotFromJSONArray(mDotCategory.getEntryData());
        } else {
            // Add vaccine from schedule
            mVaccine = getIntent().getParcelableExtra("vaccine");
            isSkipped = getIntent().getBooleanExtra("skipped", false);
            mDoseActual = getIntent().getIntExtra("dose_actual", 1);
            mDotCategory = new DotCategory();
            mDotCategory.setId(UUID.randomUUID().toString().toUpperCase());
            mDotCategory.setCategoryId(CATEGORY);
            mValues = new DotValues();
            mValues.setValue(mVaccine.getValue());
            mValues.setDetailValue(mDoseActual);
            if (isSkipped)  // Skipped
                mValues.setSkipped(1);
            else  // Doesn't skipped
                mValues.setSkipped(0);
        }
        init();
    }

    private void init() {
        View mCustomView = getLayoutInflater().inflate(R.layout.custom_item_list_add_entry_vaccine, null);
        mCheck = (CheckedTextView) mCustomView.findViewById(android.R.id.text1);
        mCheck.setPadding(16, 16, 0, 0);
        mCheck.setText(getResources().getStringArray(R.array.vaccine_list_names)[mValues.getValue()]);
        mCheck.setChecked(true);
        if (getIntent().getParcelableExtra("dot") == null)
            mCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCheck.isChecked()) {
                        mCheck.setChecked(false);
                    } else {
                        mCheck.setChecked(true);
                    }
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

        if (isSkipped)
            setDateEditInVaccine(R.string.skipped_dialog);
        else
            setDateEditInVaccine(R.string.vaccinated_dialog);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mCheck.isChecked())
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
                if (isSkipped)
                    showDateDialogVaccineSchedule(R.string.skipped_dialog, mDotCategory.getDate(), R.style.VaccineStyleDialog);
                else
                    showDateDialogVaccineSchedule(R.string.vaccinated_dialog, mDotCategory.getDate(), R.style.VaccineStyleDialog);
                break;
        }
    }
}
