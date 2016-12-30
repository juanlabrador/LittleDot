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

public class GrowthHeightGraphsActivity extends ParentFeedActivity {

    private LineChart mChart;
    private List<DotCategory> mGrowthValues;
    private List<DotValues> mValues;
    private List<Entry> mHeightValues;
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
        mDates = new ArrayList<>();
        for (int i = 0; i < mGrowthValues.size(); i++) {
            mValues = getListDotFromJSONArray(mGrowthValues.get(i).getEntryData());
            if (getAppPreferences().getInt("unit", 0) == 0){
                mHeightValues.add(new Entry(mValues.get(1).getDetailValue(), mGrowthValues.size() - i - 1, new String("Hello")));
            } else {
                mHeightValues.add(new Entry(mConversion.cmToInches((double) mValues.get(1).getDetailValue(), 0), mGrowthValues.size() - i - 1));
            }
            mDates.add(0, getFormatDateShort(getParseDB(mGrowthValues.get(i).getDate())));
        }
    }

    private void init() {
        mChart = (LineChart) findViewById(R.id.chart1);
        mChart.setDescription("Height Growth Graphs");
        mChart.setNoDataTextDescription("Need to add growth entries first.");
        mChart.setDescriptionColor(Color.BLACK);

        mChart.setHighlightEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setPinchZoom(true);
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);

        setData(mHeightValues, mDates);

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

        YAxisValueFormatter customHeight = new FormatterTextHeight(this, getAppPreferences());

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(getResources().getColor(R.color.circle_color_height));
        leftAxis.setAxisMaxValue(215f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setTextColor(getResources().getColor(R.color.circle_color_height));
        rightAxis.setAxisMaxValue(215f);
        rightAxis.setValueFormatter(customHeight);
        rightAxis.setDrawGridLines(false);
    }

    private void setData(List<Entry> height, List<String> dates) {

        List<String> xVals = dates;
        List<Entry> yVals2 = height;

        LineDataSet set1 = new LineDataSet(yVals2, getString(R.string.growth_height));
        set1.setAxisDependency(YAxis.AxisDependency.RIGHT);
        set1.setColor(getResources().getColor(R.color.circle_color_height));
        set1.setCircleColor(getResources().getColor(R.color.circle_color_height));
        set1.setLineWidth(5f);
        set1.setCircleSize(6f);
        set1.setFillAlpha(65);
        set1.setFillColor(getResources().getColor(R.color.circle_color_height));
        set1.setDrawCircleHole(true);
        set1.setHighlightEnabled(false);
        set1.setHighLightColor(getResources().getColor(R.color.line_color_height));
        //set2.setFillFormatter(new MyFillFormatter(900f));

        ArrayList<LineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);
        data.setValueTextColor(Color.DKGRAY);
        data.setValueTextSize(9f);

        // set data
        mChart.setData(data);
    }
}
