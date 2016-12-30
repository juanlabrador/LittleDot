package com.littledot.mystxx.littledot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by CharlesSio on 19/08/15.
 */
public class FeedingFeedActivity extends BaseFeedActivity {

    private static int CATEGORY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateDataEvery(null, CATEGORY);
        changeNotificationBarColor(R.color.feeding_text);

        createFeedView(
                CATEGORY,
                R.string.feeding_feed_content_list_empty, -1);

        colorFeedView(
                R.mipmap.icn_feeding,
                R.string.feeding_feed_name,
                R.mipmap.icn_add_feeding,
                R.color.feeding_text,
                R.style.FeedingStyleDialog,
                R.drawable.tinted_icon_filter_time_feeding);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.feed_add_item:
                startActivity(new Intent(this, FeedingAddEntryActivity.class));
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
