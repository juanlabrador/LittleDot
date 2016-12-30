package services;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.littledot.mystxx.littledot.ParentFeedActivity;
import com.littledot.mystxx.littledot.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import database.DataBaseHandler;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

/**
 * Created by juanlabrador on 09/10/15.
 */
public class InternetReceiver extends BroadcastReceiver {

    public final static String API = " http://littledotapp.com/api/";
    private static String TAG = "InternetReceiver";
    private Context mContext;
    private DataBaseHandler mDB;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        mDB = new DataBaseHandler(context);

        // If already have internet
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetInfo != null && activeNetInfo.isConnected();
        if (isConnected) {
            Log.i(TAG, "is connected?" + isConnected);
            List<String> idsChildren = mDB.getAllPendingChildren();
            if (idsChildren.size() > 0)
                new SyncDeleteChildren().execute(idsChildren);

            List<String> idsDots = mDB.getAllPendingDots();
            if (idsDots.size() > 0)
                new SyncDeleteEntries().execute(idsDots);

        } else Log.i(TAG, "is connected? " +isConnected);
    }

    public class SyncDeleteChildren extends AsyncTask<List<String>, Void, Boolean> {

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
                Toast.makeText(mContext, "ok 200 delete children from receiver", Toast.LENGTH_SHORT).show();
                mDB.deletePendingChildren();
            } else
                Toast.makeText(mContext, "error delete children from receiver", Toast.LENGTH_SHORT).show();
        }
    }

    public class SyncDeleteEntries extends AsyncTask<List<String>, Void, Boolean> {

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
                Toast.makeText(mContext, "ok 200 delete dots from receiver", Toast.LENGTH_SHORT).show();
                mDB.deletePendingDots();
            } else
                Toast.makeText(mContext, "error delete dots from receiver", Toast.LENGTH_SHORT).show();
        }
    }
}
