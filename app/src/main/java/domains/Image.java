package domains;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by juanlabrador on 02/09/15.
 */
public class Image implements Parcelable {

    private String id;
    private String idDot;
    private byte[] bytes;
    private String url;

    public Image() {
    }

    public Image(String id, String idDot, byte[] bytes, String url) {
        this.id = id;
        this.idDot = idDot;
        this.bytes = bytes;
        this.url = url;
    }

    protected Image(Parcel in) {
        id = in.readString();
        idDot = in.readString();
        bytes = in.createByteArray();
        url = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public String getIdDot() {
        return idDot;
    }

    public void setIdDot(String idDot) {
        this.idDot = idDot;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(idDot);
        dest.writeByteArray(bytes);
        dest.writeString(url);
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
}
