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
public class MedicineAddEntryActivity extends BaseFeedActivity {

    private static int CATEGORY = 5;
    private DotCategory mDotCategory;

    private EditText medicineEntry;
    private View mCustomView;
    private List<Image> mPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_add_entry);
        changeNotificationBarColor(R.color.medicine_text);
        navigationToolbarWithIcon(R.mipmap.img_navbar_medicine);

        if (getIntent().getParcelableExtra("dot") != null) {
            mDotCategory = getIntent().getParcelableExtra("dot");
        } else {  // Add New Entry
            mDotCategory = new DotCategory();
            mDotCategory.setId(UUID.randomUUID().toString().toUpperCase());
            mDotCategory.setCategoryId(CATEGORY);
        }

        init();
    }

    private void init() {
        mCustomView = getLayoutInflater().inflate(R.layout.custom_text_add_entry, null);
        medicineEntry = (EditText) mCustomView.findViewById(R.id.content_text);
        medicineEntry.requestFocus();
        medicineEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(medicineEntry, InputMethodManager.SHOW_IMPLICIT);
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
        medicineEntry.setText(mDotCategory.getNote());
        setDateEditInButton(mDotCategory.getDate());

        medicineEntry.addTextChangedListener(new TextWatcher() {
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
        if (!medicineEntry.getText().toString().equals(""))
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
            if (!medicineEntry.getText().toString().equals("")) {
                mDotCategory.setNote(medicineEntry.getText().toString());
                if (id == R.id.save_entry) {
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
                showDateDialog(mDotCategory.getDate(), R.style.MedicineStyleDialog);
                break;
        }
    }
}
