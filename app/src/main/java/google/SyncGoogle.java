package google;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.littledot.mystxx.littledot.ParentFeedActivity;
import com.littledot.mystxx.littledot.R;

import java.util.UUID;

/**
 * Created by juanlabrador on 16/10/15.
 */
public class SyncGoogle extends ParentFeedActivity implements ConnectionCallbacks, OnConnectionFailedListener {

    private static final String TAG = "android-google";
    private static final int SIGN_IN = 0;

    private GoogleApiClient mGoogleApiClient;
    private AppCompatActivity mActivity;
    private Button mButtonIn;
    private Button mButtonOut;
    private ProgressDialog mProgress;
    private AlertDialog.Builder mDialog;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    public SyncGoogle(AppCompatActivity activity, Button signin, Button signout, SharedPreferences appPreferences, SharedPreferences.Editor editorPreferences) {
        mActivity = activity;
        mButtonIn = signin;
        mButtonOut = signout;
        mPreferences = appPreferences;
        mEditor = editorPreferences;
    }

    public void showDialog() {
        mDialog = new AlertDialog.Builder(mActivity);
        mDialog.setMessage("Are you sure sign out with google? If you sign out, can't sync with LittleDot services.");
        mDialog.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                signOutGoogle();
            }
        });
        mDialog.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mDialog.create();
        mDialog.show();
    }

    private void showProgress() {
        mProgress = new ProgressDialog(mActivity);
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
        mProgress.setMessage("Wait please...");
        mProgress.show();
    }

    public void disconnect() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    public void signInGoogle() {
        if (mGoogleApiClient == null) {

            mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                    .addApi(Plus.API)
                    .addScope(Plus.SCOPE_PLUS_LOGIN)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    public boolean isConnected() {
        if (mGoogleApiClient != null) {
            return mGoogleApiClient.isConnected();
        } else {
            return false;
        }
    }

    /**
     * Sign-out from google
     * */
    public void signOutGoogle() {
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                mGoogleApiClient.disconnect();
                updateUI(false);
            }
        } else {
            updateUI(false);
        }
        mEditor.putString("email_user", "");
        mEditor.commit();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Called whenever the API client fails to signInGoogle.
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), mActivity, 0).show();
            return;
        }

        try {
            result.startResolutionForResult(mActivity, SIGN_IN);
            showProgress();
        } catch (SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == SIGN_IN) {
            if (responseCode != RESULT_OK) {
                if (mProgress != null)
                    mProgress.dismiss();
                Log.i(TAG, "result canceled!");
                return;
            }

            Log.i(TAG, "result ok!");
            if (!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "API client connected.");
        Toast.makeText(mActivity, "User is connected!", Toast.LENGTH_LONG).show();
        if (mProgress != null)
            mProgress.dismiss();
        // Get user's information
        getProfileInformation();

        // Update the UI after signIn
        updateUI(true);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
        if (mProgress != null)
            mProgress.dismiss();
    }

    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            mButtonOut.setVisibility(View.VISIBLE);
            mButtonIn.setVisibility(View.GONE);
        } else {
            mButtonIn.setVisibility(View.VISIBLE);
            mButtonOut.setVisibility(View.GONE);
        }
    }

    /**
     * Fetching user's information name, email, profile pic
     * */
    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personGooglePlusProfile = currentPerson.getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                Log.e(TAG, "Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + email);

                mEditor.putString("email_user", email);
                mEditor.putString("id_user", UUID.randomUUID().toString().toUpperCase());
                mEditor.commit();

                Log.i(TAG, "ID: " + mPreferences.getString("id_user", ""));

            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}