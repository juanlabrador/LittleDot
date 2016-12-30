package com.littledot.mystxx.littledot;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class DoctorAddressMapActivity extends FragmentActivity {

    private GoogleMap mMap;
    private MarkerOptions mMarker;
    private Geocoder mGeoCoder;
    private CameraPosition mCameraPosition;
    private double mLatitude, mLongitude;
    private String mDoctorAddress = "";
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_maps);
        setupMap();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupMap();
    }

    private void setupMap() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (mMap != null) {
                providePins();
            }
        }
    }

    private void providePins() {

        mIntent = getIntent();
        mDoctorAddress = mIntent.getExtras().getString("address");

        mMap.clear();

        mGeoCoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = mGeoCoder.getFromLocationName(
                    mDoctorAddress, 5);
            if (addresses.size() > 0) {
                mLatitude = (addresses.get(0).getLatitude());
                mLongitude = (addresses.get(0).getLongitude());
            }

            mMarker = new MarkerOptions()
                    .position(new LatLng(mLatitude, mLongitude))
                    .title(mDoctorAddress).icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

            mMap.addMarker(mMarker);

            mCameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(mLatitude, mLongitude)).zoom(6)
                    .build();

            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(mCameraPosition));
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
