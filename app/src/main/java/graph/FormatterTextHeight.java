package graph;

import android.content.Context;
import android.content.SharedPreferences;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.littledot.mystxx.littledot.R;

/**
 * Created by juanlabrador on 12/10/15.
 */
public class FormatterTextHeight implements YAxisValueFormatter {

    private Context mContext;
    private SharedPreferences preferences;

    public FormatterTextHeight(Context context, SharedPreferences preferences) {
        mContext = context;
        this.preferences = preferences;
    }

    @Override
    public String getFormattedValue(float value, YAxis yAxis) {
        if (preferences.getInt("unit", 0) == 0){
            return (int) value + "cm";
        } else {
            return (int) value + "in";
        }
    }
}
