package graph;

import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BubbleChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.BubbleDataSet;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.littledot.mystxx.littledot.ParentFeedActivity;
import com.littledot.mystxx.littledot.R;

import java.util.ArrayList;
import java.util.List;

import domains.DotCategory;
import domains.DotValues;

public class BubbleChartSampleActivity extends ParentFeedActivity {
    BubbleChart mChart;
    List<DotCategory> growthValues;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubblechart);
        init();
    }

    public void init(){
        mChart = (BubbleChart) findViewById(R.id.chart);

        mChart.setDescription("");

        mChart.setDrawGridBackground(false);

        mChart.setTouchEnabled(true);
        mChart.setHighlightEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);

        mChart.setMaxVisibleValueCount(200);
        mChart.setPinchZoom(true);

        mChart.getAxisLeft().setStartAtZero(false);
        mChart.getAxisRight().setStartAtZero(false);


        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);

        YAxis yl = mChart.getAxisLeft();
        yl.setSpaceTop(30f);
        yl.setStartAtZero(false);
        yl.setSpaceBottom(30f);

        mChart.getAxisRight().setEnabled(false);

        XAxis xl = mChart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);

        setValue();
    }

    public void setValue(){
        growthValues = getDotsByCategory(8);

/*        for (int counter2 = mGrowthValues.size() - 1, counter = 0; counter2 >= 0; counter2--) {
            List<DotValues> dotValues = getListDotFromJSONArray(mGrowthValues.get(counter2).getEntryData());
            labelsArray[counter] = (counter + 1) + "";
            valuesArray[0][counter] = dotValues.get(0).getDetailValue();
            valuesArray[1][counter] = dotValues.get(1).getDetailValue();
            counter++;
        }*/

        ArrayList<String> xAxisValues = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            xAxisValues.add((i) + "");
        }

        ArrayList<BubbleEntry> yVals1 = new ArrayList<>();
        ArrayList<BubbleEntry> yVals2 = new ArrayList<>();
        ArrayList<BubbleEntry> yVals3 = new ArrayList<>();

        yVals1.add(new BubbleEntry(0, 1, 1));
        yVals2.add(new BubbleEntry(2, 2, 2));
        yVals2.add(new BubbleEntry(4, 4, 4));
        yVals3.add(new BubbleEntry(3, 3, 3));

        // create a dataset and give it a type
        BubbleDataSet set1 = new BubbleDataSet(yVals1, "Walking");
        set1.setColor(ColorTemplate.COLORFUL_COLORS[0], 130);
        set1.setDrawValues(true);
        BubbleDataSet set2 = new BubbleDataSet(yVals2, "Laughing");
        set2.setColor(ColorTemplate.COLORFUL_COLORS[1], 130);
        set2.setDrawValues(true);
        BubbleDataSet set3 = new BubbleDataSet(yVals3, "Standing");
        set3.setColor(ColorTemplate.COLORFUL_COLORS[2], 130);
        set3.setDrawValues(true);

        ArrayList<BubbleDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the datasets
        dataSets.add(set2);
        dataSets.add(set3);

        // create a data object with the datasets
        BubbleData data = new BubbleData(xAxisValues, dataSets);
        data.setValueTextSize(8f);
        data.setValueTextColor(Color.WHITE);
        data.setHighlightCircleWidth(1.5f);

        mChart.setData(data);
        mChart.invalidate();
    }
}
