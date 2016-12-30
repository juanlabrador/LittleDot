package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.littledot.mystxx.littledot.R;

/**
 * Created by juanlabrador on 18/08/15.
 */
public class OptionsListAdapter extends ArrayAdapter<String> {

    private int[] mIcons;
    private String[] mContents;

    public OptionsListAdapter(Context context, int[] icons, String[] contents) {
        super(context, -1, contents);

        mIcons = icons;
        mContents = contents;
    }

    public static class ViewHolder {
        ImageView mIcon;
        TextView mContent;

        public ViewHolder(View view) {
            mIcon = (ImageView) view.findViewById(R.id.icon_option);
            mContent = (TextView) view.findViewById(R.id.name_option);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.custom_item_list_options, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.mIcon.setImageResource(mIcons[position]);
        viewHolder.mContent.setText(mContents[position]);

        return convertView;
    }
}