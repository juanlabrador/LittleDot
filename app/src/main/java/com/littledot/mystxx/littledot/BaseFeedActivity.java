package com.littledot.mystxx.littledot;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.littledot.dtpicker.SlideDateTimeListener;
import com.littledot.dtpicker.SlideDateTimePicker;
import com.ocpsoft.pretty.time.PrettyTime;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import domains.Image;

/**
 * Created by juanlabrador on 18/08/15.
 */
public class BaseFeedActivity extends ParentFeedActivity implements CompoundButton.OnCheckedChangeListener {

    private static final int TAKE_CAMERA = 1;
    private static final int TAKE_GALLERY = 2;
    private static final int CROP_IMAGE = 3;
    private static String TAG = "BaseFeedActivity";

    // All Feed
    private ImageView mFeedIcon;
    private Button mFeedChooseTime;
    private TextView mFeedNameBaby;
    private TextView mFeedTitle;
    private ImageView mFeedAddItem;
    private View mContainerMenu;
    private TextView mTextMenu1;
    private TextView mTextMenu2;
    private int mStyle;
    private int mColor;
    private int mCategory;
    private String mDateTime;
    private View mViewAddImage;
    private ImageView mAttachButton;
    private View mTranslucent;
    private String mBackText;
    private ImageView mAddImage;
    private LinearLayout mGallery;

    // Choose Time Add Entry
    private Button mChooseTimeButton;

    // Doctor View
    private ImageView mDoctorCloseButton;
    private TextView mDoctorNameBaby;
    private TextView mDoctorName;
    private TextView mDoctorHoursWork;
    private TextView mDoctorSendEmail;
    private TextView mDoctorCall1;
    private TextView mDoctorCall2;
    public TextView mDoctorAddress;
    private Button mDoctorEdit;
    private View mDoctorContainerName;
    private View mDoctorContainerHourWork;

    // Edit Doctor
    private EditText mName;
    private EditText mHourWork;
    private EditText mPhone1;
    private EditText mPhone2;
    private EditText mEmail;
    private EditText mAddress;

