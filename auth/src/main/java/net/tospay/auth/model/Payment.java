package net.tospay.auth.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Payment implements Parcelable {

    @SerializedName("amount")
    @Expose
    private Double amount;

    @SerializedName("currency")
    @Expose
    private String currency;

    @SerializedName("callback")
    @Expose
    private String callback;

    public Payment() {
    }

    protected Payment(Parcel in) {
        if (in.readByte() == 0) {
            amount = null;
        } else {
            amount = in.readDouble();
        }
        currency = in.readString();
        callback = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (amount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(amount);
        }
        dest.writeString(currency);
        dest.writeString(callback);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Payment> CREATOR = new Creator<Payment>() {
        @Override
        public Payment createFromParcel(Parcel in) {
            return new Payment(in);
        }

        @Override
        public Payment[] newArray(int size) {
            return new Payment[size];
        }
    };

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }
}
