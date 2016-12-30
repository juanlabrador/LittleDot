package domains;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Hossam on 8/20/2015.
 */

public class DotCategory implements Parcelable {

    private String id;
    private int categoryId;
    private String dateModified;
    private List<DotValues> entryData;
    private String note;
    private List<Image> images;
    private String tag;

    boolean isHeader = false;
    boolean havePhoto =false;

    private JSONArray mEntryData;

    public DotCategory(){
    }

    public DotCategory(String id, int category, JSONArray jArray, String dateModified, String note) {
        this.id = id;
        this.categoryId = category;
        this.mEntryData = jArray;
        this.dateModified = dateModified;
        this.note = note;
    }

    public DotCategory(String id, int category, String dateModified, String note) {
        this.id = id;
        this.categoryId = category;
        this.mEntryData = new JSONArray();
        this.dateModified = dateModified;
        this.note = note;
    }

    public DotCategory(int category, JSONArray jArray, String dateModified, String note) {
        this.categoryId = category;
        this.mEntryData = jArray;
        this.dateModified = dateModified;
        this.note = note;
    }

    public DotCategory(String date) {
        this(null, -1, null, date, null);
        isHeader = true;
    }

    protected DotCategory(Parcel in) {
        id = in.readString();
        categoryId = in.readInt();
        try {
            mEntryData = new JSONArray(in.readString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dateModified = in.readString();
        note = in.readString();
        images = in.readArrayList(Image.class.getClassLoader());
        tag = in.readString();
    }

    //    get the category id of the entry
    public int getCategoryId() {
        return categoryId;
    }

    //    set the category id of the entry
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    // get the date of the entry
    public String getDate() {
        return dateModified;
    }

    // set the date of the entry
    public void setDate(String dateModified) {
        this.dateModified = dateModified;
    }

    //get entry note
    public String getNote() {
        return note;
    }

    //set entry note
    public void setNote(String note) {
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEntryData(List<DotValues> entryData) {
        this.entryData = entryData;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        havePhoto = true;
        this.images = images;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public JSONArray getEntryData() {
        return mEntryData;
    }


    public void setEntryData(JSONArray mEntryData) {
        this.mEntryData = mEntryData;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public boolean isHavePhoto() {
        return havePhoto;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(categoryId);
        dest.writeString(String.valueOf(mEntryData));
        dest.writeString(dateModified);
        dest.writeString(note);
        dest.writeList(images);
        dest.writeString(tag);
    }

    public static final Creator<DotCategory> CREATOR = new Creator<DotCategory>() {
        @Override
        public DotCategory createFromParcel(Parcel in) {
            return new DotCategory(in);
        }

        @Override
        public DotCategory[] newArray(int size) {
            return new DotCategory[size];
        }
    };
}
