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

import org.joda.time.LocalDate;
import org.joda.time.Period;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import domains.Child;

/**
 * Created by juanlabrador on 31/08/15.
 */
public class ChildrenListAdapter extends ArrayAdapter<Child> {

    private SimpleDateFormat mFormat;
    private Context mContext;

    public ChildrenListAdapter(Context context, List<Child> childrenEdits) {
        super(context, R.layout.custom_item_menu_list_children, childrenEdits);
        mContext = context;
        mFormat = new SimpleDateFormat(context.getString(R.string.format_date_service));
    }

    public static class ViewHolder {
        CircleImageView mPhoto;
        TextView mName;
        TextView mAge;

        public ViewHolder(View view) {
            mPhoto = (CircleImageView) view.findViewById(R.id.item_baby_photo);
            mName = (TextView) view.findViewById(R.id.item_baby_name);
            mAge = (TextView) view.findViewById(R.id.item_baby_age);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Child mChild = getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.custom_item_menu_list_children, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (mChild.getImage() != null)
            viewHolder.mPhoto.setImageBitmap(getImageFromBytes(mChild.getImage()));
        else viewHolder.mPhoto.setImageResource(R.mipmap.icn_children_default);

        viewHolder.mName.setText(mChild.getName());
        viewHolder.mAge.setText(calculateAge(mChild.getDob()));

        return convertView;
    }

    public Bitmap getImageFromBytes(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    /**
     * Method for calculate age child in item list
     * @param date
     * @return
     */
    private String calculateAge(String date) {
        Calendar mDate = Calendar.getInstance();
        try {
            mDate.setTime(mFormat.parse(date));
            mDate.add(Calendar.MONTH, 1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        LocalDate mBirth;
        if (mDate.get(Calendar.MONTH) != 0)
            mBirth = new LocalDate(mDate.get(Calendar.YEAR), mDate.get(Calendar.MONTH), mDate.get(Calendar.DAY_OF_MONTH));
        else {
            mDate.add(Calendar.YEAR, -1);
            mBirth = new LocalDate(mDate.get(Calendar.YEAR), 12, mDate.get(Calendar.DAY_OF_MONTH));
        }

        LocalDate mNow = new LocalDate();
        Period mPeriod = new Period(mBirth, mNow);

        if (mPeriod.getYears() >= 1)
            return mPeriod.getYears() + " " +
                    mContext.getString(R.string.children_age_year) + " " +
                    mPeriod.getMonths() +
                    mContext.getString(R.string.children_age_month) + " " +
                    mPeriod.getDays() +
                    mContext.getString(R.string.children_age_days);
        else
            return  mPeriod.getMonths() + " " +
                    mContext.getString(R.string.children_age_months) + " " +
                    mPeriod.getDays() +
                    mContext.getString(R.string.children_age_days);
    }
}