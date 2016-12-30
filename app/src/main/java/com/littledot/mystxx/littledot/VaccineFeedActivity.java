package com.littledot.mystxx.littledot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import adapters.VaccineListAdapter;
import domains.DotCategory;
import domains.DotValues;
import domains.Vaccine;

/**
 * Created by CharlesSio on 19/08/15.
 */
public class VaccineFeedActivity extends BaseFeedActivity implements AdapterView.OnItemClickListener {

    private static int CATEGORY = 7;
    private List<DotCategory> mVaccines;
    private List<DotValues> mDoses;
    private List<Vaccine> mVaccinesSchedule;

    // View schedule vaccines
    private View mCustomView;
    private Button mNextButton;
    private Button mOptionalButton;
    private ListView mListVaccines;
    private VaccineListAdapter mAdapter;
    private boolean inNext = true;

    // View go to known
    private View mCustomGoView;
    private TextView mContentGo;
    private AlertDialog mDialog;
    private AlertDialog mScheduleDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeNotificationBarColor(R.color.vaccine_text);

        updateDataEvery(null, CATEGORY);

        createCustomFeed(
                CATEGORY,
                R.string.vaccine_feed_content_list_empty,
                getResources().getStringArray(R.array.vaccine_menu_list),
                R.color.vaccine_background);

