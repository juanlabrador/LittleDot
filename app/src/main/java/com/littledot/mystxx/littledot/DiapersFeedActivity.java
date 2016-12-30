package com.littledot.mystxx.littledot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


/**
 * Created by juanlabrador on 18/08/15.
 */
public class DiapersFeedActivity extends BaseFeedActivity {

    private static int CATEGORY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeNotificationBarColor(R.color.diapers_text);
        updateDataEvery(null, CATEGORY);

        createFeedView(
                CATEGORY,
                R.string.diapers_feed_content_list_empty, -1);

        colorFeedView(
                R.mipmap.icn_diapers,
                R.string.diapers_feed_name,
                R.mipmap.icn_add_diapers,
                R.color.diapers_text,
                R.style.DiapersStyleDialog,
                R.drawable.tinted_icon_filter_time_diapers);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.feed_add_item:
                startActivity(new Intent(this, DiapersAddEntryActivity.class));
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
