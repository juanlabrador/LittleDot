package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.littledot.mystxx.littledot.R;

import java.util.List;

import domains.Vaccine;

/**
 * Created by juanlabrador on 26/08/15.
 */
public class VaccineListAdapter extends ArrayAdapter<Vaccine> {

    private List<Vaccine> mVaccines;

    public VaccineListAdapter(Context context, List<Vaccine> vaccines) {
        super(context, -1, vaccines);
        mVaccines = vaccines;
    }

    public class ViewHolder {
        TextView mVaccine;
        TextView mDoses;

        public ViewHolder(View view) {
            mVaccine = (TextView) view.findViewById(R.id.vaccine_detail_name);
            mDoses = (TextView) view.findViewById(R.id.vaccine_detail_dose);
        }

        public void setDose(int position) {
            // FLU and Tick-borne are "recurring"
            if (!mVaccines.get(position).isRepeating())
                mDoses.setText(String.format(
                        getContext().getString(R.string.vaccine_detail_dose),
                        mVaccines.get(position).getMaxDose()));
            else
                mDoses.setText(R.string.vaccine_recurring);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder mViewHolder;
        Vaccine vaccine = getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.custom_vaccine_schedule_list_item, parent, false);
            mViewHolder = new ViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.mVaccine.setText(vaccine.getName());
        mViewHolder.setDose(position);
        return convertView;
    }

    @Override
    public Vaccine getItem(int position) {
        return super.getItem(position);
    }
}