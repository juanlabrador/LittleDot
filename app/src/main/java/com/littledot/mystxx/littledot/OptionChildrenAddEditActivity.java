package com.littledot.mystxx.littledot;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import domains.Child;
import domains.DotCategory;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import services.UserServices;

/**
 * Created by juanlabrador on 12/08/15.
 */
public class OptionChildrenAddEditActivity extends ParentFeedActivity implements View.OnClickListener {

    private static final int TAKE_CAMERA = 1;
    private static final int TAKE_GALLERY = 2;
    private static final int CROP_IMAGE = 3;
    private static final String TAG = "OptionChildrenAddEdit";
    private CircleImageView mPhotoEdit;
    private EditText mNameEdit;
    private EditText mBirthdayEdit;
    private Button mEraseData;
    private Bitmap mBitmapPhoto;
    private Button mGenderMale;
    private Button mGenderFemale;
    private Toolbar mToolbar;
    private SimpleDateFormat mFormatDate;
    private SimpleDateFormat mFormatDB;
    private Child mChild = null;
    private Date mDoB;
    private boolean justCamera = true;

    // Dialog Birthday
    private LayoutInflater mLayoutInflater;
    private View mCustomDatePicker;
    private DatePicker mDatePicker;
    private Calendar mCalendar;
    private AlertDialog.Builder mDialog;
    public String[] mLocale = { "en", "es", "de", "hr" };

