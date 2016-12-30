package com.littledot.mystxx.littledot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by juanlabrador on 18/08/15.
 */
public class MilestoneFeedActivity extends BaseFeedActivity {

    private static int CATEGORY = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeNotificationBarColor(R.color.milestone_text);

        updateDataEvery(null, CATEGORY);

        createFeedView(
                CATEGORY,
                R.string.milestone_feed_content_list_empty, -1);

        colorFeedView(
                R.mipmap.icn_milestones,
                R.string.milestone_feed_name,
                R.mipmap.icn_add_milestones,
                R.color.milestone_text,
                R.style.MilestoneStyleDialog,
                R.drawable.tinted_icon_filter_time_milestone);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.feed_add_item:
                startActivity(new Intent(this, MilestoneAddEntryActivity.class));
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
