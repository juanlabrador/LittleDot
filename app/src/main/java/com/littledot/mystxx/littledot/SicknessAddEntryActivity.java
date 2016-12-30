package com.littledot.mystxx.littledot;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import domains.DotCategory;
import domains.Image;

/**
 * Created by CharlesSio on 19/08/15.
 */

public class SicknessAddEntryActivity extends BaseFeedActivity {

    private static int CATEGORY = 3;
    private DotCategory mDotCategory;
    private EditText sicknessEntry;
    private View mCustomView;
    private List<Image> mPhotos;
    private String mEntryDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_add_entry);
        changeNotificationBarColor(R.color.sickness_text);
        navigationToolbarWithIcon(R.mipmap.img_navbar_sick);

        if (getIntent().getParcelableExtra("dot") != null) {
            mDotCategory = getIntent().getParcelableExtra("dot");
            mEntryDate = mDotCategory.getDate();
        } else {  // Add New Entry
            mDotCategory = new DotCategory();
            mDotCategory.setId(UUID.randomUUID().toString().toUpperCase());
            mDotCategory.setCategoryId(CATEGORY);
            mEntryDate = getIntent().getStringExtra("entry_date");
        }

        init();
    }

    private void init() {
        mCustomView = getLayoutInflater().inflate(R.layout.custom_text_add_entry, null);
        sicknessEntry = (EditText) mCustomView.findViewById(R.id.content_text);
        sicknessEntry.requestFocus();
        sicknessEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(sicknessEntry, InputMethodManager.SHOW_IMPLICIT);
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

        sicknessEntry.setText(mDotCategory.getNote());
        setDateEditInButton(mEntryDate);

        sicknessEntry.addTextChangedListener(new TextWatcher() {
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!sicknessEntry.getText().toString().equals(""))
            getMenuInflater().inflate(R.menu.menu_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        List<Image> mGallery = new ArrayList<>();
        if (id == R.id.save_entry) {
            // If text is empty, not save nothing
            if (!sicknessEntry.getText().toString().equals("")) {
                mDotCategory.setNote(sicknessEntry.getText().toString());
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
                    mDotCategory.setEntryData(new JSONArray());
                    mDotCategory.setDate(getDateTime());
                    if (getIntent().getParcelableExtra("dot") != null) {
                        updateDot(mDotCategory);
                    } else {
                        addNewDot(mDotCategory);
                    }
                }
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
                showDateDialog(mDotCategory.getDate(), R.style.SicknessStyleDialog);
                break;
        }
    }
}
