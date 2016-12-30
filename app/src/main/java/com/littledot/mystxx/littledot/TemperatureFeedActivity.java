package com.littledot.mystxx.littledot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by juanlabrador on 18/08/15.
 */
public class TemperatureFeedActivity extends BaseFeedActivity {

    private static int CATEGORY = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeNotificationBarColor(R.color.temperature_text);

        updateDataEvery(null, CATEGORY);

        createCustomFeed(
                CATEGORY,
                R.string.temperature_feed_content_list_empty,
                getResources().getStringArray(R.array.temperature_menu_list),
                R.color.temperature_background);

        colorFeedView(
                R.mipmap.icn_temperature,
                R.string.temperature_feed_name,
                R.mipmap.icn_add_temperature,
                R.color.temperature_text,
                R.style.TemperatureStyleDialog,
                R.drawable.tinted_icon_filter_time_temperature
        );

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.feed_add_item:
                startActivity(new Intent(this, TemperatureAddEntryActivity.class));
                overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                break;
            case R.id.menu_1:
                showSimpleDialog(
                        R.string.temperature_title_info,
                        R.layout.custom_temperature_advices,
                        R.style.TemperatureStyleDialog);
                break;
            case R.id.menu_2:
                showListOptionDialog(
                        R.string.temperature_menu_2,
                        getResources().getStringArray(R.array.temperature_doses),
                        R.style.TemperatureStyleDialog);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDataEvery(null, CATEGORY);
    }
}
