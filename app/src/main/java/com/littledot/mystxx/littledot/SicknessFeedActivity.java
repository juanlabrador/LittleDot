package com.littledot.mystxx.littledot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SicknessFeedActivity extends BaseFeedActivity {

    private static int CATEGORY = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeNotificationBarColor(R.color.sickness_text);

        updateDataEvery(null, CATEGORY);

        createFeedView(
                CATEGORY,
                R.string.sickness_feed_content_list_empty, -1);

        colorFeedView(
                R.mipmap.icn_sickness,
                R.string.sickness_feed_name,
                R.mipmap.icn_add_sickness,
                R.color.sickness_text,
                R.style.SicknessStyleDialog,
                R.drawable.tinted_icon_filter_time_sickness);

    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.feed_add_item:
                startActivity(new Intent(this, SicknessAddEntryActivity.class));
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
