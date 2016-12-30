package com.littledot.mystxx.littledot;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by juanlabrador on 12/08/15.
 */
public class DoctorEditActivity extends BaseFeedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createDoctorEdit();
        navigationToolbarWithTitle(R.string.edit_doctor);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.save_entry) {
            changeDataDoctor();
            overridePendingTransition(R.anim.anim_nothing, R.anim.anim_out);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
