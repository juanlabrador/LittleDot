package adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.littledot.mystxx.littledot.BaseFeedActivity;
import com.littledot.mystxx.littledot.DiapersAddEntryActivity;
import com.littledot.mystxx.littledot.FeedingAddEntryActivity;
import com.littledot.mystxx.littledot.GrowthAddEntryActivity;
import com.littledot.mystxx.littledot.MedicineAddEntryActivity;
import com.littledot.mystxx.littledot.MilestoneEditEntryActivity;
import com.littledot.mystxx.littledot.OthersAddEntryActivity;
import com.littledot.mystxx.littledot.ParentFeedActivity;
import com.littledot.mystxx.littledot.R;
import com.littledot.mystxx.littledot.SicknessAddEntryActivity;
import com.littledot.mystxx.littledot.SleepingAddEntryActivity;
import com.littledot.mystxx.littledot.TeethEditEntryActivity;
import com.littledot.mystxx.littledot.TemperatureAddEntryActivity;
import com.littledot.mystxx.littledot.VaccineEditEntryActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import domains.Conversion;
import domains.DotCategory;
import domains.DotValues;
import domains.Image;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import services.UserServices;

/**
 * Created by juanlabrador on 22/08/15.
 */
public class FeedListAdapter extends ArrayAdapter<DotCategory> {

    public final static String API = " http://littledotapp.com/api/";
    private final String[] mMilestoneRes, mTeethRes, mVaccineRes, mDiapersRes;
    private static final int HEADER = 0;
    private static final int ITEM_WITHOUT_PHOTOS = 1;
    private static final int ITEM_WITH_PHOTOS = 2;
    private SimpleDateFormat mFormatDate;
    private SimpleDateFormat mFormatTime;
    private List<DotCategory> mDotCategories;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private SharedPreferences mPreferences;
    private Conversion mConversion;

    public FeedListAdapter(Context context, List<DotCategory> dotCategories) {
        super(context, -1, dotCategories);
        mDotCategories = dotCategories;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(getContext());
        mFormatDate = new SimpleDateFormat(context.getString(R.string.format_date_service));
        mFormatTime = new SimpleDateFormat(context.getString(R.string.format_time));

        // Arrays
        mMilestoneRes = context.getResources().getStringArray(R.array.milestone_list);
        mTeethRes = context.getResources().getStringArray(R.array.teeth_list);
        mVaccineRes = context.getResources().getStringArray(R.array.vaccine_list);
        mDiapersRes = context.getResources().getStringArray(R.array.diapers_poo_list);
        mPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        mConversion = new Conversion();
    }

    public class ItemWithoutPhotos {
        ImageView mIcon;
        TextView mContent;
        TextView mTime;
        ImageView mEditButton;

