package com.littledot.mystxx.littledot;

import android.os.Bundle;
import android.widget.TextView;

public class GrowthBMIActivity extends ParentFeedActivity {

    TextView bmiValue, bmiLabel, bmiDetails, bmiHeight, bmiWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_growth_bmi);
        init();
    }

    private void init() {
        bmiValue = (TextView) findViewById(R.id.growth_bmi_value);
        bmiLabel = (TextView) findViewById(R.id.growth_bmi_value_label);

        bmiDetails = (TextView) findViewById(R.id.growth_bmi_details);
        bmiHeight = (TextView) findViewById(R.id.growth_bmi_height_value);
        bmiWeight = (TextView) findViewById(R.id.growth_bmi_weight_value);
    }
}
