package domains;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by juanlabrador on 01/09/15.
 */
public class Vaccine implements Parcelable {

    private int value;
    private String name;
    private int maxDose;
    private boolean optional;
    private int minAge;
    private boolean repeating;
    private String detail;

    public Vaccine(int value, String name, int maxDose, boolean optional, int minAge, boolean repeating, String detail) {
        this.value = value;
        this.name = name;
        this.maxDose = maxDose;
        this.optional = optional;
        this.minAge = minAge;
        this.repeating = repeating;
        this.detail = detail;
    }

    protected Vaccine(Parcel in) {
        value = in.readInt();
        name = in.readString();
        maxDose = in.readInt();
        minAge = in.readInt();
        detail = in.readString();
    }



    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxDose() {
        return maxDose;
    }

    public void setMaxDose(int maxDose) {
        this.maxDose = maxDose;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public boolean isRepeating() {
        return repeating;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(value);
        dest.writeString(name);
        dest.writeInt(maxDose);
        dest.writeInt(minAge);
        dest.writeString(detail);
    }

    public static final Creator<Vaccine> CREATOR = new Creator<Vaccine>() {
        @Override
        public Vaccine createFromParcel(Parcel in) {
            return new Vaccine(in);
        }

        @Override
        public Vaccine[] newArray(int size) {
            return new Vaccine[size];
        }
    };
}
