package graph;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.littledot.mystxx.littledot.R;
import java.util.ArrayList;

public class BarChartSampleActivity extends AppCompatActivity {
    HorizontalBarChart mChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barchart);
        init();
    }

    public void init(){
        mChart = (HorizontalBarChart) findViewById(R.id.chart);

        mChart.setDescription("");

        mChart.setMaxVisibleValueCount(60);
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);

        mChart.setDrawValueAboveBar(false);

        // change the position of the y-labels
        YAxis yLabels = mChart.getAxisLeft();
        yLabels.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

        XAxis xLabels = mChart.getXAxis();
        xLabels.setPosition(XAxis.XAxisPosition.TOP);


        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_RIGHT);
        l.setFormSize(8f);
        l.setFormToTextSpace(4f);
        l.setXEntrySpace(6f);

        setValue();
    }

    public void setValue(){
        ArrayList<BarEntry> yVals1 = new ArrayList<>();

        for (int i = 0; i < 4 + 1; i++) {
            float mult = (4 + 1);
            float val1 = (float) (Math.random() * mult) + mult / 3;
            float val2 = (float) (Math.random() * mult) + mult / 3;
            float val3 = (float) (Math.random() * mult) + mult / 3;

            yVals1.add(new BarEntry(new float[] { val1, val2, val3 }, i));
        }

        BarDataSet set1 = new BarDataSet(yVals1, "Sample Set");
        set1.setColors(getColors());
        set1.setStackLabels(new String[]{"Sample 1", "Sample 2", "Sample 3"});

        ArrayList<BarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(getXAxisValues(), dataSets);
        mChart.setData(data);
        mChart.invalidate();
    }

    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("JAN");
        xAxis.add("FEB");
        xAxis.add("MAR");
        xAxis.add("APR");
        xAxis.add("MAY");
        xAxis.add("JUN");
        return xAxis;
    }

    private int[] getColors() {

        int stacksize = 3;
        int[] colors = new int[stacksize];

        for (int i = 0; i < stacksize; i++) {
            colors[i] = ColorTemplate.VORDIPLOM_COLORS[i];
        }

        return colors;
    }
}
