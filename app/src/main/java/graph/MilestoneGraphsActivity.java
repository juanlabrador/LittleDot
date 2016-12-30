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

import domains.DotCategory;
import domains.DotValues;

/**
 * Created by juanlabrador on 12/10/15.
 */
public class MilestoneGraphsActivity extends ParentFeedActivity {

    private LineChart mChart;
    private List<DotCategory> mMilestoneDots;
    private List<DotValues> mValues;
    private List<Entry> mMilestoneValues;
    private List<String> mDates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphs_growth);
        setLineData();
        init();
    }

    public void setLineData() {
        mMilestoneDots = getDotsByCategory(8);
        mMilestoneValues = new ArrayList<>();
        mDates = new ArrayList<>();
        for (int i = 0; i < mMilestoneDots.size(); i++) {
            mValues = getListDotFromJSONArray(mMilestoneDots.get(i).getEntryData());
            mMilestoneValues.add(new Entry(mValues.get(0).getValue(), mMilestoneDots.size() - i - 1, new String("Hello")));

            mDates.add(0, getFormatDateShort(getParseDB(mMilestoneDots.get(i).getDate())));
        }
    }

    private void init() {
        mChart = (LineChart) findViewById(R.id.chart1);
        mChart.setDescription("Milestone Graphs");
        mChart.setNoDataTextDescription("Need to add milestone entries first.");
        mChart.setDescriptionColor(Color.BLACK);

        mChart.setHighlightEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setPinchZoom(true);
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);

        setData(mMilestoneValues, mDates);

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

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.isForceLabelsEnabled();
        rightAxis.setEnabled(false);
        rightAxis.setDrawGridLines(false);
    }

    private void setData(List<Entry> milestone, List<String> dates) {

        List<String> xVals = dates;
        /*List<String> xVals = new ArrayList<>();
        for (int i = 0; i < weight.size(); i++) {
            xVals.add((i) + "");
        }*/

        List<Entry> yVals1 = milestone;
        /*List<Entry> yVals1 = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            float mult = range / 2f;
            float val = (float) (Math.random() * mult);// + (float)
            // ((mult *
            // 0.1) / 10);
            yVals1.add(new Entry(val, i));
        }*/

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals1, getString(R.string.milestone_feed_name));
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(getResources().getColor(R.color.milestone_text));
        set1.setCircleColor(getResources().getColor(R.color.milestone_text));
        set1.setLineWidth(5f);
        set1.setCircleSize(6f);
        set1.setFillAlpha(65);
        set1.setValueFormatter(new ValueFormatterTextMilestone(this));
        set1.setFillColor(getResources().getColor(R.color.milestone_text));
        set1.setDrawCircleHole(true);
        //set1.setFillFormatter(new MyFillFormatter(0f));
//        set1.setDrawHorizontalHighlightIndicator(false);
//        set1.setVisible(false);
//        set1.setCircleHoleColor(Color.WHITE);


        // create a data object with the datasets
        LineData data = new LineData(xVals, set1);
        data.setValueTextColor(Color.DKGRAY);
        data.setValueTextSize(9f);

        // set data
        mChart.setData(data);
    }
}
