package domains;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Hossam on 8/21/2015.
 */
public class Child implements Parcelable {

    private String guid;
    private String name;
    private String birthDate;
    private int gender;
    private byte[] image;


    public Child(String id, String name, String birthDate, int sex, byte[] image) {
        this.guid = id;
        this.name = name;
        this.birthDate = birthDate;
        this.gender = sex;
        this.image = image;
    }

    public Child() {

    }

    public Child(Parcel in) {
        setID(in.readString());
        setName(in.readString());
        setDob(in.readString());
        setSex(in.readInt());
        setImage(in.createByteArray());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return birthDate;
    }

    public void setDob(String dob) {
        this.birthDate = dob;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getID() {
        return guid;
    }

    public void setID(String ID) {
        this.guid = ID;
    }

    public int getSex() {
        return gender;
    }

    public void setSex(int mSex) {
        this.gender = mSex;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(guid);
        out.writeString(name);
        out.writeString(birthDate);
        out.writeInt(gender);
        out.writeByteArray(image);
    }

    public static final Parcelable.Creator<Child> CREATOR = new Parcelable.Creator<Child>() {
        @Override
        public Child createFromParcel(Parcel in) {
            return new Child(in);
        }

        @Override
        public Child[] newArray(int size) {
            return new Child[size];
        }
    };
}
