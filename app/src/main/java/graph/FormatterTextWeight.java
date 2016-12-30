package graph;

import android.content.Context;
import android.content.SharedPreferences;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.littledot.mystxx.littledot.R;

/**
 * Created by juanlabrador on 12/10/15.
 */
public class FormatterTextWeight implements YAxisValueFormatter {

    private SharedPreferences preferences;
    private Context context;

    public FormatterTextWeight(Context context, SharedPreferences preferences) {
        this.preferences = preferences;
        this.context = context;
    }

    @Override
    public String getFormattedValue(float value, YAxis yAxis) {
        if (preferences.getInt("unit", 0) == 0){
            return (int) value + "kg";
        } else {
            return (int) value + "lb";
        }
    }
}
