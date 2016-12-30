package com.littledot.mystxx.littledot;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.Period;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.UUID;

import adapters.FeedListAdapter;
import adapters.MainFeedListAdapter;
import database.DataBaseHandler;
import domains.Child;
import domains.Conversion;
import domains.DotCategory;
import domains.DotValues;
import domains.GroupDot;
import domains.Image;
import domains.Vaccine;
import domains.services.SyncEntriesByChildren;
import domains.services.Entries;
import domains.services.EntryChildren;
import domains.services.EntryData;
import domains.services.SyncChildren;
import domains.services.UnlockedCategories;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import services.UserServices;
import tools.ExceptionHandler;

/**
 * Created by juanlabrador on 28/08/15.
 */
public class ParentFeedActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = "ParentFeedActivity";
    public final static String API = "http://46.101.199.222/api/";
    //public final static String API = "http://littledotapp.com/api/";

    // General
    private DataBaseHandler mDB;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditPreferences;
    private List<Child> mChildren;
    private Child mChild;
    private ImageView mButtonHeader;

    // List Dots
    private ListView mListDots;
    private View mEmptyList;
    private View mHeaderList;
    private View mFooterList;
    private TextView mEmptyTitle;
    private TextView mEmptyContent;
    private Button mEmptyChooseTime;
    private Button mChooseTime;
    private FeedListAdapter mAdapter;
    private MainFeedListAdapter mMainAdapter;


    // Error Dates
    private boolean mError;
    private AppCompatEditText mFromDate;
    private AppCompatEditText mToDate;
    private Calendar mFrom;
    private Calendar mTo;

    private int mPositionSelected;
    public String[] mLocale = { "en", "es", "de", "hr" };

    // CustomView Every Filter
    private View mFilterView;
    private View mContainerDates;
    private ListView mList;

    // CustomView Main Filter
    private View mFilterMainView;
    private View mContainerDatesMain;
    private ListView mListMain;

    private SimpleDateFormat mFormatDB;
    private SimpleDateFormat mFormatDateShort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        mFormatDB = new SimpleDateFormat(getString(R.string.format_date_service));
        mFormatDateShort = new SimpleDateFormat(getString(R.string.format_date_short));
        mPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        mEditPreferences = mPreferences.edit();
        setLocale();
        mDB = new DataBaseHandler(this);

        mChildren = mDB.getAllChildChildren();
        if (areThereChildren())
            childSelected();

    }

    public Date getParseDB(String date) {
        try {
            return mFormatDB.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getFormatDB(Calendar calendar) {
        return mFormatDB.format(calendar.getTime());
    }

    public String getFormatDateShort(Date date) {
        return mFormatDateShort.format(date).substring(0, 1).toUpperCase() +
                mFormatDateShort.format(date).substring(1).toLowerCase();
    }

    /**
     * If phone have access to internet via WIFI or NETWORK
     * @return true or false
     */
    public boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    /**
     * Method for change color bar just > Lollipop
     * @param color
     */

    protected void changeNotificationBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(getResources().getColor(color));
    }

    /**
     * Here create our toolbar for other screens
     * */
    protected void navigationToolbarWithTitle(int title) {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(title);
        setSupportActionBar(mToolbar);
    }

    /**
     * This method is the basis for creating all screens except for doctor and menu options
     * @param layout
     * @param header
     * @param button_header
     */
    protected void createScreen(int layout, int header, int button_header) {

        setContentView(layout);

        mButtonHeader = (ImageView) findViewById(button_header);
        mButtonHeader.setOnClickListener(this);
        mListDots = (ListView) findViewById(R.id.list_dots);

        // Create customs view for ListView
        mEmptyList = getLayoutInflater().inflate(R.layout.custom_empty_list, mListDots, false);

        if (header == -1)
            mHeaderList = getLayoutInflater().inflate(R.layout.custom_header_list, mListDots, false);
        else
            mHeaderList = getLayoutInflater().inflate(header, mListDots, false);

        mFooterList = getLayoutInflater().inflate(R.layout.custom_item_feed_footer_line, mListDots, false);

        mEmptyChooseTime = (Button) mEmptyList.findViewById(R.id.choose_time_filter);
        mEmptyChooseTime.setText(
                getString(R.string.filter_title_message) + " " +
                        getResources().getStringArray(R.array.filter_time_list)[mPreferences.getInt("filter_time", 2)]);
        mEmptyChooseTime.setOnClickListener(this);

        mEmptyTitle = (TextView) mEmptyList.findViewById(R.id.empty_title);
        mEmptyContent = (TextView) mEmptyList.findViewById(R.id.empty_content);

        mChooseTime = (Button) mHeaderList.findViewById(R.id.choose_time_filter);
        mChooseTime.setText(
                getString(R.string.filter_title_message) + " " +
                        getResources().getStringArray(R.array.filter_time_list)[mPreferences.getInt("filter_time", 2)]);
        mChooseTime.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mChooseTime.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.tinted_icon_filter_time_feed), null, null, null);
            mEmptyChooseTime.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.tinted_icon_filter_time_feed), null, null, null);

        }

        mListDots.setEmptyView(mEmptyList);
        mListDots.addHeaderView(mHeaderList, null, false);
        mListDots.addFooterView(mFooterList, null, false);

        // Obligatory add empty view
        RelativeLayout root = (RelativeLayout) findViewById(R.id.empty_list);
        root.addView(mEmptyList);

        //mAdapter = new FeedListAdapter(this, dots);
        //mListDots.setAdapter(mAdapter);
    }

    /**
     * Update the list to the main screen
     * @param dotCategories
     */
    public void updateDataMain(List<DotCategory> dotCategories) {
        mPositionSelected = getAppPreferences().getInt("filter_time", 4);
        //If dots is null, it is because the method is called from Main
        //If dots != null, it is because the method is called from Dialog Filter Time
        if (mListDots != null) {
            List<DotCategory> newList = new ArrayList<>();
            if (dotCategories != null)
                newList.addAll(dotCategories);
            else {
                dotCategories = new ArrayList<>();
                if (mPositionSelected != 6) {
                    dotCategories.addAll(filterListByTime(mPositionSelected));
                    newList.addAll(dotCategories);
                } else {
                    changeDatesRange(null, -1, -1);
                    List<Calendar> mDates = new ArrayList<>();
                    mDates.add(0, mFrom);
                    mDates.add(0, mTo);
                    dotCategories.addAll(filterListBetweenDates(mDates));
                    newList.addAll(dotCategories);
                }
            }

            List<DotCategory> newDotCategories = groupListByDate(newList);
            // Group dots if is the same category in a block day

            List<GroupDot> mGroups = new ArrayList<>();
            List<DotCategory> mGroupByCategory = new ArrayList<>();
            int more = 0;
            String mDateNameButton = null;
            String mDateTagButton = null;

            for (int k = 0; k < newDotCategories.size(); k++) {
                if (newDotCategories.get(k).getCategoryId() == -1) {
                    // If date is different to Today
                    if (!newDotCategories.get(k).getDate().equals(getString(R.string.filter_today))) {
                        mDateNameButton = newDotCategories.get(k).getDate();
                        mDateTagButton = newDotCategories.get(k + 1).getDate();
                    }

                    mGroups.add(new GroupDot(newDotCategories.get(k)));  // Add to new group dot
                    newDotCategories.remove(k); // Remove in original list
                    k--;
                } else {
                    // For know if has more of one in this block of date
                    for (int i = 0; i < newDotCategories.size(); i++) {
                        if (newDotCategories.get(i).getCategoryId() != -1) {
                            if (newDotCategories.get(k).getCategoryId() == newDotCategories.get(i).getCategoryId())
                                more++;   // sum for know
                        } else
                            break;
                    }

                    if (more > 1) {
                        // Copy the original list
                        List<DotCategory> copyList = new ArrayList<>(newDotCategories);
                        GroupDot group = new GroupDot();
                        DotCategory dotCategory = newDotCategories.get(k);
                        group.setDotCategory(dotCategory);
                        // For add the dots with same category in a group day
                        for (int j = 0; j < copyList.size(); j++) {
                            if (copyList.get(j).getCategoryId() != -1) {  // Not header date
                                if (dotCategory.getCategoryId() == copyList.get(j).getCategoryId()) {
                                    mGroupByCategory.add(copyList.get(j));  // It has more of one
                                    newDotCategories.remove(copyList.get(j));
                                }
                            } else // If get to next header date, break
                                break;
                        }
                        group.setGroup(mGroupByCategory);
                        mGroups.add(group);
                        mGroupByCategory = new ArrayList<>(); // Reset
                    } else {
                        mGroups.add(new GroupDot(newDotCategories.get(k)));   // If is just one
                        newDotCategories.remove(k);
                    }

                    try {
                        if (newDotCategories.get(k).getCategoryId() == -1)
                            if (mDateTagButton != null)
                                mGroups.add(new GroupDot(mDateNameButton, mDateTagButton));   // If finish block day, add button.
                    } catch (IndexOutOfBoundsException e) {
                        if (mDateNameButton != null)
                            mGroups.add(new GroupDot(mDateNameButton, mDateTagButton));   // Last item
                    }

                    more = 0;
                    k--;


                }
            }

            mMainAdapter = new MainFeedListAdapter(this, mGroups);
            mListDots.setAdapter(mMainAdapter);
        }
    }

    /**
     * Update list data for every feed
     * @param dotCategories
     */
    public void updateDataEvery(List<DotCategory> dotCategories, int category) {
        mPositionSelected = getAppPreferences().getInt("filter_time", 4);
        //If dots is null, it is because the method is called from every feed
        //If dots != null, it is because the method is called from Dialog Filter Time
        if (mListDots != null) {
            List<DotCategory> newList = new ArrayList<>();
            if (dotCategories != null)
                newList.addAll(dotCategories);
            else {
                dotCategories = new ArrayList<>();
                if (mPositionSelected != 6) {
                    dotCategories.addAll(filterListByTimeCategory(mPositionSelected, category));
                    newList.addAll(dotCategories);
                } else {
                    changeDatesRange(null, -1, -1);
                    List<Calendar> mDates = new ArrayList<>();
                    mDates.add(0, mFrom);
                    mDates.add(0, mTo);
                    dotCategories.addAll(filterListBetweenDates(mDates));
                    newList.addAll(dotCategories);
                }
            }
            List<DotCategory> newDotCategories = groupListByDate(newList);
            mAdapter = new FeedListAdapter(this, newDotCategories);
            mListDots.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }

    /**---------------------------------------------------------------------------------------------------*/
    /**--------------------------------------Children methods------------------------------------------------*/

    /**
     * Validate if there are children
     * @return
     */
    protected boolean areThereChildren() {
        mChildren = mDB.getAllChildChildren();
        if (!mChildren.isEmpty())
            return true;
        else
            return false;
    }

    /**
     * Validate if just there is one child or no
     * @return
     */
    protected boolean isThereChild() {
        mChildren = mDB.getAllChildChildren();
        if (mChildren.size() > 1)
            return true;
        else
            return false;
    }

    /**
     * search child selected
     */
    protected void childSelected() {
        if (areThereChildren()) {
            mChildren = mDB.getAllChildChildren();
            for (Child c : mChildren) {
                if (c.getID().equals(mPreferences.getString("child_selected", ""))) {
                    mChild = c;
                }
            }
        }
    }

    /**
     * get child selected
     * @return
     */
    public Child getChild() {
        childSelected();
        return mChild;
    }

    /**
     * get list children from db
     * @return
     */
    public List<Child> getChildren() {
        mChildren = mDB.getAllChildChildren();
        return mChildren;
    }

    /**
     * get list children without child selected
     * @return
     */
    public List<Child> getChildrenWithoutSelected() {
        mChildren = getChildren();
        childSelected();
        mChildren.remove(mChild);
        return mChildren;
    }

    /**
     * Calculate age of child if he has more of 3 years, it is becauses in growth
     * there is a seek bar for head circumference where the child should be to have more 3 years
     * @return
     */
    protected boolean calculateAge() {
        childSelected();
        Calendar mDate = Calendar.getInstance();
        SimpleDateFormat mFormat = new SimpleDateFormat(getString(R.string.format_date_service));
        try {
            mDate.setTime(mFormat.parse(mChild.getDob()));
            mDate.add(Calendar.MONTH, 1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        LocalDate mBirth;

        if (mDate.get(Calendar.MONTH) != 0)
            mBirth = new LocalDate(mDate.get(Calendar.YEAR), mDate.get(Calendar.MONTH), mDate.get(Calendar.DAY_OF_MONTH));
        else {
            mDate.add(Calendar.YEAR, -1);
            mBirth = new LocalDate(mDate.get(Calendar.YEAR), 12, mDate.get(Calendar.DAY_OF_MONTH));
        }

        LocalDate mNow = new LocalDate();
        Period mPeriod = new Period(mBirth, mNow);

        if (mPeriod.getYears() >= 3)
            return true;
        else
            return  false;
    }

    /**
     * Calculate time for vaccination, rest min age with child age selected
     * @param max
     * @return
     */
    protected String calculateVaccination(int max) {
        childSelected();
        Calendar mDate = Calendar.getInstance();
        SimpleDateFormat mFormat = new SimpleDateFormat(getString(R.string.format_date_service));
        try {
            mDate.setTime(mFormat.parse(mChild.getDob()));
            mDate.add(Calendar.MONTH, 1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        LocalDate mBirth;

        if (mDate.get(Calendar.MONTH) != 0)
            mBirth = new LocalDate(mDate.get(Calendar.YEAR), mDate.get(Calendar.MONTH), mDate.get(Calendar.DAY_OF_MONTH));
        else {
            mDate.add(Calendar.YEAR, -1);
            mBirth = new LocalDate(mDate.get(Calendar.YEAR), 12, mDate.get(Calendar.DAY_OF_MONTH));
        }
        LocalDate mNow = new LocalDate();
        Period mPeriod = new Period(mBirth, mNow);
        Months age = Months.monthsBetween(mBirth, mNow);

        if (age.getMonths() > max)
            return getString(R.string.time_now);
        else
            return  (max - age.getMonths()) + " " +
                    getString(R.string.children_age_months) + " " +
                    mPeriod.getDays() +
                    getString(R.string.children_age_days);
    }

    /**
     * Update name of baby in empty content in every feed if there isn't data
     * @param res
     */
    protected void updateContentEmptyList(int res) {
        mEmptyContent.setText(
                String.format(getString(res), mChild.getName()));
    }

    /**
     * Update name of baby in empty content with just text name
     * @param name
     */
    protected void updateContentEmptyList(String name) {
        mEmptyContent.setText(
                String.format(getString(R.string.feed_content_list_empty), name));
    }

    /**---------------------------------------------------------------------------------------------------*/
    /**--------------------------------getter methods--------------------------------------------------*/
    /**
     * getPreferences
     * @return
     */
    protected SharedPreferences getAppPreferences() {
        return mPreferences;
    }

    /**
     * Method for get EditPreferences variable
     * @return
     */
    protected SharedPreferences.Editor getEditorPreferences() {
        return mEditPreferences;
    }

    /**
     * Public method of views
     * @return
     */
    public TextView getEmptyTitle() {
        return mEmptyTitle;
    }

    public TextView getEmptyContent() {
        return mEmptyContent;
    }

    public ImageView getButtonHeader() {
        return mButtonHeader;
    }

    public Button getChooseTime() {
        return mChooseTime;
    }

    public Button getEmptyChooseTime() {
        return mEmptyChooseTime;
    }

    public View getHeaderList() {
        return mHeaderList;
    }

    /**-----------------------------------------------------------------------------------------------------*/
    /**----------------------------------DataBase Methods------------------------------------------------*/

    /**
     * Method for add a dot
     * @param dotCategory
     */
    protected void addNewDot(DotCategory dotCategory) {
        mDB.addNewDot(dotCategory, mChild);
    }

    /**
     * Method for update a dot
     * @param dotCategory
     */
    protected void updateDot(DotCategory dotCategory) {
        mDB.updateDot(dotCategory);
    }

    public List<Child> getAllChildChildren() {
        return mDB.getAllChildChildren();
    }
    public void addNewChild(Child child) {
        mDB.addNewChild(child);
    }

    public void updateChild(Child child) {
        mDB.updateChild(child);
    }

    public void deleteChild(String id) {
        mDB.deleteChild(id);
    }

    /**
     * Method for delete a dot
     * @param dotCategory
     */
    public void deleteDot(DotCategory dotCategory) {
        mDB.deleteDot(dotCategory);
    }

    /**
     * Method for delete an image
     * @param image
     */
    public void deleteImage(Image image) {
        mDB.deleteImage(image);
    }

    /**
     * Adds new Child for delete when have internet connection
     * @param id children
     *
     */
    public void addPendingChild(String id){
        mDB.addPendingChild(id);
    }

    /**
     * Adds new dot for delete when have internet connection
     * @param id dot
     *
     */
    public void addPendingDot(String id){
        mDB.addPendingDot(id);
    }

    /**
     * Return list of Children id in database for delete in server
     */
    public List<String> getAllPendingChildren(){
        return mDB.getAllPendingChildren();
    }

    /**
     * Return list of dots id in database for delete in server
     */
    public List<String> getAllPendingDots(){
        return mDB.getAllPendingDots();
    }

    /**
     * Delete all pending children by delete once delete server success
     */
    public void deletePendingChildren(){
        mDB.deletePendingChildren();
    }

    /**
     * Delete all pending dots by delete once delete server success
     */
    public void deletePendingDots(){
        mDB.deletePendingDots();
    }

    /**
     * Method for get list images by entry
     * @param id
     * @return
     */
    public List<Image> getImagesDB(String id) {
        return mDB.getImages(id);
    }

    /**
     * Method for get an image
     * @param id
     * @return image object
     */
    public Image getImageDB(String id) {
        return mDB.getImage(id);
    }

    /**
     * Method for get list a dot filter
     * @param category
     * @return
     */
    public List<DotCategory> getDotsByCategory(int category) {
        childSelected();
        return mDB.getDotsOfCategoryForChild(category, mChild.getID());
    }

    /**
     * Method for get list a dot filter
     * @return
     */
    public List<DotCategory> getDots() {
        childSelected();
        return mDB.getChildDots(mChild);
    }

    /**
     * Method for get list a dot filter
     * @return
     */
    public List<DotCategory> getDots(Child child) {
        return mDB.getChildDots(child);
    }

    /**
     * Method will return list dots with doses
     * @param dotCategories
     * @return
     */
    public List<DotValues> getDosesVaccines(List<DotCategory> dotCategories) {
        List<DotValues> vaccines = new ArrayList<>();
        for (int i = 0; i < dotCategories.size(); i++) {
            DotCategory bd = dotCategories.get(i);
            DotValues vaccine = getDotFromJSONArray(bd.getEntryData());
            vaccines.add(vaccine);
        }
        return vaccines;
    }

    /**
     * Method will return last weight registered of child
     * @return
     */
    public float getLastWeightChild() {
        List<DotCategory> dots = getDotsByCategory(6);
        if (dots != null && !dots.isEmpty()) {
            List<DotValues> growth = getListDotFromJSONArray(dots.get(0).getEntryData());
            if (growth.get(0).getValue() == 0) {
                return growth.get(0).getDetailValue();
            }
        }
        return 0;
    }


    /**
     * Method for find the next doses
     * @param vaccine
     * @param vaccines
     * @return
     */
    public int getNextDoseByVaccine(Vaccine vaccine, List<DotValues> vaccines) {
        ArrayList<Integer> maxDoses = new ArrayList<>();
        ArrayList<Integer> findDoses;
        ArrayList<Integer> doses = new ArrayList<>();
        // List by Dose
        for (int i = 1; i <= vaccine.getMaxDose(); i++) {
            maxDoses.add(i);
        }
        // List by doses in db
        for (int i = 0; i < vaccines.size(); i++) {
            if (vaccine.getValue() == vaccines.get(i).getValue()) {
                doses.add(Math.round(vaccines.get(i).getDetailValue()));
            }
        }

        // Find the next doses
        findDoses = new ArrayList<>(maxDoses);
        findDoses.removeAll(doses);

        // return the younger number
        try {
            return Collections.min(findDoses);
        } catch (NoSuchElementException e) {
            return -1;  // If doses finished
        }
    }


    /**
     * Convert several items dot to JSONArray
     * @param dotValues
     * @return
     */
    protected JSONArray getJSONArrayFromListDot(List<DotValues> dotValues) {
        JSONArray array = new JSONArray();
        for (int i = 0; i < dotValues.size(); i++) {
            JSONObject object = getJSONObjectFromDot(dotValues.get(i));
            array.put(object);
        }
        return array;
    }

    /**
     * Convert dot growth in JSONArray
      * @param weight
     * @param height
     * @param head
     * @return
     */
    protected JSONArray getJSONArrayFromGrowthDot(DotValues weight, DotValues height, DotValues head) {
        JSONArray array = new JSONArray();
        array.put(getJSONObjectFromDot(weight));
        array.put(getJSONObjectFromDot(height));
        if (calculateAge())
            if (head != null)
                array.put(getJSONObjectFromDot(head));
        return array;
    }

    /**
     * Convert dot diapers in JSONArray
     * @param pee
     * @param poo
     * @return
     */
    protected JSONArray getJSONArrayFromDiapersDot(DotValues pee, DotValues poo) {
        JSONArray array = new JSONArray();
        if (pee != null)
            array.put(getJSONObjectFromDot(pee));
        if (poo != null)
            array.put(getJSONObjectFromDot(poo));
        return array;
    }

    /**
     * Convert one item dot to JSONArray
     * @param dotValues
     * @return
     */
    protected JSONArray getJSONArrayFromListDot(DotValues dotValues) {
        JSONArray array = new JSONArray();
        array.put(getJSONObjectFromDot(dotValues));
        return array;
    }

    /**
     * Convert one item dot to JSONObject
     * @param dotValues
     * @return
     */
    protected JSONObject getJSONObjectFromDot(DotValues dotValues) {
        //JSONObject mJObject = new JSONObject();
        Gson gson = new Gson();

        /*try {
            mJObject.put("group", dotValues.getGroup());
            mJObject.put("value", dotValues.getValue());
            mJObject.put("detailValue", dotValues.getDetailValue());
            mJObject.put("text", dotValues.getText());
            mJObject.put("skipped", dotValues.getSkipped());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return mJObject;
        */
        try {
            return new JSONObject(gson.toJson(dotValues));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * get Dot from JSONArray
     * @param array
     * @return
     */
    protected DotValues getDotFromJSONArray(JSONArray array) {
        DotValues dotValues = new DotValues();
        Gson gson = new Gson();
        /*try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject currentDot = (JSONObject) array.get(i);
                if (currentDot.has("group"))
                    dotValues.setGroup(currentDot.getInt("group"));
                if (currentDot.has("value"))
                    dotValues.setValue(currentDot.getInt("value"));
                if (currentDot.has("detailValue"))
                    dotValues.setDetailValue((float) currentDot.getDouble("detailValue"));
                if (currentDot.has("text"))
                    dotValues.setText(currentDot.getString("text"));
                if (currentDot.has("skipped"))
                    dotValues.setSkipped(currentDot.getInt("skipped"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dotValues;*/

        try {
            for (int i = 0; i < array.length(); i++) {
                dotValues = gson.fromJson(array.get(i).toString(), DotValues.class);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dotValues;
    }

    /**
     * get List Dot from JSONArray
     * @param array
     * @return
     */
    protected List<DotValues> getListDotFromJSONArray(JSONArray array) {
        List<DotValues> dotValues = new ArrayList<>();
        Gson gson = new Gson();
        /*try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject currentDot = (JSONObject) array.get(i);
                DotValues values = new DotValues();
                if (currentDot.has("group"))
                    values.setGroup(currentDot.getInt("group"));
                if (currentDot.has("value"))
                    values.setValue(currentDot.getInt("value"));
                if (currentDot.has("detailValue"))
                    values.setDetailValue((float) currentDot.getDouble("detailValue"));
                if (currentDot.has("text"))
                    values.setText(currentDot.getString("text"));
                if (currentDot.has("skipped"))
                    values.setSkipped(currentDot.getInt("skipped"));
                dotValues.add(values);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dotValues;*/

        try {
            for (int i = 0; i < array.length(); i++) {
                dotValues.add(gson.fromJson(array.get(i).toString(), DotValues.class));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dotValues;
    }

    /**
     * Method for order list with header by Dates
     * Example: "Today", "Yesterday", "Sunday", "June 24, 2015
     * @param dotCategories
     * @return
     */
    protected List<DotCategory> groupListByDate(List<DotCategory> dotCategories) {
        List<DotCategory> newList = new ArrayList<>();
        newList.addAll(dotCategories);
        int count = 0;  // Variable for sum number dates the same type

        SimpleDateFormat mFormatDB = new SimpleDateFormat(getString(R.string.format_date_service));
        SimpleDateFormat mFormatDayWeek = new SimpleDateFormat(getString(R.string.format_day), new Locale(mLocale[getAppPreferences().getInt("language", 0)]));
        SimpleDateFormat mFormatDate = new SimpleDateFormat(getString(R.string.format_pretty_time), new Locale(mLocale[getAppPreferences().getInt("language", 0)]));
        Calendar mDate = Calendar.getInstance();
        Calendar mLastDate = Calendar.getInstance();
        Calendar mToday = Calendar.getInstance();
        Calendar mYesterday = Calendar.getInstance();
        mYesterday.add(Calendar.DAY_OF_MONTH, -1);

        Calendar mRangeWeekDays = Calendar.getInstance();
        mRangeWeekDays.add(Calendar.DAY_OF_MONTH, -7);

        // Filter for header date
        for (int i = 0; i < dotCategories.size(); i++) {
            try {
                mDate.setTime(mFormatDB.parse(dotCategories.get(i).getDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (i == 0) {  // First Date
                if (mDate.get(Calendar.YEAR) == mToday.get(Calendar.YEAR) &&
                        mDate.get(Calendar.MONTH) == mToday.get(Calendar.MONTH) &&
                        mDate.get(Calendar.DAY_OF_MONTH) == mToday.get(Calendar.DAY_OF_MONTH))
                    // Is Today
                    newList.add(0, new DotCategory(getString(R.string.filter_today)));
                else if (mDate.get(Calendar.YEAR) == mYesterday.get(Calendar.YEAR) &&
                        mDate.get(Calendar.MONTH) == mYesterday.get(Calendar.MONTH) &&
                        mDate.get(Calendar.DAY_OF_MONTH) == mYesterday.get(Calendar.DAY_OF_MONTH))
                    // Is yesterday
                    newList.add(0, new DotCategory(getString(R.string.filter_yesterday)));
                else if ((mDate.get(Calendar.YEAR) == mYesterday.get(Calendar.YEAR) &&
                        mDate.get(Calendar.MONTH) == mYesterday.get(Calendar.MONTH) &&
                        mDate.get(Calendar.DAY_OF_MONTH) < mYesterday.get(Calendar.DAY_OF_MONTH)) &&
                        (mDate.get(Calendar.YEAR) == mRangeWeekDays.get(Calendar.YEAR) &&
                                mDate.get(Calendar.MONTH) == mRangeWeekDays.get(Calendar.MONTH) &&
                                mDate.get(Calendar.DAY_OF_MONTH) > mRangeWeekDays.get(Calendar.DAY_OF_MONTH)))
                    // Between yesterday and 5 days ago
                    newList.add(0, new DotCategory(mFormatDayWeek.format(mDate.getTime()).substring(0, 1).toUpperCase() +
                            mFormatDayWeek.format(mDate.getTime()).substring(1).toLowerCase()));
                else
                    // Old date
                    newList.add(0, new DotCategory(mFormatDate.format(mDate.getTime()).substring(0, 1).toUpperCase() +
                            mFormatDate.format(mDate.getTime()).substring(1).toLowerCase()));

                count = 1;
                mLastDate = mDate;
                mDate = Calendar.getInstance();  // Reset
            } else {
                if (!(mDate.get(Calendar.YEAR) == mLastDate.get(Calendar.YEAR)
                        && mDate.get(Calendar.MONTH) == mLastDate.get(Calendar.MONTH)
                        && mDate.get(Calendar.DAY_OF_MONTH) == mLastDate.get(Calendar.DAY_OF_MONTH))) {
                    if (mDate.get(Calendar.YEAR) == mYesterday.get(Calendar.YEAR) &&
                            mDate.get(Calendar.MONTH) == mYesterday.get(Calendar.MONTH) &&
                            mDate.get(Calendar.DAY_OF_MONTH) == mYesterday.get(Calendar.DAY_OF_MONTH))
                        // Is yesterday
                        newList.add(i + count, new DotCategory(getString(R.string.filter_yesterday)));
                    else if ((mDate.get(Calendar.YEAR) == mYesterday.get(Calendar.YEAR) &&
                            mDate.get(Calendar.MONTH) == mYesterday.get(Calendar.MONTH) &&
                            mDate.get(Calendar.DAY_OF_MONTH) < mYesterday.get(Calendar.DAY_OF_MONTH)) &&
                            (mDate.get(Calendar.YEAR) == mRangeWeekDays.get(Calendar.YEAR) &&
                                    mDate.get(Calendar.MONTH) == mRangeWeekDays.get(Calendar.MONTH) &&
                                    mDate.get(Calendar.DAY_OF_MONTH) > mRangeWeekDays.get(Calendar.DAY_OF_MONTH)))
                        // Between yesterday and 5 days ago
                        newList.add(i + count, new DotCategory(mFormatDayWeek.format(mDate.getTime()).substring(0, 1).toUpperCase() +
                                mFormatDayWeek.format(mDate.getTime()).substring(1).toLowerCase()));
                    else
                        // Old date
                        newList.add(i + count, new DotCategory(mFormatDate.format(mDate.getTime()).substring(0, 1).toUpperCase() +
                                mFormatDate.format(mDate.getTime()).substring(1).toLowerCase()));

                    count = count + 1;
                    mLastDate = mDate;
                    mDate = Calendar.getInstance();  // Reset
                }
            }
        }
        return newList;
    }

    /**
     * Method for filter dots by range date
     * @param higher
     * @param less
     * @param dotCategories
     */
    public List<DotCategory> filterByRangeDate(Calendar higher, Calendar less, List<DotCategory> dotCategories) {
        List<DotCategory> newList = new ArrayList<>();

        SimpleDateFormat mFormatDB = new SimpleDateFormat(getString(R.string.format_date_service));
        Calendar date = Calendar.getInstance();
        Calendar dateHigher = higher;
        Calendar dateLess = less;

        // Filter for header date
        for (int i = 0; i < dotCategories.size(); i++) {
            try {
                date.setTime(mFormatDB.parse(dotCategories.get(i).getDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // If date >= dateLess or date <= dateHigher
            if ((date.before(dateHigher) || date.equals(dateHigher)) &&
                    (date.after(dateLess) || date.equals(dateLess))) {
                newList.add(dotCategories.get(i));
                date = Calendar.getInstance(); // Reset
            }
        }

        return newList;
    }

    @Override
    public void onClick(View v) {}

    /**---------------------------------------------------------------------------------------------------*/
    /**-----------------------------------Dialog methods---------------------------------------------*/
    /**
     * Dialog for add a list item basic
     * */
    protected void showListOptionDialog(int title, final String[] items, int style) {
        AlertDialog.Builder mDialog = new AlertDialog.Builder(this, style);
        mDialog.setTitle(title);
        mDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        showDialogDose(items[0], R.style.TemperatureStyleDialog, 0);
                        break;
                    case 1:
                        showDialogDose(items[1], R.style.TemperatureStyleDialog, 1);
                        break;
                }
            }
        });
        mDialog.create();
        mDialog.show();
    }

    /**
     * Show dialog for calculate doses
     * @param title
     * @param style
     */
    private void showDialogDose(String title, int style, int position) {
        Conversion mConversion = new Conversion();
        View view = getLayoutInflater().inflate(R.layout.custom_temperature_doses_details, null);

        TextView mWeight = (TextView) view.findViewById(R.id.weight_child);
        TextView mDose = (TextView) view.findViewById(R.id.doses_child);

        float weight = getLastWeightChild();

        if (getAppPreferences().getInt("unit", 0) == 0)   // Kg
            mWeight.setText(String.format(getString(R.string.temperature_weight_kg), weight));
        else
            mWeight.setText(String.format(getString(R.string.temperature_weight_lb), mConversion.kgToPounds((double) weight)));

        if (position == 0)
            calculateIbuprofenDose(weight, mDose);
        else
            calculateParacetamolDose(weight, mDose);

        AlertDialog.Builder mDialog = new AlertDialog.Builder(this, style);
        mDialog.setTitle(title);

        mDialog.setView(view);
        mDialog.setNegativeButton(R.string.button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        mDialog.setCancelable(false);
        mDialog.create();
        mDialog.show();
    }

    /**
     * Calculate doses for Ibuprofen
     * @param weight
     * @param textView
     */
    private void calculateIbuprofenDose(float weight, TextView textView) {
        if (getAppPreferences().getInt("unit", 0) == 0) {   // Kg
            if (weight <= 6) {
                textView.setText(R.string.temperature_doses_doctor);
            } else if (weight > 6 && weight <= 8) {
                textView.setText(String.format(getString(R.string.temperature_doses_mg),
                        50));
            } else if (weight > 8 && weight <= 10) {
                textView.setText(String.format(getString(R.string.temperature_doses_mg),
                        75));
            } else if (weight > 11 && weight <= 16) {
                textView.setText(String.format(getString(R.string.temperature_doses_mg),
                        100));
            } else if (weight > 17 && weight <= 21) {
                textView.setText(String.format(getString(R.string.temperature_doses_mg),
                        150));
            } else if (weight > 22 && weight <= 27) {
                textView.setText(String.format(getString(R.string.temperature_doses_mg),
                        200));
            } else if (weight > 28 && weight <= 32) {
                textView.setText(String.format(getString(R.string.temperature_doses_mg),
                        250));
            } else if (weight > 33 && weight <= 43) {
                textView.setText(String.format(getString(R.string.temperature_doses_mg),
                        300));
            } else if (weight > 43) {
                textView.setText(R.string.temperature_doses_adult);
            }
        } else {    // Lb
            Conversion mConversion = new Conversion();
            float weightLb = mConversion.kgToPounds(weight);
            if (weightLb <= 12) {
                textView.setText(R.string.temperature_doses_doctor);
            } else if (weightLb > 12 && weightLb <= 17) {
                textView.setText(String.format(getString(R.string.temperature_doses_mg),
                        50));
            } else if (weightLb > 18 && weightLb <= 23) {
                textView.setText(String.format(getString(R.string.temperature_doses_mg),
                        75));
            } else if (weightLb > 23 && weightLb <= 35) {
                textView.setText(String.format(getString(R.string.temperature_doses_mg),
                        100));
            } else if (weightLb > 35 && weightLb <= 47) {
                textView.setText(String.format(getString(R.string.temperature_doses_mg),
                        150));
            } else if (weightLb > 47 && weightLb <= 59) {
                textView.setText(String.format(getString(R.string.temperature_doses_mg),
                        200));
            } else if (weightLb > 59 && weightLb <= 71) {
                textView.setText(String.format(getString(R.string.temperature_doses_mg),
                        250));
            } else if (weightLb > 71 && weightLb <= 95) {
                textView.setText(String.format(getString(R.string.temperature_doses_mg),
                        300));
            } else if (weightLb > 95) {
                textView.setText(R.string.temperature_doses_adult);
            }
        }
    }

    /**
     * Calculate doses for Paracetamol
     * @param weight
     * @param textView
     */
    private void calculateParacetamolDose(float weight, TextView textView) {
        if (getAppPreferences().getInt("unit", 0) == 0) {   // Kg
            if (weight <= 5.4) {
                textView.setText(R.string.temperature_doses_doctor);
            } else if (weight > 5.4 && weight <= 7.9) {
                textView.setText(String.format(getString(R.string.temperature_doses_mg),
                        80));
            } else if (weight > 7.9 && weight <= 10.9) {
                textView.setText(String.format(getString(R.string.temperature_doses_mg),
                        120));
            } else if (weight > 11 && weight <= 15.9) {
                textView.setText(String.format(getString(R.string.temperature_doses_mg),
                        160));
            } else if (weight > 15.9 && weight <= 21.9) {
                textView.setText(String.format(getString(R.string.temperature_doses_mg),
                        240));
            } else if (weight > 21.9 && weight <= 26.9) {
                textView.setText(String.format(getString(R.string.temperature_doses_mg),
                        320));
            } else if (weight > 26.9 && weight <= 31.9) {
                textView.setText(String.format(getString(R.string.temperature_doses_mg),
                        400));
            } else if (weight > 31.9 && weight <= 43.9) {
                textView.setText(String.format(getString(R.string.temperature_doses_mg),
                        480));
            }
        } else {    // Lb
            Conversion mConversion = new Conversion();
            float weightLb = mConversion.kgToPounds(weight);
            if (weightLb <= 11) {
                textView.setText(R.string.temperature_doses_doctor);
            } else if (weightLb > 12 && weightLb <= 17) {
                textView.setText(String.format(getString(R.string.temperature_doses_mg),
                        80));
            } else if (weightLb > 18 && weightLb <= 23) {
                textView.setText(String.format(getString(R.string.temperature_doses_mg),
                        120));
            } else if (weightLb > 23 && weightLb <= 35) {
                textView.setText(String.format(getString(R.string.temperature_doses_mg),
                        160));
            } else if (weightLb > 35 && weightLb <= 47) {
                textView.setText(String.format(getString(R.string.temperature_doses_mg),
                        240));
            } else if (weightLb > 47 && weightLb <= 59) {
                textView.setText(String.format(getString(R.string.temperature_doses_mg),
                        320));
            } else if (weightLb > 59 && weightLb <= 71) {
                textView.setText(String.format(getString(R.string.temperature_doses_mg),
                        400));
            } else if (weightLb > 71 && weightLb <= 95) {
                textView.setText(String.format(getString(R.string.temperature_doses_mg),
                        480));
            }
        }
    }

    /**
     * Dialog for add a custom view
     * @param title
     * @param layout
     * @param style
     */
    protected void showSimpleDialog(int title, View layout, int style) {
        AlertDialog.Builder mDialog = new AlertDialog.Builder(this, style);
        mDialog.setTitle(title);

        mDialog.setView(layout);
        mDialog.setNegativeButton(R.string.button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        mDialog.setCancelable(false);
        mDialog.create();
        mDialog.show();
    }

    /**
     * Dialog for add a custom view without button bottom
     * @param title
     * @param layout
     * @param style
     * @return
     */
    protected AlertDialog showSimpleDialogCancelable(int title, View layout, int style) {
        AlertDialog.Builder mDialog = new AlertDialog.Builder(this, style);
        mDialog.setTitle(title);
        mDialog.setView(layout);
        AlertDialog dialog = mDialog.create();
        dialog.show();
        return dialog;
    }

    /**
     * Dialog for add a view static
     * @param title
     * @param layout
     * @param style
     */
    protected void showSimpleDialog(String title, int layout, int style) {
        AlertDialog.Builder mDialog = new AlertDialog.Builder(this, style);
        mDialog.setTitle(title);

        mDialog.setView(layout);
        mDialog.setNegativeButton(R.string.button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        mDialog.setCancelable(false);
        mDialog.create();
        mDialog.show();
    }


    /**
     * Dialog for choose time in every feed
     * @param style
     * @param category
     * @param color
     */
    protected void showTimeListDialog(final int style, final int category, final int color) {

        customFilterEveryFeed(category, color, style);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //((CheckedTextView) view).setCheckMarkDrawable(R.drawable.style_radio_diapers);
                mPositionSelected = position;
                if (mPositionSelected == 6) {
                    changeDatesRange(mContainerDates, color, style);
                    mContainerDates.setVisibility(View.VISIBLE);
                } else {
                    mContainerDates.setVisibility(View.GONE);
                }
            }
        });
        setListHeightInsideScroll(mList);
        AlertDialog.Builder mDialog = new AlertDialog.Builder(this, style);
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
                getChooseTime().setText(
                        getString(R.string.filter_title_message) + " " +
                                getResources().getStringArray(R.array.filter_time_list)[mPositionSelected]);
                getEmptyChooseTime().setText(
                        getString(R.string.filter_title_message) + " " +
                                getResources().getStringArray(R.array.filter_time_list)[mPositionSelected]);
                if (mPositionSelected == 6) {
                    if (!mError) {
                        List<Calendar> mDates = new ArrayList<>();
                        mDates.add(0, mFrom);
                        mDates.add(0, mTo);
                        updateDataEvery(filterListBetweenDates(mDates, category), -1);
                        // We save the filter time selected
                        getEditorPreferences().putInt("filter_time", mPositionSelected);
                        getEditorPreferences().commit();
                        dialog.dismiss();
                    }
                } else {
                    updateDataEvery(filterListByTimeCategory(mPositionSelected, category), -1);
                    // We save the filter time selected
                    getEditorPreferences().putInt("filter_time", mPositionSelected);
                    getEditorPreferences().commit();
                    dialog.dismiss();
                }

            }
        });
    }

    /**
     * get List dots filter by category
     * @param position
     * @param category
     * @return
     */
    protected final List<DotCategory> filterListByTimeCategory(int position, int category) {
        List<DotCategory> newFilter = new ArrayList<>();
        Calendar mToday = Calendar.getInstance();
        switch (position) {
            case 0:  // 7 days
                Calendar m7daysAgo = Calendar.getInstance();
                m7daysAgo.add(Calendar.DAY_OF_MONTH, -7);
                newFilter.addAll(filterByRangeDate(mToday, m7daysAgo, getDotsByCategory(category)));
                break;
            case 1: // 14 days
                Calendar m14daysAgo = Calendar.getInstance();
                m14daysAgo.add(Calendar.DAY_OF_MONTH, -14);
                newFilter.addAll(filterByRangeDate(mToday, m14daysAgo, getDotsByCategory(category)));
                break;
            case 2: // 30 days
                Calendar m30daysAgo = Calendar.getInstance();
                m30daysAgo.add(Calendar.DAY_OF_MONTH, -30);
                newFilter.addAll(filterByRangeDate(mToday, m30daysAgo, getDotsByCategory(category)));
                break;
            case 3: // 2 months
                Calendar m2MonthsAgo = Calendar.getInstance();
                m2MonthsAgo.add(Calendar.MONTH, -2);
                newFilter.addAll(filterByRangeDate(mToday, m2MonthsAgo, getDotsByCategory(category)));
                break;
            case 4: // 6 months
                Calendar m6MonthsAgo = Calendar.getInstance();
                m6MonthsAgo.add(Calendar.MONTH, -6);
                newFilter.addAll(filterByRangeDate(mToday, m6MonthsAgo, getDotsByCategory(category)));
                break;
            case 5: // Since birth
                newFilter.addAll(getDots());
                break;
            case 6: //Between dates
                break;
        }

        return newFilter;
    }

    /**
     * Dialog for choose time in main feed
     * */
    protected void showTimeListDialog() {
        customFilterMainFeed();
        mListMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPositionSelected = position;
                if (mPositionSelected == 6) {
                    changeDatesRange(mFilterMainView, R.color.text_feed, R.style.NormalStyleDialog);
                    mContainerDatesMain.setVisibility(View.VISIBLE);
                } else {
                    mContainerDatesMain.setVisibility(View.GONE);
                }
            }
        });

        setListHeightInsideScroll(mListMain);
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
        mDialog.setView(mFilterMainView);
        final AlertDialog dialog = mDialog.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getChooseTime().setText(
                        getString(R.string.filter_title_message) + " " +
                                getResources().getStringArray(R.array.filter_time_list)[mPositionSelected]);
                getEmptyChooseTime().setText(
                        getString(R.string.filter_title_message) + " " +
                                getResources().getStringArray(R.array.filter_time_list)[mPositionSelected]);
                if (mPositionSelected == 6) {
                    if (!mError) {
                        List<Calendar> mDates = new ArrayList<>();
                        mDates.add(0, mFrom);
                        mDates.add(0, mTo);
                        updateDataMain(filterListBetweenDates(mDates));
                        // We save the filter time selected
                        getEditorPreferences().putInt("filter_time", mPositionSelected);
                        getEditorPreferences().commit();
                        dialog.dismiss();
                    }
                } else {
                    updateDataMain(filterListByTime(mPositionSelected));
                    // We save the filter time selected
                    getEditorPreferences().putInt("filter_time", mPositionSelected);
                    getEditorPreferences().commit();
                    dialog.dismiss();
                }

            }
        });
    }

    /**
     * Custom view in dialog list time
     */
    private void customFilterMainFeed() {
        Context mContextTheme = new ContextThemeWrapper(this, R.style.NormalStyleDialog);
        mFilterMainView = getLayoutInflater().from(mContextTheme)
                .inflate(R.layout.custom_filter_time, null, false);

        mContainerDatesMain = mFilterMainView.findViewById(R.id.container_dates);
        mListMain = (ListView) mFilterMainView.findViewById(R.id.list_filter_time);
        mListMain.setAdapter(new ArrayAdapter<>(this, R.layout.custom_item_filter_time_main,
                        getResources().getStringArray(R.array.filter_time_list))
        );
        mListMain.setItemChecked(getAppPreferences().getInt("filter_time", 4), true);
        mListMain.setSelection(getAppPreferences().getInt("filter_time", 4));
        if (getAppPreferences().getInt("filter_time", 4) == 6) {
            changeDatesRange(mFilterMainView, R.color.text_feed, R.style.NormalStyleDialog);
            mContainerDatesMain.setVisibility(View.VISIBLE);
        }
    }

    private void customFilterEveryFeed(int category, int color, int style) {
        int layout = 0;
        Context mContextTheme = new ContextThemeWrapper(this, style);
        mFilterView = getLayoutInflater().from(mContextTheme).inflate(R.layout.custom_filter_time, null, false);

        mContainerDates = mFilterView.findViewById(R.id.container_dates);
        mList = (ListView) mFilterView.findViewById(R.id.list_filter_time);
        switch (category) {
            case 0:
                layout = R.layout.custom_item_filter_time_feeding;
                break;
            case 1:
                layout = R.layout.custom_item_filter_time_sleeping;
                break;
            case 2:
                layout = R.layout.custom_item_filter_time_diapers;
                break;
            case 3:
                layout = R.layout.custom_item_filter_time_sickness;
                break;
            case 4:
                layout = R.layout.custom_item_filter_time_temperature;
                break;
            case 5:
                layout = R.layout.custom_item_filter_time_medicines;
                break;
            case 6:
                layout = R.layout.custom_item_filter_time_growth;
                break;
            case 7:
                layout = R.layout.custom_item_filter_time_vaccine;
                break;
            case 8:
                layout = R.layout.custom_item_filter_time_milestone;
                break;
            case 9:
                layout = R.layout.custom_item_filter_time_teeth;
                break;
            case 10:
                layout = R.layout.custom_item_filter_time_others;
                break;
        }
        mList.setAdapter(new ArrayAdapter<>(this, layout,
                getResources().getStringArray(R.array.filter_time_list)));
        mList.setItemChecked(getAppPreferences().getInt("filter_time", 2), true);
        mList.setSelection(getAppPreferences().getInt("filter_time", 2));
        if (getAppPreferences().getInt("filter_time", 2) == 6) {
            changeDatesRange(mFilterView, color, style);
            mContainerDates.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Here work with the dates in option "between"
     * @param view
     */
    protected void changeDatesRange(View view, int color, final int style) {
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
     * Show the dialog in every edit text in "between" dates"
     * @param style
     * @param edit
     */
    protected void showDateBetweenDialog(int style, final EditText edit) {
        Context mContextTheme = new ContextThemeWrapper(this, style);
        View mCustomDatePicker = getLayoutInflater().from(mContextTheme)
                .inflate(R.layout.custom_date_picker, null, false);

        final DatePicker mDatePicker = (DatePicker) mCustomDatePicker.findViewById(R.id.datePicker);
        mDatePicker.setMaxDate(Calendar.getInstance().getTime().getTime());

        final Calendar mCalendar = Calendar.getInstance();

        AlertDialog.Builder mDialog = new AlertDialog.Builder(this, style);
        mDialog.setView(mCustomDatePicker);
        mDialog.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCalendar.set(mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDayOfMonth(), mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE));
                edit.setText(new SimpleDateFormat(getString(R.string.format_pretty_time), new Locale(mLocale[getAppPreferences().getInt("language", 0)])).format(mCalendar.getTime()));
            }
        }).setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        mDialog.create();
        mDialog.show();
    }


    /**
     * Method for get list general dots filter by date range
     * @param position
     * @return
     */
    protected final List<DotCategory> filterListByTime(int position) {
        List<DotCategory> newFilter = new ArrayList<>();
        Calendar mToday = Calendar.getInstance();
        switch (position) {
            case 0:  // 7 days
                Calendar m7daysAgo = Calendar.getInstance();
                m7daysAgo.add(Calendar.DAY_OF_MONTH, -7);
                newFilter.addAll(filterByRangeDate(mToday, m7daysAgo, getDots()));
                break;
            case 1: // 14 days
                Calendar m14daysAgo = Calendar.getInstance();
                m14daysAgo.add(Calendar.DAY_OF_MONTH, -14);
                newFilter.addAll(filterByRangeDate(mToday, m14daysAgo, getDots()));
                break;
            case 2: // 30 days
                Calendar m30daysAgo = Calendar.getInstance();
                m30daysAgo.add(Calendar.DAY_OF_MONTH, -30);
                newFilter.addAll(filterByRangeDate(mToday, m30daysAgo, getDots()));
                break;
            case 3: // 2 months
                Calendar m2MonthsAgo = Calendar.getInstance();
                m2MonthsAgo.add(Calendar.MONTH, -2);
                newFilter.addAll(filterByRangeDate(mToday, m2MonthsAgo, getDots()));
                break;
            case 4: // 6 months
                Calendar m6MonthsAgo = Calendar.getInstance();
                m6MonthsAgo.add(Calendar.MONTH, -6);
                newFilter.addAll(filterByRangeDate(mToday, m6MonthsAgo, getDots()));
                break;
            case 5: // Since birth
                newFilter.addAll(getDots());
                break;
        }

        return newFilter;
    }

    /**
     * Method for filter list dots by date range in main
     * @param dates
     * @return
     */
    protected final List<DotCategory> filterListBetweenDates(List<Calendar> dates) {
        List<DotCategory> newFilter = new ArrayList<>();
        newFilter.addAll(filterByRangeDate(dates.get(0), dates.get(1), getDots()));
        return newFilter;
    }

    /**
     * Method for filter list dots by date range by category
     * @param dates
     * @param category
     * @return
     */
    protected final List<DotCategory> filterListBetweenDates(List<Calendar> dates, int category) {
        List<DotCategory> newFilter = new ArrayList<>();
        newFilter.addAll(filterByRangeDate(dates.get(0), dates.get(1), getDotsByCategory(category)));
        return newFilter;
    }

    /**
     * Dialog for add a custom view static in every feed
     * @param title
     * @param layout
     * @param style
     */
    protected void showSimpleDialog(int title, int layout, int style) {
        AlertDialog.Builder mDialog = new AlertDialog.Builder(this, style);
        mDialog.setTitle(title);

        mDialog.setView(layout);
        mDialog.setNegativeButton(R.string.button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        mDialog.setCancelable(false);
        mDialog.create();
        mDialog.show();
    }


    /**
     * Transform bytes to bitmap for image
     * @param image
     * @return
     */
    protected Bitmap getImageFromBytes(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    /**
     * Transform Bitmap to bytes
     * @param image
     * @return
     */
    public byte[] getBytesFromBitmap(Bitmap image) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Reset filter date
        getEditorPreferences().putInt("filter_time", 2);
    }

    /**
     * Method for we can show the list inside ScrollView
     * @param listView
     */
    public void setListHeightInsideScroll(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup) {
                listItem.setLayoutParams(new ViewGroup.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT));
            }
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    /**
     * Method for change language by default when init application
     */
    private void setLocale() {
        Locale myLocale = new Locale(mLocale[getAppPreferences().getInt("language", 0)]);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    /**----------------------------------------- SERVICES -------------------------------------------------*/

    class CategoriesUnlockedSync extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();

            UserServices service = retrofit.create(UserServices.class);

            Call<UnlockedCategories> call = service.getUnlockedCategories(params[0]);

            Response<UnlockedCategories> response = null;
            try {
                response = call.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            UnlockedCategories categories =  response.body();

            String num = "Categories: ";

            for (Integer i : categories.getUnlockedCategories()) {
                num += i + ", ";
            }

            return num;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
        }
    }

    class SyncChild extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();

            String username = "limun.zut@icloud.com";
            List<Child> children = new ArrayList<>();
            Child child = new Child("21EC2020-3AEA-1069-A2DD-08002B30309D",
                    "Marko",
                    "2014-01-01T00:00:00Z", 0, null);

            children.add(child);

            SyncChildren sync = new SyncChildren(username, children);

            UserServices service = retrofit.create(UserServices.class);

            Call<Object> call = service.syncChildren("");

            try {
                return call.execute().isSuccess();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
            if (s)
                Toast.makeText(getApplicationContext(), "ok 200", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
        }
    }

    class SyncUserData extends AsyncTask<Void, Void, List<EntryChildren>> {

        @Override
        protected List<EntryChildren> doInBackground(Void... params) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();

            String userId = "21EC2020-3AEA-1069-A2DD-08002B30309D";
            String dateModified = "2014-01-01T00:00:00Z";

            UserServices service = retrofit.create(UserServices.class);

            Call<List<EntryChildren>> call = service.syncUser(userId, dateModified);

            Response<List<EntryChildren>> response = null;
            try {
                response = call.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response.body();
        }

        @Override
        protected void onPostExecute(List<EntryChildren> s) {
            super.onPostExecute(s);
            Toast.makeText(getApplicationContext(), "Name: " + s.get(0).getName(), Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), "DoB: " + s.get(0).getBirthDate(), Toast.LENGTH_SHORT).show();
            //Toast.makeText(getApplicationContext(), "Category: " + s.get(0).getEntries().get(0).getCategoryId(), Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), "Value:" + s.get(0).getEntries().get(0).getEntryData().get(0).getValue(), Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), "Note:" + s.get(0).getEntries().get(0).getNote(), Toast.LENGTH_SHORT).show();
        }
    }

    class SyncEntries extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();

            String username = "limun.zut@icloud.com";
            List<Child> children = new ArrayList<>();
            Child child = new Child("21EC2020-3AEA-1069-A2DD-08002B30309D",
                    "Marko",
                    "2014-01-01T00:00:00Z", 0, null);

            children.add(child);

            List<EntryData> entriesData = new ArrayList<>();
            EntryData entryData = new EntryData(0,3,0,"Some Text", false);
            entriesData.add(entryData);

            List<String> pictures = new ArrayList<>();
            pictures.add("21EC2020-3AEA-1069-A2DD-08002B30308F");


            List<Entries> entries = new ArrayList<>();
            Entries entry = new Entries("21EC2020-3AEA-1069-A2DD-08002B30309D", 2, "2014-01-01T00:00:00Z",
                    entriesData,pictures, "Text for entry note");
            entries.add(entry);

            SyncEntriesByChildren sync = new SyncEntriesByChildren(/*username, children, entries*/);

            UserServices service = retrofit.create(UserServices.class);

            Call<Object> call = service.syncEntries("");

            try {
                return call.execute().isSuccess();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
            if (s)
                Toast.makeText(getApplicationContext(), "ok 200", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
        }
    }



    class SyncDeleteEntries extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();

            List<String> entryGuids = new ArrayList<>();
            entryGuids.add("21EC2020-3AEA-1069-A2DD-08002B30309F");
            entryGuids.add("21EC2020-3AEA-1069-A2DD-08002B30309A");
            entryGuids.add("21EC2020-3AEA-1069-A2DD-08002B30309D");

            UserServices service = retrofit.create(UserServices.class);

            Call<Object> call = service.syncDeleteEntries(entryGuids);

            try {
                return call.execute().isSuccess();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
            if (s)
                Toast.makeText(getApplicationContext(), "ok 200", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
        }
    }

    class UploadImage extends AsyncTask<Void, Void, Boolean> {

        private final MediaType JPEG = MediaType.parse("image/jpeg; charset=utf-8");

        @Override
        protected Boolean doInBackground(Void... params) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();

            UserServices service = retrofit.create(UserServices.class);

            RequestBody id = RequestBody.create(JPEG, "21EC2020-3AEA-1069-A2DD-08002B30308F");
            RequestBody image = RequestBody.create(JPEG, "/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0a HBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIy MjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAABAAEDASIA AhEBAxEB/8QAFQABAQAAAAAAAAAAAAAAAAAAAAf/xAAUEAEAAAAAAAAAAAAAAAAAAAAA/8QAFAEB AAAAAAAAAAAAAAAAAAAAAP/EABQRAQAAAAAAAAAAAAAAAAAAAAD/2gAMAwEAAhEDEQA/AL+AD//Z");

            Call<Object> call = service.uploadImage(id, image);

            try {
                return call.execute().isSuccess();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
            if (s)
                Toast.makeText(getApplicationContext(), "ok 200", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
        }
    }

    class DownloadImage extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            try {
                URL url = new URL(API + "user/sync/image?id=" + "1");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(3000);
                con.setReadTimeout(3000);
                con.connect();
                Log.i("TAG", "URL" + con.getHeaderField("Location"));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String headers) {
            super.onPostExecute(headers);
        }
    }
}