        colorFeedView(
                R.mipmap.icn_vaccines,
                R.string.vaccine_feed_name,
                R.mipmap.icn_add_vaccines,
                R.color.vaccine_text,
                R.style.VaccineStyleDialog,
                R.drawable.tinted_icon_filter_time_vaccine);

    }

    private void createVaccineScheduleView() {
        inNext = true;
        mCustomView = getLayoutInflater().inflate(R.layout.custom_vaccine_schedule_list, null);
        mListVaccines = (ListView) mCustomView.findViewById(R.id.vaccine_schedule_list);
        mListVaccines.setOnItemClickListener(this);
        mNextButton = (Button) mCustomView.findViewById(R.id.vaccine_list_next_button);
        mNextButton.setOnClickListener(this);
        mOptionalButton = (Button) mCustomView.findViewById(R.id.vaccine_list_optional_button);
        mOptionalButton.setOnClickListener(this);

        NextVaccines();

    }

    private void NextVaccines() {
        // Next Vaccines
        mVaccinesSchedule = createScheduleVaccines();
        mAdapter = new VaccineListAdapter(this, mVaccinesSchedule);
        mListVaccines.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void OptionalVaccines() {
        // Optional Vaccines
        mVaccinesSchedule = createScheduleVaccines();
        mAdapter = new VaccineListAdapter(this, mVaccinesSchedule);
        mListVaccines.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void createVaccineGoToKnownView() {
        mCustomGoView = getLayoutInflater().inflate(R.layout.custom_vaccine_go_to_known, null);
        mContentGo = (TextView) mCustomGoView.findViewById(R.id.vaccine_go_to_know);
        mContentGo.setText(Html.fromHtml(getString(R.string.vaccine_go_to_know)));
        mContentGo.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.feed_add_item:
                startActivity(new Intent(this, VaccineAddEntryActivity.class));
                overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                break;
            case R.id.menu_1:
                createVaccineScheduleView();
                mScheduleDialog = showSimpleDialogCancelable(R.string.vaccine_vaccination_title, mCustomView, R.style.VaccineStyleDialog);
                break;
            case R.id.menu_2:
                createVaccineGoToKnownView();
                showSimpleDialog(R.string.temperature_title_info, mCustomGoView, R.style.VaccineStyleDialog);
                break;
            case R.id.vaccine_list_next_button:
                inNext = true;
                mNextButton.setBackgroundResource(R.drawable.style_button_vaccine_color);
                mNextButton.setTextColor(getResources().getColor(R.color.window_background));
                mOptionalButton.setBackgroundResource(R.drawable.style_button_vaccine);
                mOptionalButton.setTextColor(getResources().getColor(R.color.vaccine_text));
                NextVaccines();
                break;
            case R.id.vaccine_list_optional_button:
                inNext = false;
                mNextButton.setBackgroundResource(R.drawable.style_button_vaccine);
                mNextButton.setTextColor(getResources().getColor(R.color.vaccine_text));
                mOptionalButton.setBackgroundResource(R.drawable.style_button_vaccine_color);
                mOptionalButton.setTextColor(getResources().getColor(R.color.window_background));
                OptionalVaccines();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        showVaccineDetail(mVaccinesSchedule.get(position));
    }

    /**
     * Method for create custom item vaccine in schedule view
     * @param vaccine
     */
    private void showVaccineDetail(final Vaccine vaccine) {
        final int dose = getNextDoseByVaccine(vaccine, mDoses);
        mCustomView = getLayoutInflater().inflate(R.layout.custom_vaccine_schedule_item_detail, null);

        // Check info vaccination
        ((TextView) mCustomView.findViewById(R.id.vaccine_info_check))
                .setText(String.format(getString(R.string.vaccine_info_check), calculateVaccination(vaccine.getMinAge())));

        // Age min for vaccinated
        ((TextView) mCustomView.findViewById(R.id.age_dose))
                .setText(String.format(getString(R.string.vaccine_age), vaccine.getMinAge()));
        // Number max doses + dose actual
        if (!vaccine.getName().equals(getString(R.string.vaccine_optional_4)) &&
                !vaccine.getName().equals(getString(R.string.vaccine_optional_7)))
            ((TextView) mCustomView.findViewById(R.id.number_dose))
                    .setText(String.format(getString(R.string.vaccine_dose),
                            dose,
                            vaccine.getMaxDose()));
        else // Recurring
            ((TextView) mCustomView.findViewById(R.id.number_dose))
                    .setText(R.string.vaccine_recurring);
        ((TextView)mCustomView.findViewById(R.id.vaccine_info_details)).setText(vaccine.getDetail());

        mCustomView.findViewById(R.id.vaccine_info_done_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(), VaccineEditEntryActivity.class)
                                .putExtra("vaccine", vaccine)
                                .putExtra("skipped", false)
                                .putExtra("dose_actual", dose));
                        overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                        mDialog.dismiss();
                        mScheduleDialog.dismiss();
                    }
                });

        mCustomView.findViewById(R.id.vaccine_info_skipped_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(), VaccineEditEntryActivity.class)
                                .putExtra("vaccine", vaccine)
                                .putExtra("skipped", true)
                                .putExtra("dose_actual", dose));
                        overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                        mDialog.dismiss();
                        mScheduleDialog.dismiss();
                    }
                });


        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.VaccineStyleDialog);
        builder.setTitle(vaccine.getName());
        builder.setView(mCustomView);
        mDialog = builder.create();
        mDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVaccines = getDotsByCategory(CATEGORY);
        mDoses = getDosesVaccines(mVaccines);
        updateDataEvery(null, CATEGORY);
    }

    /**
     * Create the original list with all conditions initial, for example
     * *** Dose actual
     * *** if vaccine finish doses
     * @return
     */
    private List<Vaccine> createScheduleVaccines() {
        mVaccines = getDotsByCategory(CATEGORY);
        mDoses = getDosesVaccines(mVaccines);
        List<Vaccine> vaccines = new ArrayList<>();
        String[] vacc = getResources().getStringArray(R.array.vaccine_list_names);
        String[] details = getResources().getStringArray(R.array.vaccine_list_details);
        int[] doses = getResources().getIntArray(R.array.max_doses_vaccines);
        Vaccine vaccine;
        if (inNext) {
            vaccine = new Vaccine(1, vacc[1], doses[1], false, 2, false, details[1]);
            if (getNextDoseByVaccine(vaccine, mDoses) != -1)
                vaccines.add(vaccine);
            vaccine = new Vaccine(2, vacc[2], doses[2], false, 2, false, details[2]);
            if (getNextDoseByVaccine(vaccine, mDoses) != -1)
                vaccines.add(vaccine);
            vaccine = new Vaccine(3, vacc[3], doses[3], false, 2, false, details[3]);
            if (getNextDoseByVaccine(vaccine, mDoses) != -1)
                vaccines.add(vaccine);
            vaccine = new Vaccine(4, vacc[4], doses[4], false, 12, false, details[4]);
            if (getNextDoseByVaccine(vaccine, mDoses) != -1)
                vaccines.add(vaccine);
            vaccine = new Vaccine(5, vacc[5], doses[5], false, 0, false, details[5]);
            if (getNextDoseByVaccine(vaccine, mDoses) != -1)
                vaccines.add(vaccine);
            //vaccine = new Vaccine(12, vacc[12], doses[12], false, 84, false);
            //vaccines.add(vaccine);
        } else {
            vaccine = new Vaccine(0, vacc[0], doses[0], true, 0, false, details[0]);
            if (getNextDoseByVaccine(vaccine, mDoses) != -1)
                vaccines.add(vaccine);
            vaccine = new Vaccine(6, vacc[6], doses[6], true, 12, false, details[6]);
            if (getNextDoseByVaccine(vaccine, mDoses) != -1)
                vaccines.add(vaccine);
            vaccine = new Vaccine(7, vacc[7], doses[7], true, 12, false, details[7]);
            if (getNextDoseByVaccine(vaccine, mDoses) != -1)
                vaccines.add(vaccine);
            vaccine = new Vaccine(8, vacc[8], doses[8], true, 12, true, details[8]);
            vaccines.add(vaccine);
            vaccine = new Vaccine(9, vacc[9], doses[9], true, 12, false, details[9]);
            if (getNextDoseByVaccine(vaccine, mDoses) != -1)
                vaccines.add(vaccine);
            vaccine = new Vaccine(10, vacc[10], doses[10], true, 6, true, details[10]);
            vaccines.add(vaccine);
            vaccine = new Vaccine(11, vacc[11], doses[11], true, 2, false, details[11]);
            if (getNextDoseByVaccine(vaccine, mDoses) != -1)
                vaccines.add(vaccine);
        }
        return vaccines;
    }
}
