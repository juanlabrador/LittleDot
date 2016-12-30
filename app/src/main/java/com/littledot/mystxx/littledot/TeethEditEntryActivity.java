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

/**
 * Created by juanlabrador on 18/08/15.
 */
public class TeethEditEntryActivity extends BaseFeedActivity {

    private DotCategory mDotCategory;
    private DotValues mValues;
    private CheckedTextView mCheck;
    private List<Image> mPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_add_entry);
        changeNotificationBarColor(R.color.teeth_text);
        navigationToolbarWithIcon(R.mipmap.img_navbar_teeth);

        // If user edit teeth
        mDotCategory = getIntent().getParcelableExtra("dot");
        mValues = getDotFromJSONArray(mDotCategory.getEntryData());

        init();
    }

    private void init() {
        View mCustomView = getLayoutInflater().inflate(R.layout.custom_item_list_add_entry_teeth, null);
        mCheck = (CheckedTextView) mCustomView.findViewById(android.R.id.text1);
        mCheck.setPadding(16, 16, 0, 0);
        mCheck.setText(getResources().getStringArray(R.array.teeth_list)[mValues.getValue()]);
        mCheck.setChecked(true);

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

        setDateEditInButton(mDotCategory.getDate());
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
            mDotCategory.setDate(getDateTime());
            updateDot(mDotCategory);
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
}
