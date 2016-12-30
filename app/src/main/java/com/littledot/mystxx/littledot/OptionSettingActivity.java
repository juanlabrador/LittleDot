package com.littledot.mystxx.littledot;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import database.DataBaseHandler;
import domains.Child;
import domains.DotCategory;
import domains.DotValues;
import domains.Image;
import domains.services.Children;
import domains.services.SyncEntriesByChildren;
import domains.services.Entries;
import domains.services.EntryChildren;
import domains.services.EntryData;
import domains.services.SyncChildren;
import google.SyncGoogle;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import services.UserServices;

/**
 * Created by juanlabrador on 12/08/15.
 */
public class OptionSettingActivity extends ParentFeedActivity implements View.OnClickListener {

    private static final String TAG = "OptionSettingActivity";
    private Button mLoginLink;
    private Button mSignInButton;
    private Button mSignOutButton;
    private SignInButton mGoogleAccount;
    private SyncGoogle mSyncGoogle;
    private Button mSyncData;
    private TextView mRestorePurchases;
    private TextView mLanguage;
    private TextView mUnit;
    private TextView mEraseAllData;
    private LayoutInflater mLayoutInflater;
    private Toolbar mToolbar;
    private DataBaseHandler mDB;
    private List<Child> mChildren;

    private String[] mLanguages;
    private String[] mUnits;
    private AlertDialog.Builder mDialog;
    private AlertDialog mEditDialog;
    private View mDataTag;
    private View mContainerData;

    // Upload Image
    List<Children> childrenCopy;

