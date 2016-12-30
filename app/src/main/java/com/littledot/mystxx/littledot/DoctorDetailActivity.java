package com.littledot.mystxx.littledot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by juanlabrador on 11/08/15.
 */
public class DoctorDetailActivity extends BaseFeedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createDoctorView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDataDoctor();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.address_doctor:
                Intent intent = new Intent(this,
                        DoctorAddressMapActivity.class);
                intent.putExtra("address", mDoctorAddress.getText().toString());
                startActivity(intent);
                overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                break;
        }
    }
}