    private Uri mUriPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option_children_add_edit);

        mFormatDate = new SimpleDateFormat(getString(R.string.format_pretty_time), new Locale(mLocale[getAppPreferences().getInt("language", 0)]));
        mFormatDB = new SimpleDateFormat(getString(R.string.format_date_service), new Locale(mLocale[getAppPreferences().getInt("language", 0)]));
        init();

        /**
         *
         * If there is a child select for edit, it load the data
         * If the user go to create a new child
         *
         * */
        // Edit a child
        if (getIntent().getParcelableExtra("children") != null) {
            mChild = getIntent().getParcelableExtra("children");

            mToolbar.setTitle(mChild.getName());
            mNameEdit.setText(mChild.getName());
            try {
                mDoB = mFormatDB.parse(mChild.getDob());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            mBirthdayEdit.setText(mFormatDate.format(mDoB).substring(0, 1).toUpperCase() +
                    mFormatDate.format(mDoB).substring(1).toLowerCase());

            if (mChild.getImage() != null)
                mPhotoEdit.setImageBitmap(getImageFromBytes(mChild.getImage()));

            if (mChild.getSex() == 0) {  // He's a boy
                mGenderMale.setBackgroundResource(R.drawable.style_button_color);
                mGenderMale.setTextColor(getResources().getColor(R.color.window_background));
                mGenderFemale.setBackgroundResource(R.drawable.style_button_border);
                mGenderFemale.setTextColor(getResources().getColor(R.color.text_feed));
            } else {  // She's a girl
                mGenderFemale.setBackgroundResource(R.drawable.style_button_color);
                mGenderFemale.setTextColor(getResources().getColor(R.color.window_background));
                mGenderMale.setBackgroundResource(R.drawable.style_button_border);
                mGenderMale.setTextColor(getResources().getColor(R.color.text_feed));
            }
            mEraseData.setText(String.format(getString(R.string.setting_children_menu_delete),
                    mChild.getName()));
        } else { // Add new child
            mToolbar.setTitle(R.string.setting_children_menu_new);
            mEraseData.setVisibility(View.GONE);
            mChild = new Child();
            mChild.setID(UUID.randomUUID().toString().toUpperCase());  // Generate an ID
            mChild.setSex(0);
        }
        setSupportActionBar(mToolbar);
    }

    private void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mPhotoEdit = (CircleImageView) findViewById(R.id.edit_image_baby);
        mPhotoEdit.setOnClickListener(this);
        mNameEdit = (EditText) findViewById(R.id.edit_name_children);
        mNameEdit.addTextChangedListener(new TextWatcher() {
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

        mBirthdayEdit = (EditText) findViewById(R.id.edit_date_birth_children);
        mBirthdayEdit.setOnClickListener(this);
        mBirthdayEdit.addTextChangedListener(new TextWatcher() {
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

        mEraseData = (Button) findViewById(R.id.erase_data_children_button);
        mEraseData.setOnClickListener(this);

        mGenderMale = (Button) findViewById(R.id.option_male);
        mGenderMale.setOnClickListener(this);
        mGenderFemale = (Button) findViewById(R.id.option_female);
        mGenderFemale.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_date_birth_children:
                showDateDialog(mBirthdayEdit.getText().toString());
                break;
            case R.id.edit_image_baby:
                showDialogPhoto();
                break;
            case R.id.erase_data_children_button:
                showConfirmDeleteDialog();
                break;
            case R.id.option_male:
                mGenderMale.setBackgroundResource(R.drawable.style_button_color);
                mGenderMale.setTextColor(getResources().getColor(R.color.window_background));
                mGenderFemale.setBackgroundResource(R.drawable.style_button_border);
                mGenderFemale.setTextColor(getResources().getColor(R.color.text_feed));
                mChild.setSex(0);
                break;
            case R.id.option_female:
                mGenderFemale.setBackgroundResource(R.drawable.style_button_color);
                mGenderFemale.setTextColor(getResources().getColor(R.color.window_background));
                mGenderMale.setBackgroundResource(R.drawable.style_button_border);
                mGenderMale.setTextColor(getResources().getColor(R.color.text_feed));
                mChild.setSex(1);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getIntent().getParcelableExtra("children") != null) { // Edit child
            if (!mNameEdit.getText().toString().isEmpty() &&
                    !mBirthdayEdit.getText().toString().isEmpty()) {
                getMenuInflater().inflate(R.menu.menu_children_edit, menu);
            }
        } else {// Add child
            if (!mNameEdit.getText().toString().isEmpty() &&
                    !mBirthdayEdit.getText().toString().isEmpty())
                getMenuInflater().inflate(R.menu.menu_children_add, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

       mChild.setName(mNameEdit.getText().toString());
        // When in menu, the action button save is clicked
        if (id == R.id.save_children) {
            updateChild(mChild);
        } else if (id == R.id.add_children) { // When in menu, the action button add is clicked
            addNewChild(mChild);
            selectChild();

            // If you went of OptionList and not of main feed
            if (OptionListChildrenActivity.mActivity != null)
                OptionListChildrenActivity.mActivity.finish();
        }

        // Finish the activity
        overridePendingTransition(R.anim.anim_nothing, R.anim.anim_out);
        finish();
        return super.onOptionsItemSelected(item);
    }

    // Transform Bitmap to bytes
    public byte[] getBytesFromBitmap(Bitmap image) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }

    // Transform bytes to bitmap
    public Bitmap getImageFromBytes(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    /**
     * Show dialog confirm delete
     * */
    private void showConfirmDeleteDialog() {
        mDialog = new AlertDialog.Builder(this, R.style.NormalStyleDialog);
        mDialog.setMessage(
                String.format(getString(R.string.setting_children_menu_delete_dialog),
                        mChild.getName())
        );
        mDialog.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if (getAppPreferences().getString("id_user", "").equals("")) {
                    deleteChild(mChild.getID());
                    selectChild();
                    finish();
                /*} else {
                    if (haveNetworkConnection()) {
                        //List<String> idsChildren = new ArrayList<>();
                        //idsChildren.add(mChild.getID());
                        //new SyncDeleteChildren().execute(idsChildren);
                        List<DotCategory> dots = getDots(mChild);
                        List<String> idsEntries = new ArrayList<>();
                        for (DotCategory dot : dots) {
                            idsEntries.add(dot.getId());
                        }
                        if (dots.size() > 0) {
                            Log.i(TAG, "There are entries.");
                            new SyncDeleteEntries().execute(idsEntries);
                        } else {
                            Log.i(TAG, "There aren't entries.");
                            List<String> idsChildren = new ArrayList<>();
                            idsChildren.add(mChild.getID());
                            new SyncDeleteChildren().execute(idsChildren);
                        }
                    } else {
                        // Add child pending by delete
                        addPendingChild(mChild.getID());

                        // Add pending dots by delete
                        List<DotCategory> dots = getDots(mChild);
                        for (DotCategory dot : dots) {
                            addPendingDot(dot.getId());
                        }

                        deleteChild(mChild.getID());
                        selectChild();
                        finish();
                    }
                }*/

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
     * Date picker Dialog
     * */
    private void showDateDialog(String oldDate) {
        mLayoutInflater = getLayoutInflater();
        mCustomDatePicker = mLayoutInflater.inflate(R.layout.custom_date_picker, null);

        mDatePicker = (DatePicker) mCustomDatePicker.findViewById(R.id.datePicker);
        if (!oldDate.equals("")) {
            Calendar date = Calendar.getInstance();
            try {
                date.setTime(mFormatDate.parse(oldDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            mDatePicker.updateDate(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
        }
        mDatePicker.setMaxDate(Calendar.getInstance().getTime().getTime());

        mCalendar = Calendar.getInstance();

        mDialog = new AlertDialog.Builder(this, R.style.NormalStyleDialog);
        mDialog.setView(mCustomDatePicker);
        mDialog.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCalendar.set(mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDayOfMonth());
                mBirthdayEdit.setText(mFormatDate.format(mCalendar.getTime()).substring(0, 1).toUpperCase() +
                        mFormatDate.format(mCalendar.getTime()).substring(1).toLowerCase());
                mChild.setDob(mFormatDB.format(mCalendar.getTime()));
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
     * It select the last child adding while child > 0
     * If I deleted a child, it selected the last child existing
     * */
    private void selectChild() {
        List<Child> mChildren = getAllChildChildren();
        if (mChildren.size() > 0) {
            String mID = mChildren.get(mChildren.size() - 1).getID();
            getEditorPreferences().putString("child_selected", mID);
            getEditorPreferences().commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_nothing, R.anim.anim_out);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case TAKE_CAMERA:
                    doCrop();
                    break;
                case TAKE_GALLERY:
                    if (data != null) {
                        mUriPhoto = data.getData();
                        doCrop();
                        justCamera = false;
                    }
                    break;
                case CROP_IMAGE:
                    if (data != null) {
                        mBitmapPhoto = data.getExtras().getParcelable("data");
                        mPhotoEdit.setImageBitmap(mBitmapPhoto);
                        mChild.setImage(getBytesFromBitmap(mBitmapPhoto));
                    }

                    if (justCamera) {
                        // Update Gallery after a photo
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.DATA, mUriPhoto.getEncodedPath());
                        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                        getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    }
                    break;
            }
        }
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

    public class SyncDeleteChildren extends AsyncTask<List<String>, Void, Boolean> {

        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(OptionChildrenAddEditActivity.this);
            mProgressDialog.setMessage(getString(R.string.setting_progress));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            //mProgressDialog.create();
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(List<String>... ids) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ParentFeedActivity.API)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();

            List<String> childGuids = new ArrayList<>(ids[0]);

            UserServices service = retrofit.create(UserServices.class);

            Call<Object> call = service.deleteChildren(childGuids);

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
            if (s) {
                Toast.makeText(getApplicationContext(), "ok 200", Toast.LENGTH_SHORT).show();
                /*List<DotCategory> dots = getDots(mChild);
                List<String> idsEntries = new ArrayList<>();
                for (DotCategory dot : dots) {
                    idsEntries.add(dot.getId());
                }
                if (dots.size() > 0) {
                    Log.i(TAG, "There are entries.");
                    new SyncDeleteEntries().execute(idsEntries);
                } else {
                    Log.i(TAG, "There aren't entries.");
                    deleteChild(mChild.getID());
                    selectChild();
                    finish();
                }*/
                deleteChild(mChild.getID());
                selectChild();
                finish();
            } else
                Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
            mProgressDialog.dismiss();
        }
    }

    public class SyncDeleteEntries extends AsyncTask<List<String>, Void, Boolean> {

        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(OptionChildrenAddEditActivity.this);
            mProgressDialog.setMessage(getString(R.string.setting_progress));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            //mProgressDialog.create();
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(List<String>... params) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();

            List<String> entryGuids = new ArrayList<>(params[0]);

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
            if (s) {

                List<String> idsChildren = new ArrayList<>();
                idsChildren.add(mChild.getID());
                new SyncDeleteChildren().execute(idsChildren);

                //deleteChild(mChild.getID());
                //selectChild();
                //finish();
                Toast.makeText(getApplicationContext(), "ok 200", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
            mProgressDialog.dismiss();
        }
    }

}
