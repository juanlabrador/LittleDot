package graph;

import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.littledot.mystxx.littledot.ParentFeedActivity;
import com.littledot.mystxx.littledot.R;

import java.util.ArrayList;
import java.util.List;

import domains.Conversion;
import domains.DotCategory;
import domains.DotValues;

/**
 * Created by juanlabrador on 12/10/15.
 */
public class GrowthWeightGraphsActivity extends ParentFeedActivity {

    private LineChart mChart;
    private List<DotCategory> mGrowthValues;
    private List<DotValues> mValues;
    private List<Entry> mWeightValues;
    private List<String> mDates;
    private Conversion mConversion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphs_growth);
        mConversion = new Conversion();
        setLineData();
        init();
    }

    public void setLineData() {
        mGrowthValues = getDotsByCategory(6);
        mWeightValues = new ArrayList<>();
        mDates = new ArrayList<>();
        for (int i = 0; i < mGrowthValues.size(); i++) {
            mValues = getListDotFromJSONArray(mGrowthValues.get(i).getEntryData());
            if (getAppPreferences().getInt("unit", 0) == 0){
                mWeightValues.add(new Entry(mValues.get(0).getDetailValue(), mGrowthValues.size() - i - 1));
            } else {
               mWeightValues.add(new Entry(mConversion.kgToPounds((double) mValues.get(0).getDetailValue(), 0), mGrowthValues.size() - i - 1));
            }
            mDates.add(0, getFormatDateShort(getParseDB(mGrowthValues.get(i).getDate())));
        }
    }

    private void init() {
        mChart = (LineChart) findViewById(R.id.chart1);
        mChart.setDescription("Weight Growth Graphs");
        mChart.setNoDataTextDescription("Need to add growth entries first.");
        mChart.setDescriptionColor(Color.BLACK);

        mChart.setHighlightEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setPinchZoom(true);
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);

        setData(mWeightValues, mDates);

        Legend mLegend = mChart.getLegend();
        mLegend.setForm(Legend.LegendForm.SQUARE);
        mLegend.setTextSize(11f);
        mLegend.setTextColor(Color.DKGRAY);
        mLegend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.DKGRAY);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);

        YAxisValueFormatter customWeight = new FormatterTextWeight(this, getAppPreferences());

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(getResources().getColor(R.color.circle_color_weight));
        leftAxis.setAxisMaxValue(110f);
        leftAxis.setValueFormatter(customWeight);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setTextColor(getResources().getColor(R.color.circle_color_weight));
        rightAxis.setAxisMaxValue(110f);
        rightAxis.setValueFormatter(customWeight);
        rightAxis.setDrawGridLines(false);

    }

    private void setData(List<Entry> weight, List<String> dates) {

        List<String> xVals = dates;
        List<Entry> yVals1 = weight;
        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals1, getString(R.string.growth_weight));
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(getResources().getColor(R.color.circle_color_weight));
        set1.setCircleColor(getResources().getColor(R.color.circle_color_weight));
        set1.setLineWidth(5f);
        set1.setCircleSize(6f);
        set1.setFillAlpha(65);
        set1.setFillColor(getResources().getColor(R.color.circle_color_weight));
        set1.setHighlightEnabled(true);
        set1.setHighLightColor(getResources().getColor(R.color.line_color_weight));
        set1.setDrawCircleHole(true);

        ArrayList<LineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);
        data.setValueTextColor(Color.DKGRAY);
        data.setValueTextSize(9f);

        // set data
        mChart.setData(data);
    }
}