    // Login Link
    private static String URL_SERVER = " http://88.198.2.141/";
    private AppCompatEditText mInputEmail;
    private boolean validate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option_setting);
        mLanguages = getResources().getStringArray(R.array.languages);
        mUnits = getResources().getStringArray(R.array.units);
        mDB = new DataBaseHandler(this);
        init();
        mSyncGoogle = new SyncGoogle(this, mSignInButton, mSignOutButton, getAppPreferences(), getEditorPreferences());


        //getEditorPreferences().putString("email_user", "hrvoje@flipkod.com");
        //getEditorPreferences().putString("id_user", "21EC2020-3AEA-1069-A2DD-08002B30309D");
        //getEditorPreferences().commit();

        // Get key from Email intent
        /*if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            Uri uri = getIntent().getData();
            String email = uri.getQueryParameter("email");
            String id = uri.getQueryParameter("user_id");

            getEditorPreferences().putString("email_user", email);
            getEditorPreferences().putString("id_user", id);
            getEditorPreferences().commit();

            showMessageLogin(R.string.setting_sync_complete);
            if (!getAppPreferences().getString("id_user", "").equals("")) {
                new SyncUserData().execute(getAppPreferences().getString("id_user", ""),
                        getAppPreferences().getString("date_modified", "2014-01-01T00:00:00Z"));
            }
            Toast.makeText(this, "Email: " + email + " " + "ID: " + id, Toast.LENGTH_SHORT).show();
        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLanguages = getResources().getStringArray(R.array.languages);
        mUnits = getResources().getStringArray(R.array.units);
        mDB = new DataBaseHandler(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mChildren.size() == 0) {
            mDataTag.setVisibility(View.GONE);
            mContainerData.setVisibility(View.GONE);
        }

        if (!getAppPreferences().getString("email_user", "").equals("")) {
            mSignInButton.setVisibility(View.GONE);
            mSignOutButton.setVisibility(View.VISIBLE);
        } else {
            mSignInButton.setVisibility(View.VISIBLE);
            mSignOutButton.setVisibility(View.GONE);
        }
    }

    private void init() {

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.setting_title);

        setSupportActionBar(mToolbar);

        mLoginLink = (Button) findViewById(R.id.login_link);
        mLoginLink.setOnClickListener(this);
        mSignInButton = (Button) findViewById(R.id.sign_in);
        mSignInButton.setOnClickListener(this);
        mSignOutButton = (Button) findViewById(R.id.sign_out);
        mSignOutButton.setOnClickListener(this);
        mGoogleAccount = (SignInButton) findViewById(R.id.enter_email);
        mGoogleAccount.setOnClickListener(this);
        mRestorePurchases = (TextView) findViewById(R.id.purchases);
        mLanguage = (TextView) findViewById(R.id.setting_language);
        mLanguage.setText(mLanguages[getAppPreferences().getInt("language", 0)]);
        mLanguage.setOnClickListener(this);
        mUnit = (TextView) findViewById(R.id.setting_units);
        mUnit.setText(mUnits[getAppPreferences().getInt("unit", 0)]);
        mUnit.setOnClickListener(this);
        mDataTag = findViewById(R.id.data_children);
        mContainerData = findViewById(R.id.container_data);
        mEraseAllData = (TextView) findViewById(R.id.setting_erase_all_data);
        mEraseAllData.setOnClickListener(this);
        mSyncData = (Button) findViewById(R.id.sync_data);
        mSyncData.setOnClickListener(this);

        mChildren = mDB.getAllChildChildren();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mSyncGoogle.isConnected()) {
            mSyncGoogle.disconnect();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_language:
                showDialogLanguage();
                break;
            case R.id.setting_units:
                showDialogUnits();
                break;
            case R.id.setting_erase_all_data:
                showDialogEraseAllData();
                break;
            case R.id.sign_in:
                mSyncGoogle.signInGoogle();
                //showInputEmail();
                break;
            case R.id.sign_out:
                mSyncGoogle.showDialog();
                //showInputEmail();
                break;
            case R.id.enter_email:
                //showInputEmail();
                break;
            case R.id.login_link:
                //if (mInputEmail != null && !mInputEmail.getText().toString().isEmpty())
                //    new LoginSync().execute(mGoogleAccount.getText().toString());
                break;
            case R.id.sync_data:
                String email = getAppPreferences().getString("email_user", "");
                if (!email.equals("")) {
                    if (haveNetworkConnection()) {
                        //new SyncEntries().execute(email);
                        new SyncChild().execute(email);
                        //new SyncUserData().execute(getAppPreferences().getString("id_user", ""),
                        //        getAppPreferences().getString("date_modified", "2014-01-01T00:00:00Z"));
                    } else {
                        Toast.makeText(getApplicationContext(), "Check your internet connection.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    /**
     * Method for change the languages
     */
    private void showDialogLanguage() {
        mDialog = new AlertDialog.Builder(this);
        mDialog.setTitle(R.string.setting_language_dialog);
        mDialog.setSingleChoiceItems(mLanguages, getAppPreferences().getInt("language", 0), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        mLanguage.setText(mLanguages[0]);
                        setLocale("en");
                        break;
                    case 1:
                        mLanguage.setText(mLanguages[1]);
                        setLocale("es");
                        break;
                    case 2:
                        mLanguage.setText(mLanguages[1]);
                        setLocale("de");
                        break;
                    case 3:
                        mLanguage.setText(mLanguages[2]);
                        setLocale("hr");
                        break;
                }
                getEditorPreferences().putBoolean("changed_language", true);
                getEditorPreferences().putInt("language", which);
                getEditorPreferences().commit();
                dialog.dismiss();
            }
        });
        mDialog.create();
        mDialog.show();
    }

    /**
     * Method for change the units metrics
     */
    private void showDialogUnits() {
        mDialog = new AlertDialog.Builder(this);
        mDialog.setTitle(R.string.setting_units_dialog);
        mDialog.setSingleChoiceItems(mUnits, getAppPreferences().getInt("unit", 0), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        mUnit.setText(mUnits[0]);
                        break;
                    case 1:
                        mUnit.setText(mUnits[1]);
                        break;

                }
                getEditorPreferences().putInt("unit", which);
                getEditorPreferences().commit();
                dialog.dismiss();
            }
        });
        mDialog.create();
        mDialog.show();
    }

    /**
     * Dialog when click in erase all data
     */
    private void showDialogEraseAllData() {
        mDialog = new AlertDialog.Builder(this, R.style.NormalStyleDialog);
        mDialog.setTitle(R.string.setting_erase_all_data);

        mLayoutInflater = mLayoutInflater.from(this);
        View view = mLayoutInflater.inflate(R.layout.activity_option_setting_erase_all_data, null);

        mEraseAllData = (TextView) view.findViewById(R.id.erase_all_data_children);

        StringBuilder mMessage = new StringBuilder(getString(R.string.setting_erase_all_data_button)).append(" ");
        for (int i = 0; i < mChildren.size(); i++) {
            if (mChildren.size() == 1) {
                mMessage.append(mChildren.get(i).getName()).append(".");
            } else {
                if (i == mChildren.size() - 1) { // If it is the last
                    mMessage.append(mChildren.get(i).getName()).append(".");
                } else {
                    mMessage.append(mChildren.get(i).getName()).append(", ");
                }
            }
        }

        mEraseAllData.setText(mMessage.toString());

        mDialog.setView(view);
        mDialog.setPositiveButton(R.string.button_erase_all_data, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showConfirmDeleteDialog();
                dialog.dismiss();
            }
        });
        mDialog.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mDialog.setCancelable(false);
        mEditDialog = mDialog.create();
        mEditDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                mEditDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.text_red));
            }
        });
        mDialog.show();

    }

    /**
     * Method when user confirm to delete all data
     */
    private void showConfirmDeleteDialog() {
        mDialog = new AlertDialog.Builder(this, R.style.NormalStyleDialog);
        mDialog.setMessage(R.string.setting_erase_all_data_dialog);
        mDialog.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (getAppPreferences().getString("id_user", "").equals("")) {
                    Log.i(TAG, "Doesn't have ID");
                    for (Child child : mChildren) {
                        mDB.deleteChild(child.getID());
                    }
                } else {
                    Log.i(TAG, "Has ID");

                    List<String> idsChildren = new ArrayList<>();
                    for (Child child : mChildren) {
                        idsChildren.add(child.getID());
                    }
                    new SyncDeleteChildren().execute(idsChildren);
                    dialog.dismiss();
                }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mSyncGoogle.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_nothing, R.anim.anim_out);
    }

    /**
     * Method for change language
     * @param lang
     */
    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, OptionSettingActivity.class);
        refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(refresh);
    }

    /**
     * Method for validate email
     * @param target
     * @return
     */
    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    /**
     * Show dialog where input email
     */
    private void showInputEmail() {
        View view = getLayoutInflater().inflate(R.layout.custom_entry_email, null);
        final TextView mError = (TextView) view.findViewById(R.id.error_email);

        mInputEmail = (AppCompatEditText) view.findViewById(R.id.user_email);
        mInputEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validate = isValidEmail(mInputEmail.getText().toString());
                if (validate) {
                    mError.setVisibility(View.GONE);
                } else {
                    mError.setVisibility(View.VISIBLE);
                }
            }
        });
        AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
        mDialog.setView(view);
        mDialog.setPositiveButton(R.string.button_ok, null);
        mDialog.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog dialog = mDialog.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate) {
                    //mGoogleAccount.setText(mInputEmail.getText().toString());
                    //mGoogleAccount.setTextColor(getResources().getColor(R.color.text_normal));
                    dialog.dismiss();
                }
            }
        });
    }

    /**
     * Show the message later the email receive key
     */
    private void showMessageLogin(int string) {
        AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
        mDialog.setMessage(string);
        mDialog.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mDialog.create();
        mDialog.show();
    }

    /**
     * class for implements in background called to service
     */
    class LoginSync extends AsyncTask<String, Void, Boolean> {

        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(OptionSettingActivity.this);
            mProgressDialog.setMessage(getString(R.string.setting_progress));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            //mProgressDialog.create();
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(URL_SERVER)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();

            UserServices service = retrofit.create(UserServices.class);

            Call<Object> call = service.getKeyLogin(params[0]);

            try {
                return call.execute().isSuccess();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {

            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            mProgressDialog.dismiss();
            if (b) {
                showMessageLogin(R.string.setting_login_message);
                //mGoogleAccount.setText(R.string.setting_button_email);
                //mGoogleAccount.setTextColor(getResources().getColor(R.color.darkGray));
            } else
                Toast.makeText(getApplicationContext(), "Try again.", Toast.LENGTH_SHORT).show();
        }
    }

    public class SyncDeleteChildren extends AsyncTask<List<String>, Void, Boolean> {

        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(OptionSettingActivity.this);
            mProgressDialog.setMessage(getString(R.string.setting_progress));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            //mProgressDialog.create();
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(List<String>... ids) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API)
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
                List<String> idsEntries = new ArrayList<>();
                for (Child child : mChildren) {
                    List<DotCategory> dots = getDots(child);
                    for (DotCategory dot : dots) {
                        idsEntries.add(dot.getId());
                    }
                }
                if (idsEntries.size() > 0) {
                    Log.i(TAG, "There are entries.");
                    new SyncDeleteEntries().execute(idsEntries);
                } else {
                    Log.i(TAG, "There aren't entries.");
                    for (Child child : mChildren) {
                        mDB.deleteChild(child.getID());
                    }
                    mDataTag.setVisibility(View.GONE);
                    mContainerData.setVisibility(View.GONE);
                }

                Toast.makeText(getApplicationContext(), "ok delete child 200", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getApplicationContext(), "error delete child", Toast.LENGTH_SHORT).show();

            // Close progress dialog
            mProgressDialog.dismiss();
        }
    }

    public class SyncDeleteEntries extends AsyncTask<List<String>, Void, Boolean> {

        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(OptionSettingActivity.this);
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
                // Once deleted confirm child and entries in server, continue to delete in local db
                for (Child child : mChildren) {
                    mDB.deleteChild(child.getID());
                }
                mDataTag.setVisibility(View.GONE);
                mContainerData.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "ok delete entries 200", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getApplicationContext(), "error delete entries", Toast.LENGTH_SHORT).show();
            mProgressDialog.dismiss();
        }
    }

    public class SyncUserData extends AsyncTask<String, Void, List<EntryChildren>> {

        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(OptionSettingActivity.this);
            mProgressDialog.setMessage(getString(R.string.setting_progress));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            //mProgressDialog.create();
            mProgressDialog.show();
        }

        @Override
        protected List<EntryChildren> doInBackground(String... params) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();

            String userId = "21EC2020-3AEA-1069-A2DD-08002B30309D";
            String dateModified = "2014-01-01T00:00:00Z";

            UserServices service = retrofit.create(UserServices.class);

            Call<List<EntryChildren>> call = service.syncUser(params[0], params[1]);

            Response<List<EntryChildren>> response = null;
            try {
                response = call.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response.body();
        }

        @Override
        protected void onPostExecute(List<EntryChildren> list) {
            super.onPostExecute(list);
            mProgressDialog.dismiss();
            for (EntryChildren data : list) {
                // Set child
                Child child = new Child(data.getGuid(), data.getName(),
                        data.getBirthDate(), data.getGender(), null);
                for (Entries entry : data.getEntries()) {
                    // Set dot by category
                    List<DotValues> dotValues = new ArrayList<>();
                    for (EntryData entryData : entry.getEntryData()) {
                        // set values
                        DotValues dotValue;
                        if (entryData.getSkipped())
                            dotValue = new DotValues(entryData.getGroup(), entryData.getValue(),
                                entryData.getDetailValue(), entryData.getText(), 1);
                        else
                            dotValue = new DotValues(entryData.getGroup(), entryData.getValue(),
                                    entryData.getDetailValue(), entryData.getText(), 0);

                        dotValues.add(dotValue);
                    }

                    JSONArray jsonArray = getJSONArrayFromListDot(dotValues);
                    DotCategory dot = new DotCategory(entry.getGuid(), 0 /*entry.getCategoryId()*/, jsonArray,""
                            /*entry.getDateModified()*/, entry.getNote());

                    for (String idImage : entry.getPictures()) {
                        new DownloadImage().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, idImage, entry.getGuid());
                    }
                    // if dot exist or not, add or not
                    mDB.isDotExist(dot, child);
                }
                // if child exist, add or not
                mDB.isChildExist(child);
            }
            if (list.size() > 0) {
                Toast.makeText(getApplicationContext(), "Name: " + list.get(0).getName(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "DoB: " + list.get(0).getBirthDate(), Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), "Category: " + list.get(0).getEntries().get(0).getCategoryId(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Value:" + list.get(0).getEntries().get(0).getEntryData().get(0).getValue(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Note:" + list.get(0).getEntries().get(0).getNote(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class SyncEntries extends AsyncTask<String, Void, Boolean> {

        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(OptionSettingActivity.this);
            mProgressDialog.setMessage(getString(R.string.setting_progress));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            //mProgressDialog.create();
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();

            Gson gson = new Gson();
            List<Children> childrenList = new ArrayList<>();
            for (Child child : mChildren) {
                List<Entries> entriesList = new ArrayList<>();
                List<DotCategory> dots = getDots(child);
                for (DotCategory dot : dots) {
                    List<DotValues> dotValues = getListDotFromJSONArray(dot.getEntryData());
                    List<EntryData> entryDatas = new ArrayList<>();
                    for (DotValues dotValue : dotValues) {
                        EntryData entryData;

                        if (dotValue.getSkipped() == 1)
                            entryData = new EntryData(dotValue.getGroup(), dotValue.getValue(),
                                dotValue.getDetailValue(), dotValue.getText(), true);
                        else
                            entryData = new EntryData(dotValue.getGroup(), dotValue.getValue(),
                                    dotValue.getDetailValue(), dotValue.getText(), false);
                        entryDatas.add(entryData);
                    }

                    List<String> images = new ArrayList<>();
                    for (Image img : mDB.getImages(dot.getId())) {
                        images.add(img.getId());
                    }

                    Entries entries = new Entries(dot.getId(), dot.getCategoryId(), dot.getDate(), entryDatas,
                            images, dot.getNote());
                    entriesList.add(entries);
                }

                Children children = new Children(child.getID(), entriesList);
                childrenList.add(children);

                childrenCopy = new ArrayList<>(childrenList);
            }

            SyncEntriesByChildren sync = new SyncEntriesByChildren(params[0], childrenList);

            UserServices service = retrofit.create(UserServices.class);

            Log.i(TAG, "Upload ALL entries: " + gson.toJson(sync));
            Call<Object> call = service.syncEntries(gson.toJson(sync));

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
                for (Children child : childrenCopy) {
                    for (Entries entry : child.getEntries()) {
                        for (String idImage : entry.getPictures()) {
                            Image image = getImageDB(idImage);
                            if (image.getUrl() == null || image.getUrl().equals(""))   // If don't exist url
                            new UploadImage().execute(image.getId(), new String(Base64.encode(image.getBytes(), Base64.DEFAULT)));
                        }
                    }
                }
                Toast.makeText(getApplicationContext(), "ok entries upload 200", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getApplicationContext(), "error entries upload", Toast.LENGTH_SHORT).show();
            mProgressDialog.dismiss();

            getEditorPreferences().putString("date_modified", getFormatDB(Calendar.getInstance()));
            getEditorPreferences().commit();
        }
    }

    public class SyncChild extends AsyncTask<String, Void, Boolean> {

        ProgressDialog mProgressDialog;
        String email;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(OptionSettingActivity.this);
            mProgressDialog.setMessage(getString(R.string.setting_progress));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            //mProgressDialog.create();
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();

            Gson gson = new Gson();
            Log.i(TAG, "Upload child: " + gson.toJson(mChildren));

            email = params[0];
            SyncChildren sync = new SyncChildren(email, mChildren);
            //SyncChildren sync = new SyncChildren("hrvoje@flipkod.com", mChildren);

            UserServices service = retrofit.create(UserServices.class);

            Call<Object> call = service.syncChildren(gson.toJson(sync));

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
                Toast.makeText(getApplicationContext(), "ok children upload 200", Toast.LENGTH_SHORT).show();
                new SyncEntries().execute(email);
            } else
                Toast.makeText(getApplicationContext(), "error children upload", Toast.LENGTH_SHORT).show();
            mProgressDialog.dismiss();
            getEditorPreferences().putString("date_modified", getFormatDB(Calendar.getInstance()));
            getEditorPreferences().commit();

        }
    }

    public class UploadImage extends AsyncTask<String, Void, Boolean> {

        private final MediaType JPEG = MediaType.parse("image/jpeg; charset=utf-8");

        @Override
        protected Boolean doInBackground(String... params) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();

            UserServices service = retrofit.create(UserServices.class);

            RequestBody id = RequestBody.create(JPEG, params[0]);
            RequestBody image = RequestBody.create(JPEG, params[1]);

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
                Toast.makeText(getApplicationContext(), "Ok image save", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), "error image save", Toast.LENGTH_SHORT).show();
        }
    }

    public class DownloadImage extends AsyncTask<String, Void, String> {

        private String idDot;
        private String idImage;
        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(OptionSettingActivity.this);
            mProgressDialog.setMessage(getString(R.string.setting_progress));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            //mProgressDialog.create();
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                idImage = params[0];
                idDot = params[1];
                URL url = new URL(API + "user/sync/image?id=" + idImage);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(1500);
                con.setReadTimeout(1500);
                con.connect();
                Log.i("TAG", "new URL " + con.getHeaderField("Location"));
                return con.getHeaderField("Location");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String url) {
            super.onPostExecute(url);
            Image image = new Image();
            image.setId(idImage);
            image.setIdDot(idDot);
            image.setUrl(url);
            mDB.addImage(image, idDot);
            mProgressDialog.dismiss();
        }
    }
}