        public ItemWithoutPhotos(View view) {
            mIcon = (ImageView) view.findViewById(R.id.icon_feed);
            mContent = (TextView) view.findViewById(R.id.content_item_feed);
            mTime = (TextView) view.findViewById(R.id.time_item);
            mEditButton = (ImageView) view.findViewById(R.id.icon_edit);

            mEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCustomOptions((DotCategory) mEditButton.getTag());
                }
            });
        }
    }

    public class ItemWithPhotos {
        ImageView mIcon;
        TextView mContent;
        TextView mTime;
        ImageView mEditButton;
        LinearLayout mGallery;
        TextView mTagPhotos;

        public ItemWithPhotos(View view) {
            mIcon = (ImageView) view.findViewById(R.id.icon_feed);
            mContent = (TextView) view.findViewById(R.id.content_item_feed);
            mTime = (TextView) view.findViewById(R.id.time_item);
            mEditButton = (ImageView) view.findViewById(R.id.icon_edit);
            mGallery = (LinearLayout) view.findViewById(R.id.gallery_item);
            mTagPhotos = (TextView) view.findViewById(R.id.tag_photos);

            mEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCustomOptions((DotCategory) mEditButton.getTag());
                }
            });
        }

        public void addGallery(Bitmap image) {
            final ImageView mPhoto = new ImageView(getContext());
            mPhoto.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
            mPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mPhoto.setPadding(8, 0, 8, 0);
            mPhoto.setImageBitmap(image);
            mPhoto.setTag(image);

            mPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showImage((Bitmap) mPhoto.getTag());
                }
            });

            mGallery.addView(mPhoto);
        }
    }

    public class Header {
        TextView mTime;

        public Header(View view) {
            mTime = (TextView) view.findViewById(R.id.content_time);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        DotCategory mDotCategory = getItem(position);

        switch (getItemViewType(position)) {
            case HEADER:
                /**
                 * Here we inflate the header view where will go the date
                 * */
                Header mHeader;
                if (convertView == null) {
                    convertView = mLayoutInflater.inflate(R.layout.custom_item_feed_time, parent, false);
                    mHeader = new Header(convertView);
                    convertView.setTag(mHeader);
                } else {
                    mHeader = (Header) convertView.getTag();
                }
                mHeader.mTime.setText(mDotCategory.getDate());
                break;
            case ITEM_WITHOUT_PHOTOS:
                /**
                 * Here we inflate the item view without photos gallery where will go the date
                 * */
                ItemWithoutPhotos mItemWithout;
                if (convertView == null) {
                    convertView = mLayoutInflater.inflate(R.layout.custom_item_feed_without_photos, parent, false);
                    mItemWithout = new ItemWithoutPhotos(convertView);
                    convertView.setTag(mItemWithout);
                } else {
                    mItemWithout = (ItemWithoutPhotos) convertView.getTag();
                }
                buildByCategory(mDotCategory, mItemWithout);
                mItemWithout.mEditButton.setTag(mDotCategory);
                break;
            case ITEM_WITH_PHOTOS:
                /**
                 * Here we inflate the item view with galley photos where will go the date
                 * */
                ItemWithPhotos mItemWith;
                if (convertView == null) {
                    convertView = mLayoutInflater.inflate(R.layout.custom_item_feed_with_photos, parent, false);
                    mItemWith = new ItemWithPhotos(convertView);
                    convertView.setTag(mItemWith);
                } else {
                    mItemWith = (ItemWithPhotos) convertView.getTag();
                }
                buildByCategory(mDotCategory, mItemWith);
                mItemWith.mEditButton.setTag(mDotCategory);
                break;
        }
        return convertView;
    }

    /**
     * Here we search a determinate view by condition
     */

    @Override
    public int getItemViewType(int position) {
        if (mDotCategories.get(position).isHeader()) {
            return HEADER;
        } else if (mDotCategories.get(position).isHavePhoto()) {
            return ITEM_WITH_PHOTOS;
        } else {
            return ITEM_WITHOUT_PHOTOS;
        }
    }

    /**
     * Without photos
     * @param dotCategory
     * @param view
     */

    private void buildByCategory(DotCategory dotCategory, ItemWithoutPhotos view) {
        // Formatting date to time.
        Date mTime = null;
        try {
            mTime = mFormatDate.parse(dotCategory.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<DotValues> dotValues = getListDotFromJSONArray(dotCategory.getEntryData());
        switch (dotCategory.getCategoryId()){
            case 0: // Feeding Feed
                view.mIcon.setImageResource(R.mipmap.icn_feed_feeding);
                switch (dotValues.get(0).getValue()) {
                    case 0:
                        if (mPreferences.getInt("unit", 0) == 0)
                            view.mContent.setText(((int) dotValues.get(0).getDetailValue()) + " ml");
                        else
                            view.mContent.setText(mConversion.mlToFl((double) dotValues.get(0).getDetailValue()) + " fl oz");
                        break;
                    case 1:
                        view.mContent.setText(String.format(
                                mContext.getString(R.string.feeding_feed_item_left),
                                (int) dotValues.get(0).getDetailValue()));
                        break;
                    case 2:
                        view.mContent.setText(String.format(
                                mContext.getString(R.string.feeding_feed_item_right),
                                (int) dotValues.get(0).getDetailValue()));
                        break;
                    case 3:
                        view.mContent.setText(dotValues.get(0).getText());
                        break;
                }
                view.mTime.setText(mFormatTime.format(mTime));
                break;
            case 1:
                view.mIcon.setImageResource(R.mipmap.icn_feed_sleeping);
                view.mContent.setText(dotValues.get(0).getDetailValue() + " h");
                view.mTime.setText(mFormatTime.format(mTime));
                break;
            case 2:
                view.mIcon.setImageResource(R.mipmap.icn_feed_diapers);
                if (dotValues.size() > 1)
                    view.mContent.setText(mContext.getString(R.string.diapers_pee) + ", " +
                            mDiapersRes[dotValues.get(1).getValue()]);
                else
                    if (dotValues.get(0).getGroup() == 0)
                        view.mContent.setText(mContext.getString(R.string.diapers_pee));
                    else
                        view.mContent.setText(mDiapersRes[dotValues.get(0).getValue()]);
                view.mTime.setText(mFormatTime.format(mTime));
                break;
            case 3:
                view.mIcon.setImageResource(R.mipmap.icn_feed_sickness);
                view.mContent.setText(dotCategory.getNote());
                view.mTime.setText(mFormatTime.format(mTime));
                break;
            case 4: //Temperature Feed
                view.mIcon.setImageResource(R.mipmap.icn_feed_temperature);
                if (mPreferences.getInt("unit", 0) == 0)
                    view.mContent.setText(dotValues.get(0).getDetailValue() + " °C");
                else
                    view.mContent.setText(mConversion.celsiusToFahrenheit((double) dotValues.get(0).getDetailValue()) + " °F");
                view.mTime.setText(mFormatTime.format(mTime));
                break;
            case 5:
                view.mIcon.setImageResource(R.mipmap.icn_feed_medicines);
                view.mContent.setText(dotCategory.getNote());
                view.mTime.setText(mFormatTime.format(mTime));
                break;
            case 6:
                view.mIcon.setImageResource(R.mipmap.icn_feed_growth);

                if (mPreferences.getInt("unit", 0) == 0){
                    if (dotValues.size() > 2)
                        view.mContent.setText(mContext.getString(R.string.growth_weight) + " "
                                + dotValues.get(0).getDetailValue() + " kg, "
                                + mContext.getString(R.string.growth_height) + " "
                                + dotValues.get(1).getDetailValue() + " cm, "
                                + mContext.getString(R.string.growth_head) +" "
                                + dotValues.get(2).getDetailValue() + " cm");
                    else
                        view.mContent.setText(mContext.getString(R.string.growth_weight) + " "
                                + dotValues.get(0).getDetailValue() + " kg, "
                                + mContext.getString(R.string.growth_height) + " "
                                + dotValues.get(1).getDetailValue() + " cm");
                } else {
                    if (dotValues.size() > 2)
                        view.mContent.setText(mContext.getString(R.string.growth_weight) + " "
                                + mConversion.kgToPounds((double) dotValues.get(0).getDetailValue()) + " lb, "
                                + mContext.getString(R.string.growth_height) + " "
                                + mConversion.cmToInches((double) dotValues.get(1).getDetailValue()) + " inch, "
                                + mContext.getString(R.string.growth_head) +" "
                                + mConversion.cmToInches((double) dotValues.get(2).getDetailValue()) + " inch");
                    else
                        view.mContent.setText(mContext.getString(R.string.growth_weight) + " "
                                + mConversion.kgToPounds((double) dotValues.get(0).getDetailValue()) + " lb, "
                                + mContext.getString(R.string.growth_height) + " "
                                + mConversion.cmToInches((double) dotValues.get(1).getDetailValue()) + " inch");
                }
                view.mTime.setText(mFormatTime.format(mTime));
                break;
            case 7:
                if (dotValues.get(0).getSkipped() != 0)
                    view.mIcon.setImageResource(R.mipmap.icn_vaccines_skipped);
                else
                    view.mIcon.setImageResource(R.mipmap.icn_feed_vaccines);
                view.mContent.setText(
                        String.format(mVaccineRes[dotValues.get(0).getValue()],
                                Math.round(dotValues.get(0).getDetailValue())));
                //view.mTime.setText(mFormatTime.format(mTime));
                view.mTime.setVisibility(View.GONE);
                break;
            case 8: // Milestone Feed
                view.mIcon.setImageResource(R.mipmap.icn_feed_milestones);
                view.mContent.setText(mMilestoneRes[dotValues.get(0).getValue()]);
                view.mTime.setText(mFormatTime.format(mTime));
                break;
            case 9: // Teeth Feed
                view.mIcon.setImageResource(R.mipmap.icn_feed_teeth);
                view.mContent.setText(mTeethRes[dotValues.get(0).getValue()]);
                //view.mTime.setText(mFormatTime.format(mTime));
                view.mTime.setVisibility(View.GONE);
                break;
            case 10: // Other Feed
                view.mIcon.setImageResource(R.mipmap.icn_feed_other);
                view.mContent.setText(dotCategory.getNote());
                view.mTime.setText(mFormatTime.format(mTime));
                break;
        }
    }

    /**
     * With photos
     * @param dotCategory
     * @param view
     */

    private void buildByCategory(DotCategory dotCategory, ItemWithPhotos view) {
        // Formatting date to time.
        Date mTime = null;
        try {
            mTime = mFormatDate.parse(dotCategory.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Add images
        view.mGallery.removeAllViews();
        for (Image img : dotCategory.getImages())
            view.addGallery(getImageFromBytes(img.getBytes()));

        view.mTagPhotos.setText(dotCategory.getTag());

        List<DotValues> dotValues = getListDotFromJSONArray(dotCategory.getEntryData());
        switch (dotCategory.getCategoryId()){
            case 0: // Feeding Feed
                view.mIcon.setImageResource(R.mipmap.icn_feed_feeding);
                switch (dotValues.get(0).getValue()) {
                    case 0:
                        if (mPreferences.getInt("unit", 0) == 0)
                            view.mContent.setText(((int) dotValues.get(0).getDetailValue()) + " ml");
                        else
                            view.mContent.setText(mConversion.mlToFl((double) dotValues.get(0).getDetailValue()) + " fl oz");
                        break;
                    case 1:
                        view.mContent.setText(String.format(
                                mContext.getString(R.string.feeding_feed_item_left),
                                (int) dotValues.get(0).getDetailValue()));
                        break;
                    case 2:
                        view.mContent.setText(String.format(
                                mContext.getString(R.string.feeding_feed_item_right),
                                (int) dotValues.get(0).getDetailValue()));
                        break;
                    case 3:
                        view.mContent.setText(dotValues.get(0).getText());
                        break;
                }
                view.mTime.setText(mFormatTime.format(mTime));
                break;
            case 1:
                view.mIcon.setImageResource(R.mipmap.icn_feed_sleeping);
                view.mContent.setText(dotValues.get(0).getDetailValue() + " h");
                view.mTime.setText(mFormatTime.format(mTime));
                break;
            case 2:
                view.mIcon.setImageResource(R.mipmap.icn_feed_diapers);
                if (dotValues.size() > 1)
                    view.mContent.setText(mContext.getString(R.string.diapers_pee) + ", " +
                            mDiapersRes[dotValues.get(1).getValue()]);
                else
                if (dotValues.get(0).getGroup() == 0)
                    view.mContent.setText(mContext.getString(R.string.diapers_pee));
                else
                    view.mContent.setText(mDiapersRes[dotValues.get(0).getValue()]);
                view.mTime.setText(mFormatTime.format(mTime));
                break;
            case 3:
                view.mIcon.setImageResource(R.mipmap.icn_feed_sickness);
                view.mContent.setText(dotCategory.getNote());
                view.mTime.setText(mFormatTime.format(mTime));
                break;
            case 4: // Temperature Feed
                view.mIcon.setImageResource(R.mipmap.icn_feed_temperature);
                if (mPreferences.getInt("unit", 0) == 0)
                    view.mContent.setText(dotValues.get(0).getDetailValue() + " °C");
                else
                    view.mContent.setText(mConversion.celsiusToFahrenheit((double) dotValues.get(0).getDetailValue()) + " °F");

                view.mTime.setText(mFormatTime.format(mTime));
                break;
            case 5:
                view.mIcon.setImageResource(R.mipmap.icn_feed_medicines);
                view.mContent.setText(dotCategory.getNote());
                view.mTime.setText(mFormatTime.format(mTime));
                break;
            case 6:
                view.mIcon.setImageResource(R.mipmap.icn_feed_growth);
                if (mPreferences.getInt("unit", 0) == 0){
                    if (dotValues.size() > 2)
                        view.mContent.setText(mContext.getString(R.string.growth_weight) + " "
                                + dotValues.get(0).getDetailValue() + " kg, "
                                + mContext.getString(R.string.growth_height) + " "
                                + dotValues.get(1).getDetailValue() + " cm, "
                                + mContext.getString(R.string.growth_head) +" "
                                + dotValues.get(2).getDetailValue() + " cm");
                    else
                        view.mContent.setText(mContext.getString(R.string.growth_weight) + " "
                                + dotValues.get(0).getDetailValue() + " kg, "
                                + mContext.getString(R.string.growth_height) + " "
                                + dotValues.get(1).getDetailValue() + " cm");
                } else {
                    if (dotValues.size() > 2)
                        view.mContent.setText(mContext.getString(R.string.growth_weight) + " "
                                + mConversion.kgToPounds((double) dotValues.get(0).getDetailValue()) + " lb, "
                                + mContext.getString(R.string.growth_height) + " "
                                + mConversion.cmToInches((double) dotValues.get(1).getDetailValue()) + " inch, "
                                + mContext.getString(R.string.growth_head) +" "
                                + mConversion.cmToInches((double) dotValues.get(2).getDetailValue()) + " inch");
                    else
                        view.mContent.setText(mContext.getString(R.string.growth_weight) + " "
                                + mConversion.kgToPounds((double) dotValues.get(0).getDetailValue()) + " lb, "
                                + mContext.getString(R.string.growth_height) + " "
                                + mConversion.cmToInches((double) dotValues.get(1).getDetailValue()) + " inch");
                }
                view.mTime.setText(mFormatTime.format(mTime));
                break;
            case 7:
                if (dotValues.get(0).getSkipped() != 0)
                    view.mIcon.setImageResource(R.mipmap.icn_vaccines_skipped);
                else
                    view.mIcon.setImageResource(R.mipmap.icn_feed_vaccines);
                view.mContent.setText(
                        String.format(mVaccineRes[dotValues.get(0).getValue()],
                                Math.round(dotValues.get(0).getDetailValue())));
                //view.mTime.setText(mFormatTime.format(mTime));
                view.mTime.setVisibility(View.GONE);
                break;
            case 8: // Milestone Feed
                view.mIcon.setImageResource(R.mipmap.icn_feed_milestones);
                view.mContent.setText(mMilestoneRes[dotValues.get(0).getValue()]);
                view.mTime.setText(mFormatTime.format(mTime));
                break;
            case 9: // Teeth Feed
                view.mIcon.setImageResource(R.mipmap.icn_feed_teeth);
                view.mContent.setText(mTeethRes[dotValues.get(0).getValue()]);
                //view.mTime.setText(mFormatTime.format(mTime));
                view.mTime.setVisibility(View.GONE);
                break;
            case 10: // Other Feed
                view.mIcon.setImageResource(R.mipmap.icn_feed_other);
                view.mContent.setText(dotCategory.getNote());
                view.mTime.setText(mFormatTime.format(mTime));
                break;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }


    public Bitmap getImageFromBytes(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    /**
     * Show dialog for edit entry
     * @param dotCategory
     */
    private void showCustomOptions(final DotCategory dotCategory) {
        final int category = dotCategory.getCategoryId();
        final String[] options;
        if (category != 7) {
            options = new String[]{
                    mContext.getString(R.string.option_delete),
                    mContext.getString(R.string.option_edit)};
        } else {
            options = new String[]{
                    mContext.getString(R.string.option_delete),
                    mContext.getString(R.string.option_edit),
                    mContext.getString(R.string.option_show)};

        }
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        //if (haveNetworkConnection()) {
                        //    List<String> idDot = new ArrayList<>();
                        //    idDot.add(dotCategory.getId());
                            //new SyncDeleteEntries(dotCategory, category).execute(idDot);
                        //} else {
                            ((ParentFeedActivity) mContext).addPendingDot(dotCategory.getId());
                            ((ParentFeedActivity) mContext).deleteDot(dotCategory);
                            //if (mContext instanceof BaseFeedActivity) {
                                //mDots = ((BaseFeedActivity) mContext).getDotsByCategory(category);
                                ((ParentFeedActivity) mContext).updateDataEvery(null, category);
                            //} else {
                                //mDots = ((MainFeedActivity) mContext).getDots();
                                //((ParentFeedActivity) mContext).updateDataMain(null);
                            //}
                        //}
                        notifyDataSetChanged();
                        break;
                    case 1:
                        startAddEntryByCategory(dotCategory);
                        break;
                    case 2:
                        showDetailsVaccine(dotCategory);
                        break;
                }
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    /**
     * Open activity for edit entry
     * @param dotCategory
     */
    private void startAddEntryByCategory(DotCategory dotCategory) {
        switch (dotCategory.getCategoryId()) {
            case 0:
                mContext.startActivity(new Intent(mContext, FeedingAddEntryActivity.class)
                        .putExtra("dot", dotCategory));
                ((AppCompatActivity) mContext).overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                break;
            case 1:
                mContext.startActivity(new Intent(mContext, SleepingAddEntryActivity.class)
                        .putExtra("dot", dotCategory));
                ((AppCompatActivity) mContext).overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                break;
            case 2:
                mContext.startActivity(new Intent(mContext, DiapersAddEntryActivity.class)
                        .putExtra("dot", dotCategory));
                ((AppCompatActivity) mContext).overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                break;
            case 3:
                mContext.startActivity(new Intent(mContext, SicknessAddEntryActivity.class)
                        .putExtra("dot", dotCategory));
                ((AppCompatActivity) mContext).overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                break;
            case 4: // Temperature Add Screen
                mContext.startActivity(new Intent(mContext, TemperatureAddEntryActivity.class)
                        .putExtra("dot", dotCategory));
                ((AppCompatActivity) mContext).overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                break;
            case 5:
                mContext.startActivity(new Intent(mContext, MedicineAddEntryActivity.class)
                        .putExtra("dot", dotCategory));
                ((AppCompatActivity) mContext).overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                break;
            case 6:
                mContext.startActivity(new Intent(mContext, GrowthAddEntryActivity.class)
                        .putExtra("dot", dotCategory));
                ((AppCompatActivity) mContext).overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                break;
            case 7:
                mContext.startActivity(new Intent(mContext, VaccineEditEntryActivity.class)
                        .putExtra("dot", dotCategory));
                ((AppCompatActivity) mContext).overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                break;
            case 8: // Milestones Add Screen
                mContext.startActivity(new Intent(mContext, MilestoneEditEntryActivity.class)
                        .putExtra("dot", dotCategory));
                ((AppCompatActivity) mContext).overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                break;
            case 9:
                mContext.startActivity(new Intent(mContext, TeethEditEntryActivity.class)
                        .putExtra("dot", dotCategory));
                ((AppCompatActivity) mContext).overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                break;
            case 10:
                mContext.startActivity(new Intent(mContext, OthersAddEntryActivity.class)
                        .putExtra("dot", dotCategory));
                ((AppCompatActivity) mContext).overridePendingTransition(R.anim.anim_in, R.anim.anim_nothing);
                break;
        }
    }

    /**
     * get List Dot from JSONArray
     * @param array
     * @return
     */
    protected List<DotValues> getListDotFromJSONArray(JSONArray array) {
        List<DotValues> dotValues = new ArrayList<>();
        Gson gson = new Gson();
        /*try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject currentDot = (JSONObject) array.get(i);
                DotValues dot = new DotValues();
                if (currentDot.has("group"))
                    dot.setGroup(currentDot.getInt("group"));
                if (currentDot.has("value"))
                    dot.setValue(currentDot.getInt("value"));
                if (currentDot.has("detailValue"))
                    dot.setDetailValue((float) currentDot.getDouble("detailValue"));
                if (currentDot.has("text"))
                    dot.setText(currentDot.getString("text"));
                if (currentDot.has("skipped"))
                    dot.setSkipped(currentDot.getInt("skipped"));
                dotValuesList.add(dotValues);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dotValuesList;*/

        try {
            for (int i = 0; i < array.length(); i++) {
                dotValues.add(gson.fromJson(array.get(i).toString(), DotValues.class));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dotValues;
    }

    /**
     * Show detail of vaccine entry
     * @param dotCategory
     */
    private void showDetailsVaccine(DotCategory dotCategory) {
        AlertDialog.Builder builder;
        DotValues vaccine = getListDotFromJSONArray(dotCategory.getEntryData()).get(0);
        String[] names = mContext.getResources().getStringArray(R.array.vaccine_list_names);
        String[] details = mContext.getResources().getStringArray(R.array.vaccine_list_details);
        int[] ages = mContext.getResources().getIntArray(R.array.min_age_vaccines);
        int[] doses = mContext.getResources().getIntArray(R.array.max_doses_vaccines);

        String name = names[vaccine.getValue()];
        String detail = details[vaccine.getValue()];
        int age = ages[vaccine.getValue()];
        int dose = doses[vaccine.getValue()];

        // Formatting date to time.
        Date date = null;
        try {
            date = mFormatDate.parse(dotCategory.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        View view = ((ParentFeedActivity) mContext)
                .getLayoutInflater().inflate(R.layout.custom_vaccine_item_detail, null, false);

        // Drawing view
        if (vaccine.getSkipped() != 0) {
            builder = new AlertDialog.Builder(mContext, R.style.GrowthStyleDialog);

            view.findViewById(R.id.container_vaccine).setBackgroundColor(
                    mContext.getResources().getColor(R.color.skipped_background));
            ((TextView) view.findViewById(R.id.label_vaccine)).setText(R.string.vaccine_skipped);
            ((TextView) view.findViewById(R.id.label_vaccine))
                    .setTextColor(mContext.getResources().getColor(R.color.skipped_text));
            ((ImageView) view.findViewById(R.id.icon_vaccination))
                    .setImageResource(R.mipmap.img_vaccination_skipped);
        } else {
            builder = new AlertDialog.Builder(mContext, R.style.VaccineStyleDialog);

            view.findViewById(R.id.container_vaccine).setBackgroundColor(
                    mContext.getResources().getColor(R.color.done_background));
            ((TextView) view.findViewById(R.id.label_vaccine)).setText(R.string.vaccine_done);
            ((TextView) view.findViewById(R.id.label_vaccine))
                    .setTextColor(mContext.getResources().getColor(R.color.done_text));
            ((ImageView) view.findViewById(R.id.icon_vaccination))
                    .setImageResource(R.mipmap.img_vaccination_done);
        }

        // Set Date
        String mDate = new SimpleDateFormat(mContext.getString(R.string.format_pretty_time)).format(date);
        ((TextView) view.findViewById(R.id.date_vaccine))
                .setText(mDate.substring(0, 1).toUpperCase() +
                        mDate.substring(1).toLowerCase());
        // Age min for vaccinated
        ((TextView) view.findViewById(R.id.age_dose))
                .setText(String.format(mContext.getString(R.string.vaccine_age), age));
        // Number max doses + dose actual
        if (!name.equals(mContext.getString(R.string.vaccine_optional_4)) &&
                !name.equals(mContext.getString(R.string.vaccine_optional_7)))
            ((TextView) view.findViewById(R.id.number_dose))
                    .setText(String.format(mContext.getString(R.string.vaccine_dose),
                            Math.round(vaccine.getDetailValue()),
                            dose));
        else // Recurring
            ((TextView) view.findViewById(R.id.number_dose))
                    .setText(R.string.vaccine_recurring);

        ((TextView) view.findViewById(R.id.vaccine_info_details)).setText(detail);

        builder.setTitle(name);
        builder.setView(view);
        builder.create();
        builder.show();
    }

    /**
     * Show the image when user does click on
     * @param image
     */
    private void showImage(Bitmap image) {
        View view = ((ParentFeedActivity) mContext).
                getLayoutInflater().inflate(R.layout.custom_show_image, null);
        ImageView mImage = (ImageView) view.findViewById(R.id.show_image);
        mImage.setImageBitmap(image);
        AlertDialog.Builder mDialog = new AlertDialog.Builder(mContext);
        mDialog.setView(view);
        mDialog.create();
        mDialog.show();
    }

    public class SyncDeleteEntries extends AsyncTask<List<String>, Void, Boolean> {

        ProgressDialog mProgressDialog;
        DotCategory dotCategory;
        int category;

        public SyncDeleteEntries(DotCategory dotCategory, int category) {
            this.dotCategory = dotCategory;
            this.category = category;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setMessage(mContext.getString(R.string.setting_progress));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            //mProgressDialog.create();
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(List<String>... params) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();

            List<String> entryGuids = new ArrayList<>(params[0]);

            UserServices service = retrofit.create(UserServices.class);

            Call<Object> call = service.syncDeleteEntries(entryGuids);

            try {
                return call.execute().isSuccess();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
            if (s) {
                Toast.makeText(mContext, "ok 200 delete entry from adapter", Toast.LENGTH_SHORT).show();
                ((ParentFeedActivity) mContext).deleteDot(dotCategory);
                ((ParentFeedActivity) mContext).updateDataEvery(null, category);
            } else
                Toast.makeText(mContext, "error delete entry from adapter", Toast.LENGTH_SHORT).show();
            mProgressDialog.dismiss();
        }
    }

    /**
     * If phone have access to internet via WIFI or NETWORK
     * @return true or false
     */
    public boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}