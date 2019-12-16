package net.tospay.auth.model.transfer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderInfo implements Parcelable{

    @SerializedName("amount")
    @Expose
    private Amount amount;

    @SerializedName("reference")
    @Expose
    private String reference;

    @SerializedName("description")
    @Expose
    private String description;

    public OrderInfo() {
    }

    public OrderInfo(Amount amount, String reference) {
        this.amount = amount;
        this.reference = reference;
    }

    public OrderInfo(Amount amount, String reference, String description) {
        this.amount = amount;
        this.reference = reference;
        this.description = description;
    }

    protected OrderInfo(Parcel in) {
        amount = in.readParcelable(Amount.class.getClassLoader());
        reference = in.readString();
        description = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(amount, flags);
        dest.writeString(reference);
        dest.writeString(description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderInfo> CREATOR = new Creator<OrderInfo>() {
        @Override
        public OrderInfo createFromParcel(Parcel in) {
            return new OrderInfo(in);
        }

        @Override
        public OrderInfo[] newArray(int size) {
            return new OrderInfo[size];
        }
    };

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
