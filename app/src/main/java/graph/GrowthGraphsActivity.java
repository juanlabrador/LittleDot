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
public class GrowthGraphsActivity extends ParentFeedActivity {

    private LineChart mChart;
    private List<DotCategory> mGrowthValues;
    private List<DotValues> mValues;
    private List<Entry> mHeightValues;
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
        mHeightValues = new ArrayList<>();
        mWeightValues = new ArrayList<>();
        mDates = new ArrayList<>();
        for (int i = 0; i < mGrowthValues.size(); i++) {
            mValues = getListDotFromJSONArray(mGrowthValues.get(i).getEntryData());
            if (getAppPreferences().getInt("unit", 0) == 0){
                mHeightValues.add(new Entry(mValues.get(1).getDetailValue(), mGrowthValues.size() - i - 1, new String("Hello")));
                mWeightValues.add(new Entry(mValues.get(0).getDetailValue(), mGrowthValues.size() - i - 1));
            } else {
                mHeightValues.add(new Entry(mConversion.cmToInches((double) mValues.get(1).getDetailValue(), 0), mGrowthValues.size() - i - 1));
                mWeightValues.add(new Entry(mConversion.kgToPounds((double) mValues.get(0).getDetailValue(), 0), mGrowthValues.size() - i - 1));
            }
            mDates.add(0, getFormatDateShort(getParseDB(mGrowthValues.get(i).getDate())));
        }
    }

    private void init() {
        mChart = (LineChart) findViewById(R.id.chart1);
        mChart.setDescription("Growth Graphs");
        mChart.setNoDataTextDescription("Need to add growth entries first.");
        mChart.setDescriptionColor(Color.BLACK);

        mChart.setHighlightEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setPinchZoom(true);
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);

        setData(mWeightValues, mHeightValues, mDates);

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
        YAxisValueFormatter customHeight = new FormatterTextHeight(this, getAppPreferences());

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(getResources().getColor(R.color.circle_color_weight));
        leftAxis.setAxisMaxValue(110f);
        leftAxis.setValueFormatter(customWeight);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setTextColor(getResources().getColor(R.color.circle_color_height));
        rightAxis.setAxisMaxValue(215f);
        rightAxis.setValueFormatter(customHeight);
        rightAxis.setDrawGridLines(false);
    }

    private void setData(List<Entry> weight, List<Entry> height, List<String> dates) {

        List<String> xVals = dates;
        /*List<String> xVals = new ArrayList<>();
        for (int i = 0; i < weight.size(); i++) {
            xVals.add((i) + "");
        }*/

        List<Entry> yVals1 = weight;
        /*List<Entry> yVals1 = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            float mult = range / 2f;
            float val = (float) (Math.random() * mult);// + (float)
            // ((mult *
            // 0.1) / 10);
            yVals1.add(new Entry(val, i));
        }*/

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
        //set1.setFillFormatter(new MyFillFormatter(0f));
//        set1.setDrawHorizontalHighlightIndicator(false);
//        set1.setVisible(false);
//        set1.setCircleHoleColor(Color.WHITE);

        List<Entry> yVals2 = height;
        /*ArrayList<Entry> yVals2 = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            float mult = range;
            float val = (float) (Math.random() * mult);// + (float)
            // ((mult *
            // 0.1) / 10);
            yVals2.add(new Entry(val, i));
        }*/

        // create a dataset and give it a type
        LineDataSet set2 = new LineDataSet(yVals2, getString(R.string.growth_height));
        set2.setAxisDependency(YAxis.AxisDependency.RIGHT);
        set2.setColor(getResources().getColor(R.color.circle_color_height));
        set2.setCircleColor(getResources().getColor(R.color.circle_color_height));
        set2.setLineWidth(5f);
        set2.setCircleSize(6f);
        set2.setFillAlpha(65);
        set2.setFillColor(getResources().getColor(R.color.circle_color_height));
        set2.setDrawCircleHole(true);
        set2.setHighlightEnabled(false);
        set2.setHighLightColor(getResources().getColor(R.color.line_color_height));
        //set2.setFillFormatter(new MyFillFormatter(900f));

        ArrayList<LineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the datasets
        dataSets.add(set2);

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);
        data.setValueTextColor(Color.DKGRAY);
        data.setValueTextSize(9f);

        // set data
        mChart.setData(data);
    }
}
