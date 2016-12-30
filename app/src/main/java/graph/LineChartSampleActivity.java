package graph;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.littledot.mystxx.littledot.ParentFeedActivity;
import com.littledot.mystxx.littledot.R;

import java.util.List;

import domains.DotCategory;
import domains.DotValues;

public class LineChartSampleActivity extends ParentFeedActivity {
    private String[] labelsArray;
    private float[][] valuesArray;
    LineChartView chart;
    List<DotCategory> growthValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linechart);
        init();
    }

    public void init() {
        chart = (LineChartView) findViewById(R.id.linechart1);
        setLineData();
        produceChart(chart);
    }

    public void setLineData() {
        growthValues = getDotsByCategory(6);

        valuesArray = new float[3][];
        valuesArray[0] = new float[growthValues.size()];
        valuesArray[1] = new float[growthValues.size()];
        labelsArray = new String[growthValues.size()];

        for (int counter2 = growthValues.size() - 1, counter = 0; counter2 >= 0; counter2--) {
            List<DotValues> dotValues = getListDotFromJSONArray(growthValues.get(counter2).getEntryData());
            labelsArray[counter] = (counter + 1) + "";
            valuesArray[0][counter] = dotValues.get(0).getDetailValue();
            valuesArray[1][counter] = dotValues.get(1).getDetailValue();
            counter++;
        }
    }

    public void produceChart(LineChartView chart) {
        LineSet dataset = new LineSet(labelsArray, valuesArray[0]);
        dataset.setColor(Color.parseColor("#FF7C44"))
                .setDotsStrokeThickness(Tools.fromDpToPx(2))
                .setDotsStrokeColor(Color.parseColor("#FF7C44"))
                .setDotsColor(Color.WHITE);
        chart.addData(dataset);


        dataset = new LineSet(labelsArray, valuesArray[1]);
        dataset.setColor(Color.parseColor("#00A3D9"))
                .setDotsStrokeThickness(Tools.fromDpToPx(2))
                .setDotsStrokeColor(Color.parseColor("#00A3D9"))
                .setDotsColor(Color.WHITE);
        chart.addData(dataset);

      /*  dataset = new LineSet(labelsArray, valuesArray[2]);
        dataset.setColor(Color.parseColor("#FF365EAF"))
                .setDotsStrokeThickness(Tools.fromDpToPx(2))
                .setDotsStrokeColor(Color.parseColor("#FF365EAF"))
                .setDotsColor(Color.parseColor("#eef1f6"));
        chart.addData(dataset);
*/
        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#308E9196"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(Tools.fromDpToPx(1f));

        chart.setBorderSpacing(1)
                .setAxisBorderValues(0, 215, 5)
                .setXLabels(AxisController.LabelPosition.OUTSIDE)
                .setYLabels(AxisController.LabelPosition.OUTSIDE)
                .setLabelsColor(Color.parseColor("#666666"))
                .setXAxis(true)
                .setYAxis(true)
                .setStep(5)
                .setBorderSpacing(Tools.fromDpToPx(5))
                .setGrid(ChartView.GridType.FULL, gridPaint)
                .setBackgroundColor(Color.WHITE);
        chart.setHorizontalScrollBarEnabled(true);
        chart.setVerticalScrollBarEnabled(true);
        chart.canScrollHorizontally(3);
        chart.canScrollVertically(3);
        chart.show();
    }

}
