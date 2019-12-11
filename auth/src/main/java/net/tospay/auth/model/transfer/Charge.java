package net.tospay.auth.model.transfer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Charge implements Parcelable {

    @SerializedName("amount")
    @Expose
    private Amount amount;

    protected Charge(Parcel in) {
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

    public static final Creator<Charge> CREATOR = new Creator<Charge>() {
        @Override
        public Charge createFromParcel(Parcel in) {
            return new Charge(in);
        }

        @Override
        public Charge[] newArray(int size) {
            return new Charge[size];
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
        return "Charge{" +
                "amount=" + amount +
                '}';
    }
}
