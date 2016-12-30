package com.littledot.mystxx.littledot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by CharlesSio on 19/08/15.
 */

public class OthersFeedActivity extends BaseFeedActivity {

    private static int CATEGORY = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeNotificationBarColor(R.color.other_text);

        updateDataEvery(null, CATEGORY);

        createFeedView(
                CATEGORY,
                R.string.other_feed_content_list_empty, -1);

        colorFeedView(
                R.mipmap.icn_other,
                R.string.other_feed_name,
                R.mipmap.icn_add_other,
                R.color.other_text,
                R.style.OtherStyleDialog,
                R.drawable.tinted_icon_filter_time_others);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.feed_add_item:
                startActivity(new Intent(this, OthersAddEntryActivity.class));
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
