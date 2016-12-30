package com.littledot.mystxx.littledot;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import domains.Child;
import domains.Conversion;
import domains.DotCategory;
import domains.DotValues;

/**
 * Created by juanlabrador on 11/08/15.
 */
public class DoctorEmailOptionsActivity extends BaseFeedActivity {

    private Child mChild;
    private List<DotCategory> mDotCategories;
    private SimpleDateFormat mFormatTime;
    private SimpleDateFormat mFormatDate;
    private SimpleDateFormat mFormatDB;
    private String[] milestone;
    private String[] teeth;
    private String[] vaccines;
    private String[] poo;

    // Dialog
    private int mPositionSelected;
    private boolean mError;
    private Calendar mFrom;
    private Calendar mTo;
    private AppCompatEditText mFromDate;
    private AppCompatEditText mToDate;
    private View mFilterView;
    private View mContainerDates;
    private ListView mList;

    private Conversion mConversion;
    public String[] mLocale = { "en", "es", "de", "hr" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createDoctorEmail();
        navigationToolbarWithTitle(R.string.email_option_title);
        mChild = getChild();

        mFormatTime = new SimpleDateFormat(getString(R.string.format_time));
        mFormatDate = new SimpleDateFormat(getString(R.string.format_pretty_time),
                new Locale(mLocale[getAppPreferences().getInt("language", 0)]));
        mFormatDB = new SimpleDateFormat(getString(R.string.format_date_service));
        milestone = getResources().getStringArray(R.array.milestone_list);
        teeth = getResources().getStringArray(R.array.teeth_list);
        vaccines = getResources().getStringArray(R.array.vaccine_list);
        poo = getResources().getStringArray(R.array.diapers_poo_list);
        mPositionSelected = getAppPreferences().getInt("filter_time", 4);
        mConversion = new Conversion();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_doctor_options_email, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.choose_time_email) {
            showTimeEmailDialog();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.prepare_email) {

            // Prepare intent email
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", getAppPreferences().getString("email_doctor", ""), null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, String.format(getString(R.string.email_subject),
                    mChild.getName(), changeDateChild(mChild.getDob())));
            emailIntent.putExtra(Intent.EXTRA_TEXT, contentEmail(mDotCategories));
            try {
                startActivity(Intent.createChooser(emailIntent, getString(R.string.email_intent)));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getApplicationContext(),
                        R.string.email_no_client, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Method for filter all dots by category selected
     * @return
     */
    private List<DotCategory> groupFilter(List<DotCategory> dotCategories) {
        List<DotCategory> dotsNew = new ArrayList<>();
        for (DotCategory dotCategory : dotCategories) {
            if (getAppPreferences().getBoolean("temperature_switch", false)) {
                if (dotCategory.getCategoryId() == 4)
                    dotsNew.add(dotCategory);
            }
            if (getAppPreferences().getBoolean("sickness_switch", false)) {
                if (dotCategory.getCategoryId() == 3)
                    dotsNew.add(dotCategory);
            }
            if (getAppPreferences().getBoolean("medicines_switch", false)) {
                if (dotCategory.getCategoryId() == 5)
                    dotsNew.add(dotCategory);
            }
            if (getAppPreferences().getBoolean("vaccines_switch", false)) {
                if (dotCategory.getCategoryId() == 7)
                    dotsNew.add(dotCategory);
            }
            if (getAppPreferences().getBoolean("feeding_switch", false)) {
                if (dotCategory.getCategoryId() == 0)
                    dotsNew.add(dotCategory);
            }
            if (getAppPreferences().getBoolean("sleeping_switch", false)) {
                if (dotCategory.getCategoryId() == 1)
                    dotsNew.add(dotCategory);
            }
            if (getAppPreferences().getBoolean("diapers_switch", false)) {
                if (dotCategory.getCategoryId() == 2)
                    dotsNew.add(dotCategory);
            }
            if (getAppPreferences().getBoolean("growth_switch", false)) {
                if (dotCategory.getCategoryId() == 6)
                    dotsNew.add(dotCategory);
            }
            if (getAppPreferences().getBoolean("milestone_switch", false)) {
                if (dotCategory.getCategoryId() == 8)
                    dotsNew.add(dotCategory);
            }
            if (getAppPreferences().getBoolean("teeth_switch", false)) {
                if (dotCategory.getCategoryId() == 9)
                    dotsNew.add(dotCategory);
            }
            if (getAppPreferences().getBoolean("other_switch", false)) {
                if (dotCategory.getCategoryId() == 10)
                    dotsNew.add(dotCategory);
            }
        }
        return dotsNew;
    }

    /**
     * Method for to create the content email order with header date
     * @param dotCategories
     * @return
     */
    private String contentEmail(List<DotCategory> dotCategories) {
        List<DotCategory> dotsNew = groupDotsByDate(dotCategories);
        StringBuilder mMessage = new StringBuilder();
        for (DotCategory dotCategory : dotsNew) {
            if (dotCategory.getCategoryId() == -1) {

                mMessage.append("\n");
                mMessage.append(String.format(getString(R.string.email_text_date), dotCategory.getDate()));
                mMessage.append("\n\n");
            }
            if (getAppPreferences().getBoolean("temperature_switch", false)) {
                if (dotCategory.getCategoryId() == 4) {
                    DotValues values = getDotFromJSONArray(dotCategory.getEntryData());
                    if (getAppPreferences().getInt("unit", 0) == 0)
                        mMessage.append(String.format(getString(R.string.email_text_temperature),
                                values.getDetailValue(), changeDateToTime(dotCategory.getDate())));
                    else
                        mMessage.append(String.format(getString(R.string.email_text_temperature_f),
                                mConversion.celsiusToFahrenheit((double) values.getDetailValue()), changeDateToTime(dotCategory.getDate())));
                    mMessage.append("\n");
                }
            }
            if (getAppPreferences().getBoolean("sickness_switch", false)) {
                if (dotCategory.getCategoryId() == 3) {
                    mMessage.append(String.format(getString(R.string.email_text_sickness),
                            dotCategory.getNote(), changeDateToTime(dotCategory.getDate())));
                    mMessage.append("\n");
                }
            }
            if (getAppPreferences().getBoolean("medicines_switch", false)) {
                if (dotCategory.getCategoryId() == 5) {
                    mMessage.append(String.format(getString(R.string.email_text_medicine),
                            dotCategory.getNote(), changeDateToTime(dotCategory.getDate())));
                    mMessage.append("\n");
                }
            }
            if (getAppPreferences().getBoolean("vaccines_switch", false)) {
                if (dotCategory.getCategoryId() == 7) {
                    DotValues values = getDotFromJSONArray(dotCategory.getEntryData());
                    mMessage.append(String.format(getString(R.string.email_text_vaccine),
                            String.format(vaccines[values.getValue()], Math.round(values.getDetailValue()))));
                    mMessage.append("\n");
                }
            }
            if (getAppPreferences().getBoolean("feeding_switch", false)) {
                if (dotCategory.getCategoryId() == 0) {
                    DotValues values = getDotFromJSONArray(dotCategory.getEntryData());
                    if (values.getValue() == 0)
                        if (getAppPreferences().getInt("unit", 0) == 0)
                            mMessage.append(String.format(getString(R.string.email_text_feeding_bottle),
                                    Math.round(values.getDetailValue()), changeDateToTime(dotCategory.getDate())));
                        else
                            mMessage.append(String.format(getString(R.string.email_text_feeding_bottle_fl),
                                    mConversion.mlToFl((double) Math.round(values.getDetailValue())), changeDateToTime(dotCategory.getDate())));
                    else if (values.getValue() == 1)
                        mMessage.append(String.format(getString(R.string.email_text_feeding_breast_left),
                                Math.round(values.getDetailValue()), changeDateToTime(dotCategory.getDate())));
                    else if (values.getValue() == 2)
                        mMessage.append(String.format(getString(R.string.email_text_feeding_breast_right),
                                Math.round(values.getDetailValue()), changeDateToTime(dotCategory.getDate())));
                    else
                        mMessage.append(String.format(getString(R.string.email_text_feeding_other),
                                values.getText(), changeDateToTime(dotCategory.getDate())));
                    mMessage.append("\n");
                }
            }
            if (getAppPreferences().getBoolean("sleeping_switch", false)) {
                if (dotCategory.getCategoryId() == 1) {
                    DotValues values = getDotFromJSONArray(dotCategory.getEntryData());
                    mMessage.append(String.format(getString(R.string.email_text_sleeping),
                            values.getDetailValue(), changeDateToTime(dotCategory.getDate())));
                    mMessage.append("\n");
                }
            }
            if (getAppPreferences().getBoolean("diapers_switch", false)) {
                if (dotCategory.getCategoryId() == 2) {
                    List<DotValues> values = getListDotFromJSONArray(dotCategory.getEntryData());
                    if (values.size() > 1)
                        mMessage.append(String.format(getString(R.string.email_text_diapers_2),
                                getString(R.string.diapers_pee), poo[values.get(1).getValue()], changeDateToTime(dotCategory.getDate())));
                    else
                        if (values.get(0).getGroup() == 0)
                            mMessage.append(String.format(getString(R.string.email_text_diapers),
                                    getString(R.string.diapers_pee), changeDateToTime(dotCategory.getDate())));
                        else
                            mMessage.append(String.format(getString(R.string.email_text_diapers),
                                    poo[values.get(0).getValue()], changeDateToTime(dotCategory.getDate())))
                    ;
                    mMessage.append("\n");
                }
            }
            if (getAppPreferences().getBoolean("growth_switch", false)) {
                if (dotCategory.getCategoryId() == 6) {
                    List<DotValues> values = getListDotFromJSONArray(dotCategory.getEntryData());
                    if (getAppPreferences().getInt("unit", 0) == 0) {
                        if (values.size() > 2)
                            mMessage.append(String.format(getString(R.string.email_text_growth_2),
                                    values.get(0).getDetailValue(), values.get(1).getDetailValue(), values.get(2).getDetailValue()));
                        else
                            mMessage.append(String.format(getString(R.string.email_text_growth),
                                    values.get(0).getDetailValue(), values.get(1).getDetailValue()));
                    } else {
                        if (values.size() > 2)
                            mMessage.append(String.format(getString(R.string.email_text_growth_2_lb),
                                    mConversion.kgToPounds((double) values.get(0).getDetailValue()),
                                    mConversion.cmToInches((double) values.get(1).getDetailValue()),
                                    mConversion.cmToInches((double) values.get(2).getDetailValue())));
                        else
                            mMessage.append(String.format(getString(R.string.email_text_growth_lb),
                                    mConversion.kgToPounds((double) values.get(0).getDetailValue()),
                                    mConversion.cmToInches((double) values.get(1).getDetailValue())));
                    }
                    mMessage.append("\n");
                }
            }
            if (getAppPreferences().getBoolean("milestone_switch", false)) {
                if (dotCategory.getCategoryId() == 8) {
                    DotValues values = getDotFromJSONArray(dotCategory.getEntryData());
                    mMessage.append(String.format(getString(R.string.email_text_milestone),
                            milestone[values.getValue()]));
                    mMessage.append("\n");
                }
            }
            if (getAppPreferences().getBoolean("teeth_switch", false)) {
                if (dotCategory.getCategoryId() == 9) {
                    DotValues values = getDotFromJSONArray(dotCategory.getEntryData());
                    mMessage.append(String.format(getString(R.string.email_text_teeth),
                            teeth[values.getValue()]));
                    mMessage.append("\n");
                }
            }
            if (getAppPreferences().getBoolean("other_switch", false)) {
                if (dotCategory.getCategoryId() == 10) {
                    mMessage.append(String.format(getString(R.string.email_text_other),
                            dotCategory.getNote(), changeDateToTime(dotCategory.getDate())));
                    mMessage.append("\n");
                }
            }
        }
        return mMessage.toString();
    }

    private String changeDateToTime(String date) {
        String time = null;
        try {
            Date d = mFormatDB.parse(date);
            time = mFormatTime.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    private String changeDateChild(String dateDB) {
        String date = null;
        try {
            Date d = mFormatDB.parse(dateDB);
            date = mFormatDate.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


    /**
     * Method for order list with header by Dates
     * Example: "June 24, 2015
     * @param dotCategories
     * @return
     */
    private List<DotCategory> groupDotsByDate(List<DotCategory> dotCategories) {
        List<DotCategory> newList = new ArrayList<>();
        if (dotCategories != null)
            newList.addAll(dotCategories);
        else {
            //If dots is null, because filter time not change in dialog
            //It search for default a filter time
            dotCategories = new ArrayList<>();
            if (mPositionSelected != 6) {
                dotCategories.addAll(groupFilter(filterListByTime(mPositionSelected)));
                newList.addAll(dotCategories);
            } else {
                // If was selected of default "between" date
                changeDatesRangeEmail(null, -1, -1);
                List<Calendar> mDates = new ArrayList<>();
                mDates.add(0, mFrom);
                mDates.add(0, mTo);
                dotCategories.addAll(groupFilter(filterListBetweenDates(mDates)));
                newList.addAll(dotCategories);
            }
        }

        int count = 0;  // Variable for sum number dates the same type

        SimpleDateFormat mFormatDate = new SimpleDateFormat(getString(R.string.format_pretty_time), new Locale(mLocale[getAppPreferences().getInt("language", 0)]));
        Calendar mDate = Calendar.getInstance();
        Calendar mLastDate = Calendar.getInstance();

        Calendar mRangeWeekDays = Calendar.getInstance();
        mRangeWeekDays.add(Calendar.DAY_OF_MONTH, -7);

        // Filter for header date
        for (int i = 0; i < dotCategories.size(); i++) {
            try {
                mDate.setTime(mFormatDB.parse(dotCategories.get(i).getDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (i == 0) {
                newList.add(0, new DotCategory(mFormatDate.format(mDate.getTime())));
                count = 1;
                mLastDate = mDate;
                mDate = Calendar.getInstance();  // Reset
            } else {
                if (!(mDate.get(Calendar.YEAR) == mLastDate.get(Calendar.YEAR)
                        && mDate.get(Calendar.MONTH) == mLastDate.get(Calendar.MONTH)
                        && mDate.get(Calendar.DAY_OF_MONTH) == mLastDate.get(Calendar.DAY_OF_MONTH))) {

                    newList.add(i + count, new DotCategory(mFormatDate.format(mDate.getTime())));
                    count = count + 1;
                    mLastDate = mDate;
                    mDate = Calendar.getInstance();  // Reset
                }
            }
        }
        return newList;
    }

    /**-------------------------------------------------------------------------------------------------*/
    /**-----------------------------------------Filter time email-----------------------------------------*/


    /**
     * Dialog for choose time in main feed
     * */
    private void showTimeEmailDialog() {
        customFilterView();
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPositionSelected = position;
                if (mPositionSelected == 6) {
                    changeDatesRangeEmail(mFilterView, R.color.text_feed, R.style.NormalStyleDialog);
                    mContainerDates.setVisibility(View.VISIBLE);
                } else {
                    mContainerDates.setVisibility(View.GONE);
                }
            }
        });

        setListHeightInsideScroll(mList);
        final AlertDialog.Builder mDialog = new AlertDialog.Builder(this, R.style.NormalStyleDialog);
        mDialog.setTitle(R.string.filter_time_title);
        mDialog.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mDialog.setPositiveButton(R.string.button_ok, null);
        mDialog.setCancelable(false);
        mDialog.setView(mFilterView);
        final AlertDialog dialog = mDialog.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getChooseTimeEmail().setText(
                        getString(R.string.filter_title_message) + " " +
                                getResources().getStringArray(R.array.filter_time_list)[mPositionSelected]);
                getChooseTimeEmail().setText(
                        getString(R.string.filter_title_message) + " " +
                                getResources().getStringArray(R.array.filter_time_list)[mPositionSelected]);
                if (mPositionSelected == 6) {
                    if (!mError) {
                        List<Calendar> mDates = new ArrayList<>();
                        mDates.add(0, mFrom);
                        mDates.add(0, mTo);
                        mDotCategories = groupFilter(filterListBetweenDates(mDates));
                        // We save the filter time selected
                        getEditorPreferences().putInt("filter_time", mPositionSelected);
                        getEditorPreferences().commit();
                        dialog.dismiss();
                    }
                } else {
                    mDotCategories = groupFilter(filterListByTime(mPositionSelected));
                    // We save the filter time selected
                    getEditorPreferences().putInt("filter_time", mPositionSelected);
                    getEditorPreferences().commit();
                    dialog.dismiss();
                }

            }
        });
    }

    /**
     * Here work with the dates in option "between"
     * @param view
     * @param color
     * @param style
     */
    private void changeDatesRangeEmail(View view, int color, final int style) {
        mError = false;
        mFrom = Calendar.getInstance();
        mFrom.add(Calendar.DAY_OF_MONTH, -15);
        mTo = Calendar.getInstance();
        if (view != null) {
            final TextView errorDate = (TextView) view.findViewById(R.id.error_date);
            TextView mLabelFrom = (TextView) view.findViewById(R.id.label_date_from);
            mLabelFrom.setTextColor(getResources().getColor(color));
            TextView mLabelTo = (TextView) view.findViewById(R.id.label_date_to);
            mLabelTo.setTextColor(getResources().getColor(color));
            mFromDate = (AppCompatEditText) view.findViewById(R.id.edit_date_from);
            mFromDate.getBackground().setColorFilter(getResources().getColor(color), PorterDuff.Mode.SRC_IN);
            mToDate = (AppCompatEditText) view.findViewById(R.id.edit_date_to);
            mToDate.getBackground().setColorFilter(getResources().getColor(color), PorterDuff.Mode.SRC_IN);

            mFromDate.setText(new SimpleDateFormat(getString(R.string.format_pretty_time), new Locale(mLocale[getAppPreferences().getInt("language", 0)])).format(mFrom.getTime()));
            mFromDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDateBetweenDialog(style, mFromDate);
                }
            });
            mFromDate.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // Validate dates
                    try {
                        mFrom.setTime(new SimpleDateFormat(getString(R.string.format_pretty_time), new Locale(mLocale[getAppPreferences().getInt("language", 0)])).parse(mFromDate.getText().toString()));
                        mTo.setTime(new SimpleDateFormat(getString(R.string.format_pretty_time), new Locale(mLocale[getAppPreferences().getInt("language", 0)])).parse(mToDate.getText().toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (mFrom.after(mTo) || mFrom.equals(mTo)) {
                        errorDate.setVisibility(View.VISIBLE);
                        mError = true;
                    } else {
                        errorDate.setVisibility(View.GONE);
                        mError = false;
                    }
                }
            });

            mToDate.setText(new SimpleDateFormat(getString(R.string.format_pretty_time), new Locale(mLocale[getAppPreferences().getInt("language", 0)])).format(mTo.getTime()));
            mToDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDateBetweenDialog(style, mToDate);
                }
            });
            mToDate.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // Validate dates
                    try {
                        mFrom.setTime(new SimpleDateFormat(getString(R.string.format_pretty_time), new Locale(mLocale[getAppPreferences().getInt("language", 0)])).parse(mFromDate.getText().toString()));
                        mTo.setTime(new SimpleDateFormat(getString(R.string.format_pretty_time), new Locale(mLocale[getAppPreferences().getInt("language", 0)])).parse(mToDate.getText().toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (mFrom.after(mTo) || mFrom.equals(mTo)) {
                        errorDate.setVisibility(View.VISIBLE);
                        mError = true;
                    } else {
                        errorDate.setVisibility(View.GONE);
                        mError = false;
                    }
                }
            });
        }
    }

    /**
     * Custom view in dialog list time
     */
    private void customFilterView() {
        Context mContextTheme = new ContextThemeWrapper(this, R.style.NormalStyleDialog);
        mFilterView = getLayoutInflater().from(mContextTheme)
                .inflate(R.layout.custom_filter_time, null, false);

        mContainerDates = mFilterView.findViewById(R.id.container_dates);
        mList = (ListView) mFilterView.findViewById(R.id.list_filter_time);
        mList.setAdapter(new ArrayAdapter<>(this, R.layout.custom_item_filter_time_main,
                        getResources().getStringArray(R.array.filter_time_list))
        );
        mList.setItemChecked(getAppPreferences().getInt("filter_time", 2), true);
        mList.setSelection(getAppPreferences().getInt("filter_time", 2));
        if (getAppPreferences().getInt("filter_time", 2) == 6) {
            changeDatesRangeEmail(mFilterView, R.color.text_feed, R.style.NormalStyleDialog);
            mContainerDates.setVisibility(View.VISIBLE);
        }
    }
}