    // Array Photos
    private List<Bitmap> mPhotos;
    private TextView mTagPhoto;
    private Uri mUriPhoto;
    private Bitmap mBitmapPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        childSelected();
    }

    /**
     * Here create the feed view
     * @param category
     * @param empty_list_content
     * @param header
     */
    protected void createFeedView(int category, int empty_list_content, int header) {
        createScreen(R.layout.activity_general_feed, header, R.id.close_button);
        mFeedIcon = (ImageView) findViewById(R.id.feed_icon);
        mFeedTitle = (TextView) findViewById(R.id.feed_title);
        mFeedNameBaby = (TextView) findViewById(R.id.feed_name_baby);
        mFeedAddItem = (ImageView) findViewById(R.id.feed_add_item);
        mFeedAddItem.setOnClickListener(this);
        mFeedNameBaby.setText(getChild().getName());
        updateContentEmptyList(empty_list_content);
        mCategory = category;
    }

    /**
     * Here create the feed Temperature view and Vaccine
     * @param category
     * @param empty_list_content
     * @param menu
     * @param background_color
     */
    protected void createCustomFeed(int category, int empty_list_content, String[] menu, int background_color) {
        createFeedView(empty_list_content, empty_list_content,  R.layout.custom_header_list_menu);
        mContainerMenu = getHeaderList().findViewById(R.id.container_menu);
        mContainerMenu.setBackgroundColor(getResources().getColor(background_color));
        mTextMenu1 = (TextView) getHeaderList().findViewById(R.id.menu_1);
        mTextMenu1.setText(menu[0]);
        mTextMenu1.setOnClickListener(this);
        mTextMenu2 = (TextView) getHeaderList().findViewById(R.id.menu_2);
        mTextMenu2.setText(menu[1]);
        mTextMenu2.setOnClickListener(this);
        mCategory = category;
    }

    /**
     * Here personalize feed view, first called createFeedView method
     * @param icon
     * @param title
     * @param icon_add
     * @param color
     * @param style_dialog
     * @param drawable_tint
     */
    protected void colorFeedView(int icon, int title, int icon_add, int color, int style_dialog, int drawable_tint) {
        mFeedIcon.setImageResource(icon);
        mFeedTitle.setText(title);
        getButtonHeader().setImageResource(R.mipmap.icn_feed_close);
        mFeedAddItem.setImageResource(icon_add);
        getChooseTime().setTextColor(getResources().getColor(color));
        getEmptyChooseTime().setTextColor(getResources().getColor(color));
        getEmptyTitle().setTextColor(getResources().getColor(color));
        mStyle = style_dialog;
        mColor = color;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getChooseTime().setCompoundDrawablesWithIntrinsicBounds(getDrawable(drawable_tint), null, null, null);
            getEmptyChooseTime().setCompoundDrawablesWithIntrinsicBounds(getDrawable(drawable_tint), null, null, null);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_button:
                finish();
                overridePendingTransition(R.anim.anim_nothing, R.anim.anim_out);
                break;
            case R.id.choose_time_filter:
                showTimeListDialog(mStyle, mCategory, mColor);
                break;
            case R.id.call_doctor_1:
                startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:" + mDoctorCall1.getText().toString())));
                overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                break;
            case R.id.call_doctor_2:
                startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:" + mDoctorCall2.getText().toString())));
                overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                break;
            case R.id.send_doctor_email:
                startActivity(new Intent(this, DoctorEmailOptionsActivity.class));
                overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                break;
            case R.id.doctor_edit:
                startActivity(new Intent(this, DoctorEditActivity.class));
                overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                break;
            case R.id.attach_image:
                showAttachImage();
                break;
            case R.id.add_image:
                showDialogPhoto();
                break;
        }
    }

    /**---------------------------------------------------------------------------------------------------*/
    /**---------------------------------All Add Entry Category----------------------------------------------------------------------*

     /**
     * Method for create all Add Entry Screen
     * @param layout
     */
    protected void createEntryView(View layout) {
        mChooseTimeButton = (Button) findViewById(R.id.choose_time_button);
        mChooseTimeButton.setOnClickListener(this);
        mViewAddImage = findViewById(R.id.view_add_image);
        mTranslucent = findViewById(R.id.translucent);
        mAttachButton = (ImageView) findViewById(R.id.attach_image);
        mAttachButton.setOnClickListener(this);
        mAddImage = (ImageView) findViewById(R.id.add_image);
        mAddImage.setOnClickListener(this);
        mGallery = (LinearLayout) findViewById(R.id.gallery_item);
        mTagPhoto = (TextView) findViewById(R.id.att_note);
        LinearLayout mContainer = (LinearLayout) findViewById(R.id.container_view);
        mContainer.addView(layout);

        mPhotos = new ArrayList<>();
    }

    /**
     * Method for indicate the user that the entry has images attachment
     * @param id
     */
    protected void changeStyleAttach(String id) {
        List<Image> images = getImagesDB(id);
        if (images.isEmpty()) {
            mAttachButton.setImageResource(R.mipmap.ic_attachment);
            mAttachButton.setBackgroundResource(R.drawable.style_button_add_entry);
        } else {
            mAttachButton.setImageResource(R.mipmap.ic_attachment_edit);
            mAttachButton.setBackgroundResource(R.drawable.style_button_add_entry_check);
        }
    }

    /**
     * Show view when User has click for add image
     */
    private void showAttachImage() {
        if (mViewAddImage.getVisibility() == View.GONE) {
            Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.anim_in);
            Animation slideUpFooter = AnimationUtils.loadAnimation(this, R.anim.anim_up_view);
            Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            fadeIn.setDuration(150);
            findViewById(R.id.footer).startAnimation(slideUpFooter);
            mTranslucent.startAnimation(fadeIn);
            mViewAddImage.startAnimation(slideUp);
            mTranslucent.setVisibility(View.VISIBLE);
            mViewAddImage.setVisibility(View.VISIBLE);
            mAttachButton.setBackground(getResources().getDrawable(R.drawable.style_button_add_entry_check));
            mAttachButton.setImageResource(R.mipmap.icn_done);
            mBackText = mChooseTimeButton.getText().toString();
            mChooseTimeButton.setText(R.string.att_title);
            mChooseTimeButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } else {
            Animation slideDownFooter = AnimationUtils.loadAnimation(this, R.anim.anim_down_view);
            Animation fadeout = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
            fadeout.setDuration(150);
            Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.anim_out);
            slideDown.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    mAttachButton.setBackground(getResources().getDrawable(R.drawable.style_button_add_entry));
                    mAttachButton.setImageResource(R.mipmap.ic_attachment);
                    mChooseTimeButton.setText(mBackText);
                    mChooseTimeButton.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icn_add_time, 0, 0, 0);
                    findViewById(R.id.footer).clearAnimation();
                    mViewAddImage.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            findViewById(R.id.footer).startAnimation(slideDownFooter);
            mViewAddImage.startAnimation(slideDown);
            mTranslucent.startAnimation(fadeout);
            mTranslucent.setVisibility(View.GONE);
        }
    }

    /**
     * Add image to view
     * @param photo
     * @param image
     */
    public void addGallery(Bitmap photo, Image image) {
        final View mItemImage = getLayoutInflater().inflate(R.layout.custom_item_add_image, null);
        ImageView mPhoto = (ImageView) mItemImage.findViewById(R.id.photo);
        final ImageView mDeletePhoto = (ImageView) mItemImage.findViewById(R.id.delete_photo);
        mDeletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhotos.remove(mItemImage.getTag());
                ((LinearLayout) mItemImage.getParent()).removeView(mItemImage);
                if (mDeletePhoto.getTag() != null) {
                    deleteImage((Image) mDeletePhoto.getTag());
                }
            }
        });

        // TAG for new photos
        if (image == null) {
            mPhotos.add(photo);
            mItemImage.setTag(photo);
        } else { // TAG for existing photos
            mDeletePhoto.setTag(image);
        }

        mPhoto.setImageBitmap(photo);
        mGallery.addView(mItemImage);
    }

    /**
     * Show dialog for take photo or search gallery
     */
    private void showDialogPhoto() {
        final String[] options = new String[]{
                getString(R.string.option_take_photo),
                getString(R.string.option_take_from_gallery)};

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // Ensure that there's a camera activity to handle the intent
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            // Create the File where the photo should go
                            File photoFile = createImageFile();

                            // Continue only if the File was successfully created
                            if (photoFile != null) {
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                        Uri.fromFile(photoFile));
                                mUriPhoto = Uri.fromFile(photoFile);
                                startActivityForResult(takePictureIntent, TAKE_CAMERA);
                            }
                        }
                        break;
                    case 1:
                        Intent takeGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        takeGallery.setType("image/*");
                        startActivityForResult(takeGallery, TAKE_GALLERY);
                        break;
                }
                dialog.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_CAMERA:
                    doCrop();
                    galleryAddPic();
                    break;
                case TAKE_GALLERY:
                    if (data != null) {
                        mUriPhoto = data.getData();
                        doCrop();
                    }
                    break;
                case CROP_IMAGE:
                    if (data != null) {
                        mBitmapPhoto = data.getExtras().getParcelable("data");
                        addGallery(mBitmapPhoto, null);
                    }
                    break;
            }
        }
    }

    /**
     * For crop to image later of select or take with camera
     */
    private void doCrop() {
        startActivityForResult(new Intent("com.android.camera.action.CROP")
                .setDataAndType(mUriPhoto, "image/*")
                .putExtra("crop", "true")
                .putExtra("outputX", 200)
                .putExtra("outputY", 200)
                .putExtra("aspectX", 1)
                .putExtra("aspectY", 1)
                .putExtra("scale", true)
                .putExtra("return-data", true), CROP_IMAGE);
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mUriPhoto.getPath());
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


    /**
     * Create name to file to Photo
     * @return
     */
    private File createImageFile() {
        // Create an image file name
        String mTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String mImageName = "IMG_" + mTime + ".jpg";
        File mDirectory = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), getString(R.string.app_name));

        if (!mDirectory.exists()) {
            mDirectory.mkdirs();
        }

        return new File(mDirectory, mImageName);
    }

    /**
     * get new photos add in entry
     * @return
     */
    public List<Bitmap> getPhotos() {
        if (mPhotos.size() > 0)
            return mPhotos;
        else
            return null;
    }

    /**
     * set tag for photos
     * @param text
     */
    public void setTagPhotos(String text) {
        mTagPhoto.setText(text);
    }

    /**
     * get text of tag for photos
     * @return
     */
    public String getTagPhotos() {
        return mTagPhoto.getText().toString();
    }

    /**
     * Here create our toolbar for every add entry
     * @param icon
     */
    protected void navigationToolbarWithIcon(int icon) {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        ImageView mIconToolbar = (ImageView) findViewById(R.id.icon_toolbar);
        mIconToolbar.setImageResource(icon);
        setSupportActionBar(mToolbar);
    }


    /**
     * get Time select for new add
     * @return
     */
    protected String getDateTime() {
        if (mDateTime != null) return mDateTime;
        else return mDateTime = new SimpleDateFormat(getString(R.string.format_date_service), new Locale(mLocale[getAppPreferences().getInt("language", 0)])).format(Calendar.getInstance().getTime());
    }

    /**
     * Here set the date when user goes to edit the entry
     * @param oldDate
     */
    protected void setDateEditInButton(String oldDate) {
        Calendar date;
        mDateTime = oldDate;
        if (mDateTime != null) {
            date = Calendar.getInstance();
            try {
                date.setTime(new SimpleDateFormat(getString(R.string.format_date_service), new Locale(mLocale[getAppPreferences().getInt("language", 0)])).parse(mDateTime));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            date = Calendar.getInstance();
        }

        mChooseTimeButton.setText(getString(R.string.date_dialog) + " " +
                new PrettyTime(new Locale(mLocale[getAppPreferences().getInt("language", 0)])).format(date.getTime()).substring(0, 1).toUpperCase() +
                new PrettyTime(new Locale(mLocale[getAppPreferences().getInt("language", 0)])).format(date.getTime()).substring(1).toLowerCase());

    }

    /**
     * set date when user edit vaccine, with "skipped" or "vaccination" text
     * @param type
     */
    protected void setDateEditInVaccine(int type) {
        mChooseTimeButton.setText(getString(type) + " " +
                new PrettyTime(new Locale(mLocale[getAppPreferences().getInt("language", 0)])).format(Calendar.getInstance().getTime()).substring(0, 1).toUpperCase() +
                new PrettyTime(new Locale(mLocale[getAppPreferences().getInt("language", 0)])).format(Calendar.getInstance().getTime()).substring(1).toLowerCase());
    }

    /**
     * Date picker Dialog
     * @param oldDate
     * @param style
     */
    protected void showDateDialog(String oldDate, int style) {
        Calendar mMinDate = Calendar.getInstance();
        mMinDate.add(Calendar.MONTH, -6);
        Calendar mMaxDate = Calendar.getInstance();
        mMaxDate.add(Calendar.DAY_OF_MONTH, 1);
        Calendar mActualDate = Calendar.getInstance();

        Context mContextTheme = new ContextThemeWrapper(this, style);
        View mCustomDatePicker = getLayoutInflater().from(mContextTheme)
                .inflate(R.layout.custom_date_picker, null, false);

        final DatePicker mDatePicker = (DatePicker) mCustomDatePicker.findViewById(R.id.datePicker);
        mDatePicker.updateDate(mActualDate.get(Calendar.YEAR), mActualDate.get(Calendar.MONTH), mActualDate.get(Calendar.DAY_OF_MONTH));
        mDatePicker.setMaxDate(mMaxDate.getTime().getTime());
        mDatePicker.setMinDate(mMinDate.getTime().getTime());

        final Calendar mCalendar = Calendar.getInstance();

        if (oldDate != null) {
            mDateTime = oldDate;
            Calendar date = Calendar.getInstance();
            try {
                date.setTime(new SimpleDateFormat(getString(R.string.format_date_service)).parse(oldDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            mDatePicker.updateDate(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
            mCalendar.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
            mChooseTimeButton.setText(getString(R.string.date_dialog) + " " +
                    new PrettyTime(new Locale(mLocale[getAppPreferences().getInt("language", 0)])).format(date.getTime()).substring(0, 1).toUpperCase() +
                    new PrettyTime(new Locale(mLocale[getAppPreferences().getInt("language", 0)])).format(date.getTime()).substring(1).toLowerCase());
        }

        AlertDialog.Builder mDialog = new AlertDialog.Builder(this, style);
        mDialog.setView(mCustomDatePicker);
        mDialog.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCalendar.set(mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDayOfMonth(), mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE));
                mDateTime = new SimpleDateFormat(getString(R.string.format_date_service)).format(mCalendar.getTime());
                mChooseTimeButton.setText(getString(R.string.date_dialog) + " " +
                        new PrettyTime(new Locale(mLocale[getAppPreferences().getInt("language", 0)])).format(mCalendar.getTime()).substring(0, 1).toUpperCase() +
                        new PrettyTime(new Locale(mLocale[getAppPreferences().getInt("language", 0)])).format(mCalendar.getTime()).substring(1).toLowerCase());
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
     * Date + Time picker Dialog for every feed except Teeth, Vaccine, milestone and growth
     * because not necessary add time if this not occurring every time of day
     * @param oldDate
     * @param style
     * @param background_picker
     */
    protected void showDateTimeDialog(String oldDate, int style, int background_picker) {
        Calendar mMinDate = Calendar.getInstance();
        mMinDate.add(Calendar.MONTH, -6);
        Calendar mMaxDate = Calendar.getInstance();
        mMaxDate.add(Calendar.DAY_OF_MONTH, 1);
        SlideDateTimePicker.Builder mDialog = new SlideDateTimePicker.Builder(getSupportFragmentManager());
        mDialog.setListener(new SlideDateTimeListener() {
            @Override
            public void onDateTimeSet(Calendar calendar) {
                mDateTime = new SimpleDateFormat(getString(R.string.format_date_service)).format(calendar.getTime());
                mChooseTimeButton.setText(getString(R.string.date_dialog) + " " +
                        new PrettyTime(new Locale(mLocale[getAppPreferences().getInt("language", 0)])).format(calendar.getTime()).substring(0, 1).toUpperCase() +
                        new PrettyTime(new Locale(mLocale[getAppPreferences().getInt("language", 0)])).format(calendar.getTime()).substring(1).toLowerCase());
            }

            @Override
            public void onDateTimeCancel() {
            }
        });

        if (oldDate != null) {
            mDateTime = oldDate;
            Calendar date = Calendar.getInstance();
            try {
                date.setTime(new SimpleDateFormat(getString(R.string.format_date_service)).parse(oldDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            mDialog.setInitialDate(date.getTime());
            mChooseTimeButton.setText(getString(R.string.date_dialog) + " " +
                    new PrettyTime(new Locale(mLocale[getAppPreferences().getInt("language", 0)])).format(date.getTime()).substring(0, 1).toUpperCase() +
                    new PrettyTime(new Locale(mLocale[getAppPreferences().getInt("language", 0)])).format(date.getTime()).substring(1).toLowerCase());
        } else {
            mDialog.setInitialDate(Calendar.getInstance().getTime());
        }

        mDialog.setMinDate(mMinDate.getTime());
        mDialog.setMaxDate(mMaxDate.getTime());
        mDialog.setIs24HourTime(true);
        mDialog.setTheme(style);
        mDialog.setIndicatorColor(R.color.window_background);
        mDialog.setBackgroundTabColor(background_picker);
        mDialog.build().show();
    }

    /**
     * Show dialog for schedule vaccine
     * @param vaccine
     * @param oldDate
     * @param style
     */
    protected void showDateDialogVaccineSchedule(final int vaccine, String oldDate, int style) {
        Calendar mMinDate = Calendar.getInstance();
        mMinDate.add(Calendar.MONTH, -5);

        Context mContextTheme = new ContextThemeWrapper(this, style);
        View mCustomDatePicker = getLayoutInflater().from(mContextTheme)
                .inflate(R.layout.custom_date_picker, null, false);

        final DatePicker mDatePicker = (DatePicker) mCustomDatePicker.findViewById(R.id.datePicker);
        mDatePicker.setMaxDate(Calendar.getInstance().getTime().getTime());
        mDatePicker.setMinDate(mMinDate.getTime().getTime());

        if (oldDate != null) {
            mDateTime = oldDate;
            Calendar date = Calendar.getInstance();
            try {
                date.setTime(new SimpleDateFormat(getString(R.string.format_date_service)).parse(oldDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            mDatePicker.updateDate(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
            mChooseTimeButton.setText(getString(vaccine) + " " +
                    new PrettyTime(new Locale(mLocale[getAppPreferences().getInt("language", 0)])).format(date.getTime()).substring(0, 1).toUpperCase() +
                    new PrettyTime(new Locale(mLocale[getAppPreferences().getInt("language", 0)])).format(date.getTime()).substring(1).toLowerCase());
        }

        final Calendar mCalendar = Calendar.getInstance();

        AlertDialog.Builder mDialog = new AlertDialog.Builder(this, style);
        mDialog.setView(mCustomDatePicker);
        mDialog.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCalendar.set(mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDayOfMonth(), mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE));
                mDateTime = new SimpleDateFormat(getString(R.string.format_date_service)).format(mCalendar.getTime());
                mChooseTimeButton.setText(getString(vaccine) + " " +
                        new PrettyTime(new Locale(mLocale[getAppPreferences().getInt("language", 0)])).format(mCalendar.getTime()).substring(0, 1).toUpperCase() +
                        new PrettyTime(new Locale(mLocale[getAppPreferences().getInt("language", 0)])).format(mCalendar.getTime()).substring(1).toLowerCase());
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

    /**--------------------------------------------------------------------------------------------------------*/
    /**-----------------------------------------Doctor View-------------------------------------------- */

    /**
     * Here create the doctor view with all elements
     * */
    protected void createDoctorView() {
        setContentView(R.layout.activity_doctor_details);
        mDoctorNameBaby = (TextView) findViewById(R.id.name_baby);
        mDoctorCloseButton = (ImageView) findViewById(R.id.close_button);
        mDoctorCloseButton.setOnClickListener(this);
        mDoctorName = (TextView) findViewById(R.id.name_doctor);
        mDoctorHoursWork = (TextView) findViewById(R.id.hours_work_doctor);
        mDoctorSendEmail = (TextView) findViewById(R.id.send_doctor_email);
        mDoctorSendEmail.setOnClickListener(this);
        mDoctorCall1 = (TextView) findViewById(R.id.call_doctor_1);
        mDoctorCall1.setOnClickListener(this);
        mDoctorCall2 = (TextView) findViewById(R.id.call_doctor_2);
        mDoctorCall2.setOnClickListener(this);
        mDoctorAddress = (TextView) findViewById(R.id.address_doctor);
        mDoctorAddress.setOnClickListener(this);
        mDoctorEdit = (Button) findViewById(R.id.doctor_edit);
        mDoctorEdit.setOnClickListener(this);
        mDoctorContainerName = findViewById(R.id.container_name_doctor);
        mDoctorContainerHourWork = findViewById(R.id.container_hour_work_doctor);

        mDoctorNameBaby.setText(getChild().getName());
        mDoctorSendEmail.setText(
                String.format(getString(R.string.details_doctor_send_email),
                        getChild().getName()));
    }

    /**
     * Update data details
     */
    protected void updateDataDoctor() {
        if (!getAppPreferences().getString("name_doctor", "").equals("")) {
            mDoctorName.setText(getAppPreferences().getString("name_doctor", ""));
            mDoctorContainerName.setVisibility(View.VISIBLE);
        } else {
            mDoctorContainerName.setVisibility(View.GONE);
        }
        if (!getAppPreferences().getString("hour_work_doctor", "").equals("")) {
            mDoctorHoursWork.setText(getAppPreferences().getString("hour_work_doctor", ""));
            mDoctorContainerHourWork.setVisibility(View.VISIBLE);
        } else {
            mDoctorContainerHourWork.setVisibility(View.GONE);
        }
        if (!getAppPreferences().getString("phone_1_doctor", "").equals("")) {
            mDoctorCall1.setText(getAppPreferences().getString("phone_1_doctor", ""));
            mDoctorCall1.setVisibility(View.VISIBLE);
        } else {
            mDoctorCall1.setVisibility(View.GONE);
        }
        if (!getAppPreferences().getString("phone_2_doctor", "").equals("")) {
            mDoctorCall2.setText(getAppPreferences().getString("phone_2_doctor", ""));
            mDoctorCall2.setVisibility(View.VISIBLE);
        } else {
            mDoctorCall2.setVisibility(View.GONE);
        }
        if (!getAppPreferences().getString("address_doctor", "").equals("")) {
            mDoctorAddress.setText(getAppPreferences().getString("address_doctor", ""));
            mDoctorAddress.setVisibility(View.VISIBLE);
        } else {
            mDoctorAddress.setVisibility(View.GONE);
        }
    }

    /**
     * Create email view
     */
    protected void createDoctorEmail() {
        setContentView(R.layout.activity_doctor_email_options);
        mFeedChooseTime = (Button) findViewById(R.id.choose_time_email);
        mFeedChooseTime.setOnClickListener(this);
        SwitchCompat mTemperature = (SwitchCompat) findViewById(R.id.temperature_switch);
        mTemperature.setOnCheckedChangeListener(this);
        SwitchCompat mSickness = (SwitchCompat) findViewById(R.id.sickness_switch);
        mSickness.setOnCheckedChangeListener(this);
        SwitchCompat mMedicines = (SwitchCompat) findViewById(R.id.medicines_switch);
        mMedicines.setOnCheckedChangeListener(this);
        SwitchCompat mVaccines = (SwitchCompat) findViewById(R.id.vaccines_switch);
        mVaccines.setOnCheckedChangeListener(this);
        SwitchCompat mFeeding = (SwitchCompat) findViewById(R.id.feeding_switch);
        mFeeding.setOnCheckedChangeListener(this);
        SwitchCompat mSleeping = (SwitchCompat) findViewById(R.id.sleeping_switch);
        mSleeping.setOnCheckedChangeListener(this);
        SwitchCompat mDiapers = (SwitchCompat) findViewById(R.id.diapers_switch);
        mDiapers.setOnCheckedChangeListener(this);
        SwitchCompat mGrowth = (SwitchCompat) findViewById(R.id.growth_switch);
        mGrowth.setOnCheckedChangeListener(this);
        SwitchCompat mMilestones = (SwitchCompat) findViewById(R.id.milestone_switch);
        mMilestones.setOnCheckedChangeListener(this);
        SwitchCompat mTeeth = (SwitchCompat) findViewById(R.id.teeth_switch);
        mTeeth.setOnCheckedChangeListener(this);
        SwitchCompat mOther = (SwitchCompat) findViewById(R.id.other_switch);
        mOther.setOnCheckedChangeListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mFeedChooseTime.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.tinted_icon_filter_time_feed), null, null, null);

        mFeedChooseTime.setText(getString(R.string.filter_title_message) + " " +
                getResources().getStringArray(R.array.filter_time_list)[getAppPreferences().getInt("filter_time", 2)]);

        mTemperature.setChecked(getAppPreferences().getBoolean("temperature_switch", false));
        mSickness.setChecked(getAppPreferences().getBoolean("sickness_switch", false));
        mMedicines.setChecked(getAppPreferences().getBoolean("medicines_switch", false));
        mVaccines.setChecked(getAppPreferences().getBoolean("vaccines_switch", false));
        mFeeding.setChecked(getAppPreferences().getBoolean("feeding_switch", false));
        mSleeping.setChecked(getAppPreferences().getBoolean("sleeping_switch", false));
        mDiapers.setChecked(getAppPreferences().getBoolean("diapers_switch", false));
        mGrowth.setChecked(getAppPreferences().getBoolean("growth_switch", false));
        mMilestones.setChecked(getAppPreferences().getBoolean("milestone_switch", false));
        mTeeth.setChecked(getAppPreferences().getBoolean("teeth_switch", false));
        mOther.setChecked(getAppPreferences().getBoolean("other_switch", false));
    }

    public Button getChooseTimeEmail() {
        return mFeedChooseTime;
    }

    /**
     * Create edit view
     */
    protected void createDoctorEdit() {
        setContentView(R.layout.activity_doctor_edit);
        mName = (EditText) findViewById(R.id.edit_name_doctor);
        mHourWork = (EditText) findViewById(R.id.edit_hour_work_doctor);
        mPhone1 = (EditText) findViewById(R.id.edit_phone_1_doctor);
        mPhone2 = (EditText) findViewById(R.id.edit_phone_2_doctor);
        mEmail = (EditText) findViewById(R.id.edit_email_doctor);
        mAddress = (EditText) findViewById(R.id.edit_address_doctor);

        if (!getAppPreferences().getString("name_doctor", "").equals(""))
            mName.setText(getAppPreferences().getString("name_doctor", ""));
        if (!getAppPreferences().getString("hour_work_doctor", "").equals(""))
            mHourWork.setText(getAppPreferences().getString("hour_work_doctor", ""));
        if (!getAppPreferences().getString("phone_1_doctor", "").equals(""))
            mPhone1.setText(getAppPreferences().getString("phone_1_doctor", ""));
        if (!getAppPreferences().getString("phone_2_doctor", "").equals(""))
            mPhone2.setText(getAppPreferences().getString("phone_2_doctor", ""));
        if (!getAppPreferences().getString("email_doctor", "").equals(""))
            mEmail.setText(getAppPreferences().getString("email_doctor", ""));
        if (!getAppPreferences().getString("address_doctor", "").equals(""))
            mAddress.setText(getAppPreferences().getString("address_doctor", ""));
    }

    /**
     * Save data doctor
     */
    protected void changeDataDoctor() {
        getEditorPreferences().putString("name_doctor", mName.getText().toString());
        getEditorPreferences().putString("hour_work_doctor", mHourWork.getText().toString());
        getEditorPreferences().putString("phone_1_doctor", mPhone1.getText().toString());
        getEditorPreferences().putString("phone_2_doctor", mPhone2.getText().toString());
        getEditorPreferences().putString("email_doctor", mEmail.getText().toString());
        getEditorPreferences().putString("address_doctor", mAddress.getText().toString());
        getEditorPreferences().commit();
    }

    /**
     * Selected what data send
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.temperature_switch:
                getEditorPreferences().putBoolean("temperature_switch", isChecked);
                break;
            case R.id.sickness_switch:
                getEditorPreferences().putBoolean("sickness_switch", isChecked);
                break;
            case R.id.medicines_switch:
                getEditorPreferences().putBoolean("medicines_switch", isChecked);
                break;
            case R.id.vaccines_switch:
                getEditorPreferences().putBoolean("vaccines_switch", isChecked);
                break;
            case R.id.feeding_switch:
                getEditorPreferences().putBoolean("feeding_switch", isChecked);
                break;
            case R.id.sleeping_switch:
                getEditorPreferences().putBoolean("sleeping_switch", isChecked);
                break;
            case R.id.diapers_switch:
                getEditorPreferences().putBoolean("diapers_switch", isChecked);
                break;
            case R.id.growth_switch:
                getEditorPreferences().putBoolean("growth_switch", isChecked);
                break;
            case R.id.milestone_switch:
                getEditorPreferences().putBoolean("milestone_switch", isChecked);
                break;
            case R.id.teeth_switch:
                getEditorPreferences().putBoolean("teeth_switch", isChecked);
                break;
            case R.id.other_switch:
                getEditorPreferences().putBoolean("other_switch", isChecked);
                break;
        }
        getEditorPreferences().commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_nothing, R.anim.anim_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
