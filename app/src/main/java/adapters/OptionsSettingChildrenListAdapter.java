package adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.littledot.mystxx.littledot.R;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import domains.Child;

/**
 * Created by juanlabrador on 18/08/15.
 */
public class OptionsSettingChildrenListAdapter extends ArrayAdapter<Child> {

    private Context mContext;

    public OptionsSettingChildrenListAdapter(Context context, List<Child> childrenEdits) {
        super(context, R.layout.custom_item_more_options_list_children, childrenEdits);
        mContext = context;
    }

    public static class ViewHolder {
        CircleImageView mPhoto;
        TextView mTextProfile;

        public ViewHolder(View view) {
            mPhoto = (CircleImageView) view.findViewById(R.id.image_baby);
            mTextProfile = (TextView) view.findViewById(R.id.name_profile_baby);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Child mChildrenEdit = getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.custom_item_more_options_list_children, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (mChildrenEdit.getImage() != null)
            viewHolder.mPhoto.setImageBitmap(getImageFromBytes(mChildrenEdit.getImage()));
        else viewHolder.mPhoto.setImageResource(R.mipmap.icn_children_default);

        viewHolder.mTextProfile.setText(String.format(
                mContext.getString(R.string.setting_list_children_item),
                mChildrenEdit.getName()));

        return convertView;
    }

    public Bitmap getImageFromBytes(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}