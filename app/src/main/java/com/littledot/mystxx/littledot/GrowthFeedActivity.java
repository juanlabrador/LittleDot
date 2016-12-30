package com.littledot.mystxx.littledot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import graph.GrowthGraphsActivity;
import graph.MilestoneGraphsActivity;

/**
 * Created by juanlabrador on 18/08/15.
 */
public class GrowthFeedActivity extends BaseFeedActivity {

    private static int CATEGORY = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeNotificationBarColor(R.color.growth_text);

        updateDataEvery(null, CATEGORY);

        createCustomFeed(
                CATEGORY,
                R.string.growth_feed_content_list_empty,
                getResources().getStringArray(R.array.growth_menu_list),
                R.color.growth_background);

        colorFeedView(
                R.mipmap.icn_growth,
                R.string.growth_feed_name,
                R.mipmap.icn_add_growth,
                R.color.growth_text,
                R.style.GrowthStyleDialog,
                R.drawable.tinted_icon_filter_time_growth);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.feed_add_item:
                startActivity(new Intent(this, GrowthAddEntryActivity.class));
                overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                break;
            case R.id.menu_1:
                startActivity(new Intent(this, GrowthCurveActivity.class));
                overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                break;
            case R.id.menu_2:
                startActivity(new Intent(this, GrowthBMIActivity.class));
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
