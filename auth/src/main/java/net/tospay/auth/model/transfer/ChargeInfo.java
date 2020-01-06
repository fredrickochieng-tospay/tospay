package net.tospay.auth.model.transfer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChargeInfo implements Parcelable {

    @SerializedName("source")
    @Expose
    private Source source;

    @SerializedName("destination")
    @Expose
    private Destination destination;

    public ChargeInfo() {
    }

    protected ChargeInfo(Parcel in) {
        source = in.readParcelable(Source.class.getClassLoader());
        destination = in.readParcelable(Destination.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(source, flags);
        dest.writeParcelable(destination, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ChargeInfo> CREATOR = new Creator<ChargeInfo>() {
        @Override
        public ChargeInfo createFromParcel(Parcel in) {
            return new ChargeInfo(in);
        }

        @Override
        public ChargeInfo[] newArray(int size) {
            return new ChargeInfo[size];
        }
    };

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    @Override
    public String toString() {
        return "ChargeInfo{" +
                "source=" + source +
                ", destination=" + destination +
                '}';
    }
}
