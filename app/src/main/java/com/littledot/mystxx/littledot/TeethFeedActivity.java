package com.littledot.mystxx.littledot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by juanlabrador on 18/08/15.
 */
public class TeethFeedActivity extends BaseFeedActivity {

    private static int CATEGORY = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeNotificationBarColor(R.color.teeth_text);

        updateDataEvery(null, CATEGORY);

        createFeedView(
                CATEGORY,
                R.string.teeth_feed_content_list_empty, -1);

        colorFeedView(
                R.mipmap.icn_teeth,
                R.string.teeth_feed_name,
                R.mipmap.icn_add_teeth,
                R.color.teeth_text,
                R.style.TeethStyleDialog,
                R.drawable.tinted_icon_filter_time_teeth);

    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.feed_add_item:
                startActivity(new Intent(this, TeethAddEntryActivity.class));
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
