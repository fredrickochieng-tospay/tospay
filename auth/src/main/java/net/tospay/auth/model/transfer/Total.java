package net.tospay.auth.model.transfer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Total implements Parcelable {

    @SerializedName("amount")
    @Expose
    private Amount amount;

    public Total() {
    }

    public Total(Amount amount) {
        this.amount = amount;
    }

    protected Total(Parcel in) {
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

    public static final Creator<Total> CREATOR = new Creator<Total>() {
        @Override
        public Total createFromParcel(Parcel in) {
            return new Total(in);
        }

        @Override
        public Total[] newArray(int size) {
            return new Total[size];
        }
    };

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Total{" +
                "amount=" + amount +
                '}';
    }
}
