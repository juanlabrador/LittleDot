package com.littledot.mystxx.littledot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
/**
 * Created by CharlesSio on 19/08/15.
 */

public class SleepingFeedActivity extends BaseFeedActivity {

    private static int CATEGORY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeNotificationBarColor(R.color.sleeping_text);

        updateDataEvery(null, CATEGORY);

        createFeedView(
                CATEGORY,
                R.string.sleeping_feed_content_list_empty, -1);

        colorFeedView(
                R.mipmap.icn_sleeping,
                R.string.sleeping_feed_name,
                R.mipmap.icn_add_sleeping,
                R.color.sleeping_text,
                R.style.SleepingStyleDialog,
                R.drawable.tinted_icon_filter_time_sleeping);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.feed_add_item:
                startActivity(new Intent(this, SleepingAddEntryActivity.class));
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
