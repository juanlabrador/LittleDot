package com.littledot.mystxx.littledot;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.dropin.BraintreePaymentActivity;
import com.littledot.menu.Menu;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

import org.joda.time.LocalDate;
import org.joda.time.Period;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import adapters.ChildrenListAdapter;
import adapters.OptionsListAdapter;
import de.hdodenhof.circleimageview.CircleImageView;
import domains.Child;
import graph.BubbleChartSampleActivity;
import graph.GrowthGraphsActivity;
import graph.MilestoneGraphsActivity;


/**
 * Created by juanlabrador on 18/08/15.
 */
public class MainFeedActivity extends ParentFeedActivity implements View.OnClickListener {

    private static String TAG = "MainFeedActivity";
    private static int PAYMENT = 100;

    // Menu options
    private OptionsListAdapter mAdapterOptions;
    private ListView mListOptions;
    private ScrollView mScroll;
    private View mBottomLine;
    private SlidingUpPanelLayout mLayoutPanel;
    private ImageView mExclamationMark;

    //Menu children list
    private ChildrenListAdapter mChildAdapter;
    private ListView mChildrenList;
    private View mLineDivider;
    private List<Child> mChildren;

    // Horizontal menu
    private Menu mHorizontalMenu;

    private View mMainMenu;
    private CircleImageView mImageProfile;
    private TextView mBabyName;
    private TextView mBabyAge;

    private SimpleDateFormat mFormat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeNotificationBarColor(R.color.text_feed);

