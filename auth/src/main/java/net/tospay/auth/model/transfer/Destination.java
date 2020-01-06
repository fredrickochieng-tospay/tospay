package net.tospay.auth.model.transfer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Destination implements Parcelable {

    @SerializedName("amount")
    @Expose
    private Amount amount;

    public Destination() {
    }

    public Destination(Amount amount) {
        this.amount = amount;
    }

    protected Destination(Parcel in) {
        amount = in.readParcelable(Amount.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(amount, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Destination> CREATOR = new Creator<Destination>() {
        @Override
        public Destination createFromParcel(Parcel in) {
            return new Destination(in);
        }

        @Override
        public Destination[] newArray(int size) {
            return new Destination[size];
        }
    };

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }
}
