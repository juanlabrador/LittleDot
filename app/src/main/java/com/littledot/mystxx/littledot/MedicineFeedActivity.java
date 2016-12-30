package com.littledot.mystxx.littledot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


/**
 * Created by juanlabrador on 18/08/15.
 */
public class MedicineFeedActivity extends BaseFeedActivity {

    private static int CATEGORY = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeNotificationBarColor(R.color.medicine_text);

        updateDataEvery(null, CATEGORY);

        createFeedView(
                CATEGORY,
                R.string.medicine_feed_content_list_empty, -1);

        colorFeedView(
                R.mipmap.icn_medicines,
                R.string.medicine_feed_name,
                R.mipmap.icn_add_medicines,
                R.color.medicine_text,
                R.style.MedicineStyleDialog,
                R.drawable.tinted_icon_filter_time_medicine);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.feed_add_item:
                startActivity(new Intent(this, MedicineAddEntryActivity.class));
                overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDataEvery(null, CATEGORY);
    }
}
