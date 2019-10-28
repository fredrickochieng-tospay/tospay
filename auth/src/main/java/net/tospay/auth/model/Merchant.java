package net.tospay.auth.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Merchant implements Parcelable {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("avatar")
    @Expose
    private String avatar;

    public Merchant() {
    }

    protected Merchant(Parcel in) {
        name = in.readString();
        avatar = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(avatar);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Merchant> CREATOR = new Creator<Merchant>() {
        @Override
        public Merchant createFromParcel(Parcel in) {
            return new Merchant(in);
        }

        @Override
        public Merchant[] newArray(int size) {
            return new Merchant[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
