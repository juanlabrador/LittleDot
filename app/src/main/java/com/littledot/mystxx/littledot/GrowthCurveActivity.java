package com.littledot.mystxx.littledot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import graph.GrowthGraphsActivity;
import graph.GrowthHeightGraphsActivity;
import graph.GrowthWeightGraphsActivity;

public class GrowthCurveActivity extends ParentFeedActivity {

    TextView weight, weightValue, height, heightValue, head, headValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_growth_curve);
        init();
    }

    private void init() {
        weight = (TextView) findViewById(R.id.growth_curve_weight);
        weightValue = (TextView) findViewById(R.id.growth_curve_weight_value);

        height = (TextView) findViewById(R.id.growth_curve_height);
        heightValue = (TextView) findViewById(R.id.growth_curve_height_value);

        head = (TextView) findViewById(R.id.growth_curve_head);
        headValue = (TextView) findViewById(R.id.growth_curve_head_value);

        weight.setOnClickListener(this);
        height.setOnClickListener(this);
        head.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.growth_curve_weight:
                startActivity(new Intent(this, GrowthWeightGraphsActivity.class));
                overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                break;
            case R.id.growth_curve_height:
                startActivity(new Intent(this, GrowthHeightGraphsActivity.class));
                overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                break;
            case R.id.growth_curve_head:
                startActivity(new Intent(this, GrowthGraphsActivity.class));
                overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                break;
        }
    }
}
