package net.tospay.auth.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class PaymentTransaction implements Parcelable {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("amount")
    @Expose
    private Double amount;

    @SerializedName("currency")
    @Expose
    private String currency;

    @SerializedName("callback")
    @Expose
    private String callback;

    @SerializedName("payment_expiry")
    @Expose
    private Date paymentExpiry;

    @SerializedName("reason")
    @Expose
    private String reason;

    @SerializedName("external_reference")
    @Expose
    private String externalReference;

    @SerializedName("status")
    @Expose
    private String status;

    public PaymentTransaction() {
    }

    protected PaymentTransaction(Parcel in) {
        id = in.readString();
        if (in.readByte() == 0) {
            amount = null;
        } else {
            amount = in.readDouble();
        }
        currency = in.readString();
        callback = in.readString();
        reason = in.readString();
        externalReference = in.readString();
        status = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        if (amount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(amount);
        }
        dest.writeString(currency);
        dest.writeString(callback);
        dest.writeString(reason);
        dest.writeString(externalReference);
        dest.writeString(status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PaymentTransaction> CREATOR = new Creator<PaymentTransaction>() {
        @Override
        public PaymentTransaction createFromParcel(Parcel in) {
            return new PaymentTransaction(in);
        }

        @Override
        public PaymentTransaction[] newArray(int size) {
            return new PaymentTransaction[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public Date getPaymentExpiry() {
        return paymentExpiry;
    }

    public void setPaymentExpiry(Date paymentExpiry) {
        this.paymentExpiry = paymentExpiry;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "PaymentTransaction{" +
                "id='" + id + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", callback='" + callback + '\'' +
                ", paymentExpiry=" + paymentExpiry +
                ", reason='" + reason + '\'' +
                ", externalReference='" + externalReference + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
