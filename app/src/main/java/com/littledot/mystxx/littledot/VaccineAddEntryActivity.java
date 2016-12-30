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
import domains.Vaccine;

/**
 * Created by juanlabrador on 18/08/15.
 */
public class VaccineAddEntryActivity extends BaseFeedActivity {

    private static int CATEGORY = 7;
    private DotCategory mDotCategory;
    private DotValues mValues;
    private ListView mListVaccines;
    private View mCustomView;
    private List<Vaccine> mVaccines;

    // Vaccines from database
    private List<DotCategory> mVaccinesDB;
    private List<DotValues> mDosesDB;
    private String mEntryDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_add_entry);
        navigationToolbarWithIcon(R.mipmap.img_navbar_vaccine);
        changeNotificationBarColor(R.color.vaccine_text);
        mVaccines = createListVaccines();
        mDotCategory = new DotCategory();
        mDotCategory.setId(UUID.randomUUID().toString().toUpperCase());
        mDotCategory.setCategoryId(CATEGORY);
        mValues = new DotValues();
        mValues.setValue(-1);

        mEntryDate = getIntent().getStringExtra("entry_date");

        init();
    }

    private void init() {
        String vac;
        ArrayList vaccines = new ArrayList();
        for(Vaccine vaccine: mVaccines) {
            if (vaccine.getValue() != 8 && vaccine.getValue() != 10)
                vac = vaccine.getName() + ", dose: " + getNextDoseByVaccine(vaccine, mDosesDB);
            else
                vac = vaccine.getName();
            vaccines.add(vac);
        }

        mCustomView = getLayoutInflater().inflate(R.layout.custom_simple_list_view, null);
        mListVaccines = (ListView) mCustomView.findViewById(R.id.list_add_entry);
        mListVaccines.addFooterView(
                getLayoutInflater().inflate(R.layout.custom_footer_empty, mListVaccines, false), null, false);

        mListVaccines.setAdapter(new ArrayAdapter<>(this,
                R.layout.custom_item_list_add_entry_vaccine,
                vaccines));
        createEntryView(mCustomView);

        mListVaccines.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Vaccine vaccine = mVaccines.get(position);
                mValues.setValue(vaccine.getValue());
                mValues.setDetailValue(getNextDoseByVaccine(vaccine, mDosesDB));
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
                showDateDialog(mDotCategory.getDate(), R.style.VaccineStyleDialog);
                break;
        }
    }

    /**
     * Create the original list with all conditions initial, for example
     * *** Dose actual
     * *** if vaccine finish doses
     * @return
     */
    private List<Vaccine> createListVaccines() {
        mVaccinesDB = getDotsByCategory(CATEGORY);
        mDosesDB = getDosesVaccines(mVaccinesDB);
        List<Vaccine> vaccines = new ArrayList<>();
        String[] vacc = getResources().getStringArray(R.array.vaccine_list_names);
        String[] details = getResources().getStringArray(R.array.vaccine_list_details);
        int[] doses = getResources().getIntArray(R.array.max_doses_vaccines);
        Vaccine vaccine;
        vaccine = new Vaccine(0, vacc[0], doses[0], true, 0, false, details[0]);
        if (getNextDoseByVaccine(vaccine, mDosesDB) != -1)
            vaccines.add(vaccine);
        vaccine = new Vaccine(1, vacc[1], doses[1], false, 2, false, details[1]);
        if (getNextDoseByVaccine(vaccine, mDosesDB) != -1)
            vaccines.add(vaccine);
        vaccine = new Vaccine(2, vacc[2], doses[2], false, 2, false, details[2]);
        if (getNextDoseByVaccine(vaccine, mDosesDB) != -1)
            vaccines.add(vaccine);
        vaccine = new Vaccine(3, vacc[3], doses[3], false, 2, false, details[3]);
        if (getNextDoseByVaccine(vaccine, mDosesDB) != -1)
            vaccines.add(vaccine);
        vaccine = new Vaccine(4, vacc[4], doses[4], false, 12, false, details[4]);
        if (getNextDoseByVaccine(vaccine, mDosesDB) != -1)
            vaccines.add(vaccine);
        vaccine = new Vaccine(5, vacc[5], doses[5], false, 0, false, details[5]);
        if (getNextDoseByVaccine(vaccine, mDosesDB) != -1)
            vaccines.add(vaccine);
        vaccine = new Vaccine(6, vacc[6], doses[6], true, 12, false, details[6]);
        if (getNextDoseByVaccine(vaccine, mDosesDB) != -1)
            vaccines.add(vaccine);
        vaccine = new Vaccine(7, vacc[7], doses[7], true, 12, false, details[7]);
        if (getNextDoseByVaccine(vaccine, mDosesDB) != -1)
            vaccines.add(vaccine);
        // Tick-Borne
        vaccine = new Vaccine(8, vacc[8], doses[8], true, 12, true, details[8]);
        vaccines.add(vaccine);
        vaccine = new Vaccine(9, vacc[9], doses[9], true, 12, false, details[9]);
        if (getNextDoseByVaccine(vaccine, mDosesDB) != -1)
            vaccines.add(vaccine);
        // FLU
        vaccine = new Vaccine(10, vacc[10], doses[10], true, 6, true, details[10]);
        vaccines.add(vaccine);
        vaccine = new Vaccine(11, vacc[11], doses[11], true, 2, false, details[11]);
        if (getNextDoseByVaccine(vaccine, mDosesDB) != -1)
            vaccines.add(vaccine);
        //vaccine = new Vaccine(12, vacc[12], doses[12], false, 84, false);
        //vaccines.add(vaccine);
        return vaccines;
    }
}
