package graph;

import android.content.Context;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.littledot.mystxx.littledot.R;

/**
 * Created by juanlabrador on 12/10/15.
 */
public class ValueFormatterTextMilestone implements ValueFormatter {

    private String[] milestoneContent;

    public ValueFormatterTextMilestone(Context context) {
        milestoneContent = context.getResources().getStringArray(R.array.milestone_list);
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        /*String[] words = milestoneContent[((int) value)].split(" +");
        if (words.length > 8) {
            int lineUp = words.length/2;
            StringBuilder phrase = new StringBuilder();
            for (int i = 0; i < lineUp; i++) {
                phrase.append(words[i] + " ");
            }
            phrase.append("\n");
            for (int i = lineUp; i < words.length; i++) {
                phrase.append(words[i] + " ");
            }

            return phrase.toString();
        } else {*/
            return milestoneContent[((int) value)];
        //}
    }
}