        // If there are many children
        // It show a child for default, this child has been select before
        if (areThereChildren()) {
            init();
            updateDataMain(null);
            initMenu();
            createListOptions();
            createListChildren();
            updateChildrenMain();
        } else { // If there isn't a child
            setContentView(R.layout.custom_more_options);
            startActivity(new Intent(this, OptionChildrenAddEditActivity.class));
            overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
            createListOptions();
        }
    }

    /**
     * Initialize our main screen when there are children saved
     * */
    private void init() {
        createScreen(R.layout.activity_main_feed, -1, R.id.open_close_menu);
        mMainMenu = findViewById(R.id.main_menu);
        mImageProfile = (CircleImageView) findViewById(R.id.main_photo_baby);
        mBabyName = (TextView) findViewById(R.id.main_name_baby);
        mBabyAge = (TextView) findViewById(R.id.main_age_baby);
        mExclamationMark = (ImageView) findViewById(R.id.exclamationMark);
        mExclamationMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Please, Login with your Google account in setting.", Toast.LENGTH_LONG).show();
            }
        });
        createHorizontalMenu();

        mScroll = (ScrollView) findViewById(R.id.scroll_options);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int mHeightScreen = size.y;  // Height of screen phone
        int mHeightPanel = 250;      // Height when panel is COLLAPSED

        mLayoutPanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        mLayoutPanel.setPanelState(PanelState.EXPANDED);
        mLayoutPanel.setPanelHeight(mHeightPanel);
        mLayoutPanel.setDragView(R.id.container_top_main);

        SlidingUpPanelLayout.LayoutParams mParams = (SlidingUpPanelLayout.LayoutParams) mScroll.getLayoutParams();
        mParams.height = mHeightScreen - mHeightPanel;  // Subtract Height screen with Height Panel
        mScroll.setLayoutParams(mParams);
        mBottomLine = findViewById(R.id.bottom);

        mLayoutPanel.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                //Log.i(TAG, "onPanelSlide, offset " + slideOffset);
                //Focus in ListOptions
                if (slideOffset <= 0.90)
                    mScroll.scrollTo(0, mBottomLine.getBottom());
            }

            @Override
            public void onPanelExpanded(View panel) {
                Log.i(TAG, "onPanelExpanded");

            }

            @Override
            public void onPanelCollapsed(View panel) {
                Log.i(TAG, "onPanelCollapsed");

            }

            @Override
            public void onPanelAnchored(View panel) {
                Log.i(TAG, "onPanelAnchored");
            }

            @Override
            public void onPanelHidden(View panel) {
                Log.i(TAG, "onPanelHidden");
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mLayoutPanel != null) {
            mLayoutPanel.setPanelState(PanelState.EXPANDED);
            hideMenu();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /**
         *
         * If already there is a child or more
         * It update the screen
         *
         *
         * If the user deleted all children, show options only
         *
         * */
        if (areThereChildren()) {
            // If the view exist! Not create again, just refresh
            if (mLayoutPanel != null) {
                updateDataMain(null);
                updateChildrenMain();

                 // If user change the language, refresh screen
                if (getAppPreferences().getBoolean("changed_language", false)) {
                    init();
                    updateDataMain(null);
                    initMenu();
                    createListOptions();
                    createListChildren();
                    updateChildrenMain();
                    getEditorPreferences().putBoolean("changed_language", false);
                    getEditorPreferences().commit();
                }
            } else {
                // If not exist! Create new view
                init();
                updateDataMain(null);
                initMenu();
                createListOptions();
                createListChildren();
                updateChildrenMain();
            }
        } else {
            //If I deleted all children
            //testException(-5);
            setContentView(R.layout.custom_more_options);
            createListOptions();
        }

        if (mExclamationMark != null) {
            if (!getAppPreferences().getString("email_user", "").equals("")) {
                mExclamationMark.setVisibility(View.GONE);
            } else {
                mExclamationMark.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mLayoutPanel != null && (mLayoutPanel.getPanelState() == PanelState.COLLAPSED)) {
            mLayoutPanel.setPanelState(PanelState.EXPANDED);
        } else {
            super.onBackPressed();
        }
    }

    /**-----------------------------------------------------------------------------------------------------------------------*/
    /**--------------------------------------Options Menu-------------------------------------------------------*/
    /**
     * Create list option hide below main feed
     * */
    private void createListOptions() {
        int [] mIcons = new int[] {
                R.mipmap.icn_children,
                R.mipmap.icn_settings,
                R.mipmap.recommended
        };

        String [] mContents = getResources().getStringArray(R.array.options_list);

        mListOptions = (ListView) findViewById(R.id.list_options);
        mAdapterOptions = new OptionsListAdapter(this, mIcons, mContents);
        mListOptions.setAdapter(mAdapterOptions);

        mListOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(getApplicationContext(), OptionListChildrenActivity.class));
                        overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                        break;
                    case 1:
                        startActivity(new Intent(getApplicationContext(), OptionSettingActivity.class));
                        overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                        break;
                    case 2:
                        sharedAppFriends();
                        break;
                }
            }
        });

        setListHeightInsideScroll(mListOptions);

    }

    /**
     * Intent for shared app via SMS, Email, WhatsApp, etc etc
     */
    private void sharedAppFriends() {
        Intent mShared = new Intent(Intent.ACTION_SEND);
        mShared.setType("text/plain");
        StringBuilder mText = new StringBuilder(getString(R.string.recommend_app));
        mText.append("\n");
        mText.append("http://littledotapp.com");      // Need the google play link here
        mShared.putExtra(Intent.EXTRA_TEXT, mText.toString());
        mShared.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(mShared, String.format(getString(R.string.shared_message), getString(R.string.app_name))));
    }

    /**----------------------------------------------------------------------------------------------------------------*/
    /**---------------------------------------------Children menu----------------------------------------------------*/

    /**
     * Create list children when there are more of one
     * */
    private void createListChildren() {
        mChildrenList = (ListView) findViewById(R.id.list_children);
        mLineDivider = findViewById(R.id.divider_list);
        if (isThereChild()) {
            mChildAdapter = new ChildrenListAdapter(this, getChildren());
            // Show List children
            mChildrenList.setVisibility(View.VISIBLE);
            mLineDivider.setVisibility(View.VISIBLE);
            mChildrenList.setAdapter(mChildAdapter);
            mChildrenList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.i(TAG, "Click in list child!");
                    getEditorPreferences().putString("child_selected", mChildren.get(position).getID());
                    getEditorPreferences().commit();
                    updateChildrenMain();
                    mLayoutPanel.setPanelState(PanelState.EXPANDED);
                }
            });
        } else {  // Hide List children
            mChildrenList.setVisibility(View.GONE);
            mLineDivider.setVisibility(View.GONE);
        }
    }

    /**
     * Here select the child clicked and this child is not showing in list
     * */
    private void updateChildrenMain() {
        if (areThereChildren()) {
            if (getChild().getImage() != null) mImageProfile.setImageBitmap(getImageFromBytes(getChild().getImage()));
            else mImageProfile.setImageResource(R.mipmap.icn_children_default);

            mBabyName.setText(getChild().getName());
            mBabyAge.setText(calculateAge(getChild().getDob()));
            updateContentEmptyList(getChild().getName());

            updateDataMain(null);

            // Refresh the list
            mChildren = getChildrenWithoutSelected();
            mChildAdapter = new ChildrenListAdapter(this, mChildren);
            mChildrenList.setVisibility(View.VISIBLE);
            mLineDivider.setVisibility(View.VISIBLE);
            mChildrenList.setAdapter(mChildAdapter);

            setListHeightInsideScroll(mChildrenList);
            // Hide the list child
            if (mChildren.size() == 0) {
                mChildrenList.setVisibility(View.GONE);
                mLineDivider.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Calculate age of child for show in main
     * @param date
     * @return
     */
    private String calculateAge(String date) {
        Calendar mDate = Calendar.getInstance();
        mFormat = new SimpleDateFormat(getString(R.string.format_date_service));
        try {
            mDate.setTime(mFormat.parse(date));
            mDate.add(Calendar.MONTH, 1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        LocalDate mBirth;

        /*
         *
         * If Month between January and November
         * because LocalDate, the months are from 1 to 12.
         * Calendar, the months are from 0 to 11.
         *
         * */
        if (mDate.get(Calendar.MONTH) != 0)
            mBirth = new LocalDate(mDate.get(Calendar.YEAR), mDate.get(Calendar.MONTH), mDate.get(Calendar.DAY_OF_MONTH));
        else {
            mDate.add(Calendar.YEAR, -1);
            mBirth = new LocalDate(mDate.get(Calendar.YEAR), 12, mDate.get(Calendar.DAY_OF_MONTH));
        }

        LocalDate mNow = new LocalDate();
        Period mPeriod = new Period(mBirth, mNow);

        if (mPeriod.getYears() >= 1)
            // Example: 2 years 9m 5d
            return mPeriod.getYears() + " " +
                    getString(R.string.children_age_year) + " " +
                    mPeriod.getMonths() +
                    getString(R.string.children_age_month) + " " +
                    mPeriod.getDays() +
                    getString(R.string.children_age_days);
        else
            // Example: 6 months 7d
            return  mPeriod.getMonths() + " " +
                    getString(R.string.children_age_months) + " " +
                    mPeriod.getDays() +
                    getString(R.string.children_age_days);
    }

    /**---------------------------------------------------------------------------------------------------------*/
    /**----------------------------------------------Horizontal Menu---------------------------------------------*/

    /**
     * For create horizontal menu
     * For add a new entry directly in main feed
     * */
    private void createHorizontalMenu() {
        mHorizontalMenu = (Menu) findViewById(R.id.main_add_entry);
        final int[] mIcons = {
                R.mipmap.icn_feeding, R.mipmap.icn_sleeping,
                R.mipmap.icn_diapers, R.mipmap.icn_sickness,
                R.mipmap.icn_temperature, R.mipmap.icn_medicines,
                R.mipmap.icn_growth, R.mipmap.icn_vaccines,
                R.mipmap.icn_milestones, R.mipmap.icn_teeth,
                R.mipmap.icn_other
        };

        for (int i = 0; i < mIcons.length; i++) {
            ImageView mItemIcon = new ImageView(this);
            mItemIcon.setImageResource(mIcons[i]);

            final int position = i;
            mHorizontalMenu.addItem(mItemIcon, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mEntryOldDate;
                    if (mHorizontalMenu.getTag() != null) {
                        mEntryOldDate = (String) mHorizontalMenu.getTag();
                        mHorizontalMenu.setTag(null);
                    } else {
                        mEntryOldDate = new SimpleDateFormat(getString(R.string.format_date_service)).format(Calendar.getInstance().getTime());
                    }

                    switch (position) {
                        case 0:
                            startActivity(new Intent(getApplicationContext(), FeedingAddEntryActivity.class).putExtra("entry_date", mEntryOldDate));
                            overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                            break;
                        case 1:
                            startActivity(new Intent(getApplicationContext(), SleepingAddEntryActivity.class).putExtra("entry_date", mEntryOldDate));
                            overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                            break;
                        case 2:
                            startActivity(new Intent(getApplicationContext(), DiapersAddEntryActivity.class).putExtra("entry_date", mEntryOldDate));
                            overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                            break;
                        case 3:
                            startActivity(new Intent(getApplicationContext(), SicknessAddEntryActivity.class).putExtra("entry_date", mEntryOldDate));
                            overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                            break;
                        case 4:
                            startActivity(new Intent(getApplicationContext(), TemperatureAddEntryActivity.class).putExtra("entry_date", mEntryOldDate));
                            overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                            break;
                        case 5:
                            startActivity(new Intent(getApplicationContext(), MedicineAddEntryActivity.class).putExtra("entry_date", mEntryOldDate));
                            overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                            break;
                        case 6:
                            startActivity(new Intent(getApplicationContext(), GrowthAddEntryActivity.class).putExtra("entry_date", mEntryOldDate));
                            overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                            break;
                        case 7:
                            startActivity(new Intent(getApplicationContext(), VaccineAddEntryActivity.class).putExtra("entry_date", mEntryOldDate));
                            overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                            break;
                        case 8:
                            startActivity(new Intent(getApplicationContext(), MilestoneAddEntryActivity.class).putExtra("entry_date", mEntryOldDate));
                            overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                            break;
                        case 9:
                            startActivity(new Intent(getApplicationContext(), TeethAddEntryActivity.class).putExtra("entry_date", mEntryOldDate));
                            overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                            break;
                        case 10:
                            startActivity(new Intent(getApplicationContext(), OthersAddEntryActivity.class).putExtra("entry_date", mEntryOldDate));
                            overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                            break;
                    }
                }
            });
        }
    }

    public Menu getHorizontalMenu() {
        return mHorizontalMenu;
    }

    /***-----------------------------------------------------------------------------------------------------/
    /**---------------------------------------Main Menu--------------------------------------------*/

    private void initMenu() {
        // Main Menu
        findViewById(R.id.home_feeding_btn).setOnClickListener(this);
        findViewById(R.id.home_sleeping_btn).setOnClickListener(this);
        findViewById(R.id.home_diapers_btn).setOnClickListener(this);
        findViewById(R.id.home_sickness_btn).setOnClickListener(this);
        findViewById(R.id.home_temperature_btn).setOnClickListener(this);
        findViewById(R.id.home_medicines_btn).setOnClickListener(this);
        findViewById(R.id.home_growth_btn).setOnClickListener(this);
        findViewById(R.id.home_growth_btn_lock).setOnClickListener(this);
        findViewById(R.id.home_vaccines_btn).setOnClickListener(this);
        findViewById(R.id.home_milestones_btn).setOnClickListener(this);
        findViewById(R.id.home_teeth_btn).setOnClickListener(this);
        findViewById(R.id.home_others_btn).setOnClickListener(this);
        findViewById(R.id.home_doctors_btn).setOnClickListener(this);
    }

    private void hideMenu() {
        //overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
        mMainMenu.setVisibility(View.GONE);
        getButtonHeader().setImageResource(R.mipmap.icn_feed_filter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.choose_time_filter:
                showTimeListDialog();
                break;
            case R.id.open_close_menu:
                // When I clicked in button menu
                if (mMainMenu.getVisibility() == View.VISIBLE) {
                    mMainMenu.setVisibility(View.GONE);
                    getButtonHeader().setImageResource(R.mipmap.icn_feed_filter);
                } else {
                    mMainMenu.setVisibility(View.VISIBLE);
                    getButtonHeader().setImageResource(R.mipmap.icn_feed_filter_close);
                }
                // Close the sliding view if is open!
                if (mLayoutPanel.getPanelState() == PanelState.COLLAPSED)
                    mLayoutPanel.setPanelState(PanelState.EXPANDED);

                break;
            case R.id.home_feeding_btn:
                startActivity(new Intent(this, FeedingFeedActivity.class));
                overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                break;
            case R.id.home_sleeping_btn:
                startActivity(new Intent(this, SleepingFeedActivity.class));
                overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                break;
            case R.id.home_diapers_btn:
                startActivity(new Intent(this, DiapersFeedActivity.class));
                overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                break;
            case R.id.home_sickness_btn:
                startActivity(new Intent(this, SicknessFeedActivity.class));
                overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                break;
            case R.id.home_temperature_btn:
                startActivity(new Intent(this, TemperatureFeedActivity.class));
                overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                break;
            case R.id.home_medicines_btn:
                startActivity(new Intent(this, MedicineFeedActivity.class));
                overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                break;
            case R.id.home_growth_btn:
                startActivity(new Intent(this, GrowthFeedActivity.class));
                overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                break;
            case R.id.home_vaccines_btn:
                startActivity(new Intent(this, VaccineFeedActivity.class));
                overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                break;
            case R.id.home_milestones_btn:
                startActivity(new Intent(this, MilestoneFeedActivity.class));
                overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                break;
            case R.id.home_teeth_btn:
                startActivity(new Intent(this, TeethFeedActivity.class));
                overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                break;
            case R.id.home_doctors_btn:
                startActivity(new Intent(this, DoctorDetailActivity.class));
                overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                break;
            case R.id.home_others_btn:
                startActivity(new Intent(this, OthersFeedActivity.class));
                overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                break;
            case R.id.home_growth_btn_lock:
                Intent intent = new Intent(this, BraintreePaymentActivity.class)
                    .putExtra(BraintreePaymentActivity.EXTRA_CLIENT_TOKEN, "eyJ2ZXJzaW9uIjoyLCJhdXRob3JpemF0aW9uRmluZ2VycHJpbnQiOiJjMmQ0OWI4N2RjOGY2MzllOTcyMTY1OGM4NzZkMTAzNDkwY2U5OTgzZmYyZGU3OTI5ZTNiNWQzZWYwNWYzN2E3fGNyZWF0ZWRfYXQ9MjAxNS0xMC0yNlQxMTowMjo1Mi4wODc3MTQ2OTErMDAwMFx1MDAyNm1lcmNoYW50X2lkPTM0OHBrOWNnZjNiZ3l3MmJcdTAwMjZwdWJsaWNfa2V5PTJuMjQ3ZHY4OWJxOXZtcHIiLCJjb25maWdVcmwiOiJodHRwczovL2FwaS5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tOjQ0My9tZXJjaGFudHMvMzQ4cGs5Y2dmM2JneXcyYi9jbGllbnRfYXBpL3YxL2NvbmZpZ3VyYXRpb24iLCJjaGFsbGVuZ2VzIjpbXSwiZW52aXJvbm1lbnQiOiJzYW5kYm94IiwiY2xpZW50QXBpVXJsIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5icmFpbnRyZWVnYXRld2F5LmNvbTo0NDMvbWVyY2hhbnRzLzM0OHBrOWNnZjNiZ3l3MmIvY2xpZW50X2FwaSIsImFzc2V0c1VybCI6Imh0dHBzOi8vYXNzZXRzLmJyYWludHJlZWdhdGV3YXkuY29tIiwiYXV0aFVybCI6Imh0dHBzOi8vYXV0aC52ZW5tby5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tIiwiYW5hbHl0aWNzIjp7InVybCI6Imh0dHBzOi8vY2xpZW50LWFuYWx5dGljcy5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tIn0sInRocmVlRFNlY3VyZUVuYWJsZWQiOnRydWUsInRocmVlRFNlY3VyZSI6eyJsb29rdXBVcmwiOiJodHRwczovL2FwaS5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tOjQ0My9tZXJjaGFudHMvMzQ4cGs5Y2dmM2JneXcyYi90aHJlZV9kX3NlY3VyZS9sb29rdXAifSwicGF5cGFsRW5hYmxlZCI6dHJ1ZSwicGF5cGFsIjp7ImRpc3BsYXlOYW1lIjoiQWNtZSBXaWRnZXRzLCBMdGQuIChTYW5kYm94KSIsImNsaWVudElkIjpudWxsLCJwcml2YWN5VXJsIjoiaHR0cDovL2V4YW1wbGUuY29tL3BwIiwidXNlckFncmVlbWVudFVybCI6Imh0dHA6Ly9leGFtcGxlLmNvbS90b3MiLCJiYXNlVXJsIjoiaHR0cHM6Ly9hc3NldHMuYnJhaW50cmVlZ2F0ZXdheS5jb20iLCJhc3NldHNVcmwiOiJodHRwczovL2NoZWNrb3V0LnBheXBhbC5jb20iLCJkaXJlY3RCYXNlVXJsIjpudWxsLCJhbGxvd0h0dHAiOnRydWUsImVudmlyb25tZW50Tm9OZXR3b3JrIjp0cnVlLCJlbnZpcm9ubWVudCI6Im9mZmxpbmUiLCJ1bnZldHRlZE1lcmNoYW50IjpmYWxzZSwiYnJhaW50cmVlQ2xpZW50SWQiOiJtYXN0ZXJjbGllbnQzIiwiYmlsbGluZ0FncmVlbWVudHNFbmFibGVkIjp0cnVlLCJtZXJjaGFudEFjY291bnRJZCI6ImFjbWV3aWRnZXRzbHRkc2FuZGJveCIsImN1cnJlbmN5SXNvQ29kZSI6IlVTRCJ9LCJjb2luYmFzZUVuYWJsZWQiOmZhbHNlLCJtZXJjaGFudElkIjoiMzQ4cGs5Y2dmM2JneXcyYiIsInZlbm1vIjoib2ZmIn0=");
                startActivityForResult(intent, PAYMENT);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PAYMENT) {
            if (resultCode == BraintreePaymentActivity.RESULT_OK) {
                String nonce = data.getStringExtra(
                        BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE
                );
                // Send the nonce to your server.
            }
        }
    }
}
